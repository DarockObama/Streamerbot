package com.streamerbot.util;

import com.streamerbot.domain.DeathType;
import com.streamerbot.domain.Area;
import lombok.experimental.UtilityClass;
import net.runelite.api.*;
import net.runelite.api.annotations.Varp;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.EnumSet;
import java.util.Set;

@UtilityClass
public class WorldUtils {

    /**
     * @see <a href="https://chisel.weirdgloop.org/varbs/display?varbit=6104#ChangeFrequencyTitle">Chisel</a>
     */
    private final int DRAGON_SLAYER_II_COMPLETED = 215;

    /**
     * @see <a href="https://chisel.weirdgloop.org/varbs/display?varbit=9016#ChangeFrequencyTitle">Chisel</a>
     */
    private final int SONG_OF_THE_ELVES_COMPLETED = 200;

    /**
     * @see <a href="https://oldschool.runescape.wiki/w/RuneScape:Varplayer/2926">Wiki</a>
     */
    private final @Varp int INSIDE_RAID_OR_CHALLENGE = 2926;

    private static final Set<Area> PVP_MINIGAMES = EnumSet.of(Area.MG_BOUNTY_HUNTER, Area.MG_CASTLE_WARS, Area.MG_CLAN_WARS, Area.MG_PVP_ARENA, Area.MG_LAST_MAN_STANDING_DESERTED_ISLAND, Area.MG_LAST_MAN_STANDING_WILD_VARROCK, Area.MG_SOUL_WARS ,Area.MG_TZHAAR_FIGHT_PITS);

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

    @NotNull
    public static Area getArea(Client client) {
        WorldPoint worldPoint = getLocation(client);

        if(worldPoint != null) {
            int regionID = worldPoint.getRegionID();
            return Area.fromRegion(regionID);
        } else {
            return Area.UNKNOWN;
        }
    }

    @NotNull
    public static Area getCorrectedArea(Client client) {
        WorldPoint worldPoint = getLocation(client);

        if(worldPoint != null) {
            int regionID = worldPoint.getRegionID();
            return correctedArea(client, Area.fromRegion(regionID));
        } else {
            return Area.UNKNOWN;
        }
    }

    public boolean isPvpWorld(Set<WorldType> worldType) {
        return worldType.contains(WorldType.PVP) || worldType.contains(WorldType.DEADMAN);
    }

    public boolean isPvpSafeZone(Client client) {
        Widget widget = client.getWidget(InterfaceID.PvpIcons.PVPW_SAFE);
        return widget != null && !widget.isHidden();
    }

    public boolean isWilderness(Client client) {
        return client.getVarbitValue(VarbitID.INSIDE_WILDERNESS) > 0;
    }

    public boolean isPvpMinigame(Area area) {
        if(area == null) {
            return false;
        }

        return PVP_MINIGAMES.contains(area);
    }

    public boolean pvpEnabled(Client client, Area area, Set<WorldType> worldType) {
        return isWilderness(client) || (isPvpWorld(worldType) && !isPvpSafeZone(client) || isPvpMinigame(area));
    }

    public boolean isGalvekRematch(Client client, Area area) {
        return area == Area.REGION_GALVEK_SHIPWRECKS  && client.getVarbitValue(VarbitID.DS2) >= DRAGON_SLAYER_II_COMPLETED;
    }

    public boolean isSerenRematch(Client client, Area area) {
        return area == Area.REGION_SEREN_FIGHT && client.getVarbitValue(VarbitID.SOTE) >= SONG_OF_THE_ELVES_COMPLETED;
    }

    /**
     * Checks whether the player is within TzHaar-Ket-Rak's Challenges
     * The in-game region is the same as the inferno, but a varp value is different
     */
    public boolean isJadChallenges(Client client, Area area) {
        return area == Area.MG_INFERNO && client.getVarpValue(INSIDE_RAID_OR_CHALLENGE) > 0;
    }

    public Area correctedArea(Client client, Area area) {
        if(isJadChallenges(client, area)) {
            return Area.MG_JAD_CHALLENGES;
        } else if(isWilderness(client)) {
            return Area.REGION_WILDERNESS;
        } else {
            return area;
        }
    }

    public boolean isLastManStanding(Client client, Area area) {
        if (area == Area.MG_LAST_MAN_STANDING_WILD_VARROCK || area == Area.MG_LAST_MAN_STANDING_DESERTED_ISLAND) {
            return true;
        }

        Widget widget = client.getWidget(InterfaceID.BrOverlay.CONTENT);
        return widget != null && !widget.isHidden();
    }

    public boolean isPestControl(Client client, Area area) {
        if(area == Area.MG_PEST_CONTROL) {
            return true;
        }

        Widget widget = client.getWidget(InterfaceID.PestStatusOverlay.PEST_STATUS_PORT2);
        return widget != null && !widget.isHidden();
    }

    public boolean isTzHaarFightCave(Area area) {
        return area == Area.MG_TZHAAR_FIGHT_CAVES;
    }

    public boolean fightCavesResurrectionAvailable(Client client) {
        return client.getVarbitValue(VarbitID.KARAMJA_DIARY_ELITE_COMPLETE) > 0 && client.getVarbitValue(VarbitID.KARAMJA_FIGHTCAVE_RESSURECTION) == 0;
    }

    public boolean isZulrah(Area area) {
        return area == Area.BOSS_ZULRAH;
    }

    public boolean zulrahReviveAvailable(Client client) {
        return client.getVarbitValue(VarbitID.WESTERN_DIARY_EASY_COMPLETE) > 0 && client.getVarbitValue(VarbitID.ZULRAH_REVIVE) == 0;
    }

    /**
     * Ensures the player is actually in LMS or Pest Control.
     * Checks if the player is fighting Galvek or Seren during or after the quest and
     * chooses a DANGEROUS or SAFE DeathType accordingly.
     * Distinguishes resurrections from actual deaths at Zulrah and in the Fight Caves.
     * */
    public DeathType correctedDeathType(Client client, Area area) {
        if(isPestControl(client, area)) {
            return DeathType.SAFE_FOR_SOLO_HCIM;
        } else if(isLastManStanding(client, area)) {
            return DeathType.SAFE_FOR_HCGIM;
        } else if(isGalvekRematch(client, area) || isSerenRematch(client, area)) {
            return DeathType.SAFE_FOR_SOLO_HCIM;
        } else if(isTzHaarFightCave(area)) {
            if(fightCavesResurrectionAvailable(client)) {
                return DeathType.RESURRECTION;
            } else {
                return area.getDeathType();
            }
        } else if(isZulrah(area)) {
            if(zulrahReviveAvailable(client)) {
                return DeathType.RESURRECTION;
            } else {
                return area.getDeathType();
            }
        } else {
            return area.getDeathType();
        }
    }
}
