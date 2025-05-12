package com.streamerbot.util;

import com.streamerbot.domain.ApplicableDeathType;
import com.streamerbot.domain.Area;
import com.streamerbot.domain.SpecialEncounter;
import lombok.experimental.UtilityClass;
import net.runelite.api.*;
import net.runelite.api.annotations.Varbit;
import net.runelite.api.annotations.Varp;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import org.jetbrains.annotations.Nullable;


import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@UtilityClass
public class WorldUtils {

    private final Set<WorldType> IGNORED_WORLDS = EnumSet.of(WorldType.PVP_ARENA, WorldType.QUEST_SPEEDRUNNING, WorldType.BETA_WORLD, WorldType.NOSAVE_MODE, WorldType.TOURNAMENT_WORLD);

    /* private final Set<Integer> BA_REGIONS = ImmutableSet.of(7508, 7509, 10322);
    private final Set<Integer> CASTLE_WARS_REGIONS = ImmutableSet.of(9520, 9620);
    private final Set<Integer> CLAN_WARS_REGIONS = ImmutableSet.of(12621, 12622, 12623, 13130, 13131, 13133, 13134, 13135, 13386, 13387, 13390, 13641, 13642, 13643, 13644, 13645, 13646, 13647, 13899, 13900, 14155, 14156);
    private final Set<Integer> COX_REGIONS = ImmutableSet.of(12889, 13136, 13137, 13138, 13139, 13140, 13141, 13145, 13393, 13394, 13395, 13396, 13397, 13401);
    private final Set<Integer> GALVEK_REGIONS = ImmutableSet.of(6486, 6487, 6488, 6489, 6742, 6743, 6744, 6745);
    private final Set<Integer> GAUNTLET_REGIONS = ImmutableSet.of(7512, 7768, 12127); // includes CG
    private final Set<Integer> LMS_REGIONS = ImmutableSet.of(13658, 13659, 13660, 13914, 13915, 13916, 13918, 13919, 13920, 14174, 14175, 14176, 14430, 14431, 14432);
    private final Set<Integer> POH_REGIONS = ImmutableSet.of(7257, 7513, 7514, 7769, 7770, 8025, 8026);
    private final Set<Integer> SOUL_REGIONS = ImmutableSet.of(8493, 8748, 8749, 9005);
    private final Set<Integer> TOA_REGIONS = ImmutableSet.of(14160, 14162, 14164, 14674, 14676, 15184, 15186, 15188, 15696, 15698, 15700);
    private final Set<Integer> TOB_REGIONS = ImmutableSet.of(12611, 12612, 12613, 12867, 12869, 13122, 13123, 13125, 13379);
    private final Set<Integer> ZULRAH_REGIONS = Set.of(9007, 9008);
    private final int INFERNO_REGION = 9043;
    private final int CREATURE_GRAVEYARD_REGION = 13462;
    private final int NMZ_REGION = 9033;
    private final int TZHAAR_CAVE = 9551;
    public final @VisibleForTesting int TZHAAR_PIT = 9552;
    public final int FORTIS_REGION = 7216;
    private final int SEREN_REGION = 13148; */

    /**
     * @see <a href="https://oldschool.runescape.wiki/w/RuneScape:Varbit/6104">Wiki</a>
     */
    private final @Varbit int DRAGON_SLAYER_II_PROGRESS = 6104;

    /**
     * @see <a href="https://chisel.weirdgloop.org/varbs/display?varbit=6104#ChangeFrequencyTitle">Chisel</a>
     */
    private final int DRAGON_SLAYER_II_COMPLETED = 215;

    /**
     * @ see <a href="https://oldschool.runescape.wiki/w/RuneScape:Varbit/9016">Wiki</a>
     */
    private final @Varbit int SONG_OF_THE_ELVES_PROGRESS = 9016;

    /**
     * @see <a href="https://chisel.weirdgloop.org/varbs/display?varbit=9016#ChangeFrequencyTitle">Chisel</a>
     */
    private final int SONG_OF_THE_ELVES_COMPLETED = 200;

    /**
     * @see <a href="https://oldschool.runescape.wiki/w/RuneScape:Varplayer/2926">Wiki</a>
     */
    private final @Varp int INSIDE_RAID_OR_CHALLENGE = 2926;

    @Nullable
    public static WorldPoint getLocation(Client client) {
        return getLocation(client, client.getLocalPlayer());
    }

    @Nullable
    public static WorldPoint getLocation(Client client, Actor actor) {
        if (actor == null)
            return null;

        WorldView wv = actor.getWorldView();
        if (wv.isInstance())
            return WorldPoint.fromLocalInstance(client, actor.getLocalLocation(), wv.getPlane());

        return actor.getWorldLocation();
    }

