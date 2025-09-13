package com.streamerbot.triggers;


import com.streamerbot.DoActionRequest;
import com.streamerbot.domain.*;
import com.streamerbot.util.WorldUtils;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;


import net.runelite.api.*;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.game.NPCManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@Singleton
public class DeathTrigger extends BaseTrigger {

    private static final String ATTACK_OPTION = "Attack";

    private static final String FORTIS_DOOM_MSG = "You have been doomed!";

    /**
     * Checks whether the actor is alive and interacting with the specified player.
     */
    private static final BiPredicate<Player, Actor> INTERACTING;


    private static final Predicate<NPCComposition> NPC_VALID;

    /**
     * Orders NPCs by their likelihood of being our killer.
     */
    private static final BiFunction<NPCManager, Player, Comparator<NPC>> NPC_COMPARATOR;

    /**
     * Orders actors by their likelihood of being the killer of the specified player.
     */
    private static final Function<Player, Comparator<Player>> PK_COMPARATOR;

    @Inject
    private NPCManager npcManager;

    /**
     * Tracks the last {@link Actor} our local player interacted with,
     * for the purposes of attributing deaths to particular {@link Player}'s.
     * <p>
     * Note: this is wrapped in a weak reference to allow garbage collection,
     * for example if the {@link Actor} despawns.
     * As a result, the underlying reference can be null.
     * see identifyKiller
     */
    private WeakReference<Actor> lastTarget = new WeakReference<>(null);

    private boolean isEnabled() {
        return config.deathEnabled() && config.deathActionName() != null && config.deathActionId() != null;
    }

    private String actionName() {
        return config.deathActionName();
    }

    private String actionId() {
        return config.deathActionId();
    }

    private IgnoreSafeDeathPolicy ignoreSafeDeathPolicy() {
        return config.ignoreSafeDeathPolicy();
    }

    private boolean ignoreResurrection() {
        return config.ignoreResurrection();
    }

    private boolean ignorePvp() {
        return config.ignorePvp();
    }

    private boolean safeForGroupHardcore(DeathType deathType) {
        return deathType.equals(DeathType.SAFE_FOR_HCGIM);
    }

    private boolean safeForSoloHardcore(DeathType deathType) {
        return deathType.equals(DeathType.SAFE_FOR_HCGIM) || deathType.equals(DeathType.SAFE_FOR_SOLO_HCIM);
    }

    private boolean shouldIgnoreDeath(boolean byPKer, DeathType deathType) {
        if (ignorePvp() && byPKer) {
            log.debug("ignorePvp() is true and death was by a pker, ignoring death");
            return true;
        }

        if (ignoreResurrection() && deathType == DeathType.RESURRECTION) {
            log.debug("ignoreResurrection() is true and this death was a resurrection, ignoring death");
            return true;
        }

        if(ignoreSafeDeathPolicy().equals(IgnoreSafeDeathPolicy.SAFE_FOR_HCGIM) && safeForGroupHardcore(deathType)) {
            log.debug("Set to ignore HCGIM friendly deaths and this is indeed such a death, ignoring death");
            return true;
        }

        if(ignoreSafeDeathPolicy().equals(IgnoreSafeDeathPolicy.SAFE_FOR_SOLO_HCIM) && safeForSoloHardcore(deathType)) {
            log.debug("Set to ignore Solo HC friendly deaths and this is indeed such a death, ignoring death");
            return true;
        } else {
            return false;
        }
    }

    private DoActionRequest deathRequest(String areaType,
                                         String area,
                                         String deathType,
                                         String killerType,
                                         String killer) {

        String localPlayer = client.getLocalPlayer().getName();
        assert localPlayer != null;

        Map<String, Object> args = Map.of(
                "type", "Death",
                "localPlayer", localPlayer,
                "areaType", areaType,
                "area", area,
                "deathType", deathType,
                "killerType", killerType,
                "killer", killer);

        return new DoActionRequest(actionId(), actionName(), args);
    }

    /**
     * @param localPlayer {@link net.runelite.api.Client#getLocalPlayer()}
     * @param actor       the {@link Actor} that is a candidate killer from {@link #lastTarget}
     * @param pvpEnabled  whether a player could be our killer (e.g., in wilderness)
     * @return whether the specified actor is the likely killer of the local player
     */
    private static boolean checkLastInteraction(Player localPlayer, Actor actor, boolean pvpEnabled) {
        if (!INTERACTING.test(localPlayer, actor))
            return false;

        if (actor instanceof Player) {
            Player other = (Player) actor;
            return pvpEnabled && !other.isClanMember() && !other.isFriend() && !other.isFriendsChatMember();
        }

        if (actor instanceof NPC) {
            NPCComposition npc = ((NPC) actor).getTransformedComposition();
            if (!NPC_VALID.test(npc)) return false;
            assert npc != null;
            return ArrayUtils.contains(npc.getActions(), ATTACK_OPTION);
        }

        log.warn("Encountered unknown type of Actor; was neither Player nor NPC!");
        return false;
    }