    @Nullable
    public static Area getArea(Client client) {
        WorldPoint worldPoint = getLocation(client);
        if(worldPoint == null) {
            return null;
        } else {
            int regionId = worldPoint.getRegionID();
            return Area.fromRegion(regionId);
        }
    }

    public boolean isIgnoredWorld(Set<WorldType> worldType) {
        return !Collections.disjoint(IGNORED_WORLDS, worldType);
    }

    public boolean isPvpWorld(Set<WorldType> worldType) {
        return worldType.contains(WorldType.PVP) || worldType.contains(WorldType.DEADMAN);
    }

    public boolean isPvpSafeZone(Client client) {
        Widget widget = client.getWidget(ComponentID.PVP_SAFE_ZONE);
        return widget != null && !widget.isHidden();
    }

    public boolean isGalvekRematch(Client client, Area area) {
        return area == Area.REGION_GALVEK_SHIPWRECKS  && client.getVarbitValue(DRAGON_SLAYER_II_PROGRESS) >= DRAGON_SLAYER_II_COMPLETED;
    }

    public boolean isSerenRematch(Client client, Area area) {
        return area == Area.REGION_SEREN_FIGHT && client.getVarbitValue(SONG_OF_THE_ELVES_PROGRESS) >= SONG_OF_THE_ELVES_COMPLETED;
    }

    public boolean isInferno(Client client, Area area) {
       return area == Area.MG_INFERNO && client.getVarpValue(INSIDE_RAID_OR_CHALLENGE) == 0;
    }

    /**
     * Checks whether the player is within TzHaar-Ket-Rak's Challenges
     * The in-game region is the same as the inferno, but a varp value is different
     */
    public boolean isJadChallenges(Client client, Area area) {
        return area == Area.MG_INFERNO && client.getVarpValue(INSIDE_RAID_OR_CHALLENGE) > 0;
    }

    public boolean isLastManStanding(Client client, Area area) {
        if (area == Area.MG_LAST_MAN_STANDING_WILD_VARROCK || area == Area.MG_LAST_MAN_STANDING_DESERTED_ISLAND) {
            return true;
        }

        Widget widget = client.getWidget(ComponentID.LMS_INGAME_INFO);
        return widget != null && !widget.isHidden();
    }

    public boolean isPestControl(Client client, Area area) {
        if(area == Area.MG_PEST_CONTROL) {
            return true;
        }

        Widget widget = client.getWidget(ComponentID.PEST_CONTROL_BLUE_SHIELD);
        return widget != null && !widget.isHidden();
    }

    public boolean isTzHaarFightCave(Area area) {
        return area == Area.MG_TZHAAR_FIGHT_CAVES;
    }

    public boolean fightCavesResurrectionAvailable(Client client) {
        return client.getVarbitValue(VarbitID.KARAMJA_DIARY_ELITE_COMPLETE) > 0 && client.getVarbitValue(VarbitID.KARAMJA_FIGHTCAVE_RESSURECTION) == 0;
    }

    public boolean zulrahReviveAvailable(Client client) {
        return client.getVarbitValue(VarbitID.WESTERN_DIARY_EASY_COMPLETE) > 0 && client.getVarbitValue(VarbitID.ZULRAH_REVIVE) == 0;
    }

    public boolean isZulrah(Area area) {
        return area == Area.BOSS_ZULRAH;
    }

    /**
     * Ensures the player is actually in LMS or Pest Control.
     * Checks if the player is fighting Galvek or Seren during or after the quest and
     * chooses a DANGEROUS or SAFE ApplicableDeathType respectively.
     * Distinguishes resurrections from actual deaths at Zulrah and in the Fight Caves.
     * */
    public ApplicableDeathType correctedDeathType(Client client, Area area) {
        if(area == null) {
            return ApplicableDeathType.DANGEROUS;
        } else if(isPestControl(client, area) || isLastManStanding(client, area)) {
            return area.getDeathType();
        } else if(isGalvekRematch(client, area) || isSerenRematch(client, area)) {
            return ApplicableDeathType.SAFE;
        } else if(isJadChallenges(client, area)) {
            return ApplicableDeathType.SPECIAL;
        } else if(isTzHaarFightCave(area)) {
            if(fightCavesResurrectionAvailable(client)) {
                return ApplicableDeathType.RESURRECTION;
            } else {
                return area.getDeathType();
            }
        } else if(isZulrah(area)) {
            if(zulrahReviveAvailable(client)) {
                return ApplicableDeathType.RESURRECTION;
            } else {
                return area.getDeathType();
            }
        } else {
            return area.getDeathType();
        }
    }

    /**
     * JAD_CHALLENGES is the only SpecialEncounter that has no corresponding Area.
     */
    public SpecialEncounter correctedSpecialEncounter(Client client, Area area) {
        if(isJadChallenges(client, area)) {
            return SpecialEncounter.JAD_CHALLENGES;
        } else {
            return area.getSpecialEncounter();
        }
    }
}