    @Nullable
    private Actor identifyKiller(Area area) {
        // must be in wilderness, pvp-world or pvp minigame to be pk'ed
        boolean pvpEnabled = WorldUtils.pvpEnabled(client, area, client.getWorldType());

        Player localPlayer = client.getLocalPlayer();
        Predicate<Actor> interacting = a -> INTERACTING.test(localPlayer, a);

        // O(1) fast path based on last outbound interaction
        Actor lastTarget = this.lastTarget.get();
        if (checkLastInteraction(localPlayer, lastTarget, pvpEnabled))
            return lastTarget;

        // find another player interacting with us (that is preferably not a friend or clan member)
        if (pvpEnabled) {
            Optional<? extends Player> pker = client.getTopLevelWorldView().players().stream()
                    .filter(interacting)
                    .min(PK_COMPARATOR.apply(localPlayer)); // O(n)
            if (pker.isPresent())
                return pker.get();
        }

        // otherwise search through NPCs interacting with us
        return client.getTopLevelWorldView().npcs().stream()
                .filter(interacting)
                .filter(npc -> NPC_VALID.test(npc.getTransformedComposition()))
                .min(NPC_COMPARATOR.apply(npcManager, localPlayer)) // O(n)
                .orElse(null);
    }

    public void onActorDeath(ActorDeath actorDeath) {
        boolean self = actorDeath.getActor() == client.getLocalPlayer();

        if (isEnabled() && self) {
            handleDeath();
        }

        if (self || actorDeath.getActor() == lastTarget.get()) {
            lastTarget = new WeakReference<>(null);
        }
    }

    /**
     * Doom modifier can kill the player without health reaching zero, so ActorDeath isn't fired
     */
    public void onGameMessage(String message) {
        if (isEnabled()) {
            var player = client.getLocalPlayer();

            if (message.equals(FORTIS_DOOM_MSG) && player.getHealthRatio() > 0 && WorldUtils.getArea(client) == Area.MG_FORTIS_COLOSSEUM) {
                handleDeath();
            }
        }
    }

    public void onInteraction(InteractingChanged event) {
        if (event.getSource() == client.getLocalPlayer() && event.getTarget() != null && event.getTarget().getCombatLevel() > 0) {
            lastTarget = new WeakReference<>(event.getTarget());
        }
    }

    private void handleDeath() {
        Area area = WorldUtils.getCorrectedArea(client);
        DeathType deathType = WorldUtils.correctedDeathType(client, area);
        Actor killer = identifyKiller(area);
        boolean pker = killer instanceof Player;
        boolean npc = killer instanceof NPC;

        if (pker) {
            log.debug("killer is pker");
        } else if (npc) {
            log.debug("killer is npc");
        } else if (killer == null) {
            log.debug("killer is null");
        }

        if (shouldIgnoreDeath(pker, deathType)) {
            log.debug("shouldIgnoreDeath is true, returning");
            return;
        } else {
            log.debug("shouldIgnoreDeath is false, sending trigger");
        }

        String areaType = area.getAreaType().toString();
        String areaName = area.getAreaName();
        String deathTypeName = deathType.toString();
        String killerType = (pker || npc) ? ((pker) ? "Player" : "NPC") : "N/A";
        String killerName = (killer != null) ? killer.getName() : "N/A";

        log.debug("areaType {}", areaType);
        log.debug("area: {}", areaName);
        log.debug("deathType: {}", deathTypeName);
        log.debug("killerType: {}", killerType);
        log.debug("killer: {}", killerName);

        DoActionRequest request = deathRequest(areaType, areaName, deathTypeName, killerType, killerName);
        sendAction(request);
    }

    static {
        INTERACTING = (localPlayer, a) -> a != null && !a.isDead() && a.getInteracting() == localPlayer;

        NPC_VALID = comp -> comp != null && comp.isInteractible() && !comp.isFollower() && comp.getCombatLevel() > 0;

        NPC_COMPARATOR = (npcManager, localPlayer) -> Comparator
                .comparing(
                        NPC::getTransformedComposition,
                        Comparator.nullsFirst(
                                Comparator
                                        .comparing(
                                                (NPCComposition comp) -> comp.getStringValue(ParamID.NPC_HP_NAME),
                                                Comparator.comparing(StringUtils::isNotEmpty) // prefer has name in hit points UI
                                        )
                                        .thenComparing(comp -> ArrayUtils.contains(comp.getActions(), ATTACK_OPTION)) // prefer explicitly attackable
                                        .thenComparingInt(NPCComposition::getCombatLevel) // prefer high level
                                        .thenComparingInt(NPCComposition::getSize) // prefer large
                                        .thenComparing(NPCComposition::isMinimapVisible) // prefer visible on minimap
                                        .thenComparing(
                                                // prefer high max health
                                                comp -> npcManager.getHealth(comp.getId()),
                                                Comparator.nullsFirst(Comparator.naturalOrder())
                                        )
                        )
                )
                .thenComparingInt(p -> -localPlayer.getLocalLocation().distanceTo(p.getLocalLocation())) // prefer nearby
                .reversed(); // for consistency with PK_COMPARATOR such that Stream#min should be used in #identifyKiller

        PK_COMPARATOR = localPlayer -> Comparator
                .comparing(Player::isClanMember) // prefer not in clan
                .thenComparing(Player::isFriend) // prefer not friend
                .thenComparing(Player::isFriendsChatMember) // prefer not fc
                .thenComparingInt(p -> Math.abs(localPlayer.getCombatLevel() - p.getCombatLevel())) // prefer similar level
                .thenComparingInt(p -> -p.getCombatLevel()) // prefer higher level for a given absolute level gap
                .thenComparing(p -> p.getOverheadIcon() == null) // prefer praying
                .thenComparing(p -> p.getTeam() == localPlayer.getTeam()) // prefer different team cape
                .thenComparingInt(p -> localPlayer.getLocalLocation().distanceTo(p.getLocalLocation())); // prefer nearby
    }
}
