package com.streamerbot.triggers;


import com.streamerbot.DoActionRequest;
import com.streamerbot.domain.ApplicableDeathType;
import com.streamerbot.domain.Area;
import com.streamerbot.domain.SpecialEncounter;
import com.streamerbot.util.WorldUtils;
import lombok.extern.slf4j.Slf4j;
import javax.inject.Singleton;


import net.runelite.api.events.ActorDeath;
import java.util.Map;
import java.util.Set;

@Slf4j
@Singleton
public class DeathTrigger extends BaseTrigger {

    private boolean isEnabled() {
        return config.deathEnabled() && config.deathActionName() != null && config.deathActionId() != null;
    }

    private String actionName() {
        return config.deathActionName();
    }

    private String actionId() {
        return config.deathActionId();
    }

    private Set<SpecialEncounter> ignoredSpecialEncounters() {
        return config.ignoredSpecialEncounters();
    }

    private boolean isIgnoredSpecialEncounter(SpecialEncounter encounter) {
        return encounter != null && ignoredSpecialEncounters().contains(encounter);
    }

    private boolean ignoreSafeDeath() {
        return config.ignoreSafeDeath();
    }

    private boolean ignoreResurrection() {
        return config.ignoreResurrection();
    }

    private boolean shouldIgnoreDeath(ApplicableDeathType deathType, SpecialEncounter specialEncounter) {
        if(deathType == ApplicableDeathType.SAFE) {
            return ignoreSafeDeath();
        } else if(deathType == ApplicableDeathType.RESURRECTION) {
            return ignoreResurrection();
        } else if(specialEncounter != null) {
            return isIgnoredSpecialEncounter(specialEncounter);
        } else {
            return false;
        }
    }

    private DoActionRequest deathRequest(String location) {
        String playerName = client.getLocalPlayer().getName();
        assert playerName != null;
        Map<String, Object> args = Map.of("playerName", playerName, "location", location);
        return new DoActionRequest(actionId(), actionName(), args);
    }

    public void onActorDeath(ActorDeath actorDeath) {
        if(!isEnabled()) {
            return;
        }

        boolean self = actorDeath.getActor() == client.getLocalPlayer();
        if (self) {
           handleDeath();
       }
    }

    private void handleDeath() {
        Area area = WorldUtils.getArea(client);
        ApplicableDeathType deathType = WorldUtils.correctedDeathType(client, area);
        SpecialEncounter specialEncounter = WorldUtils.correctedSpecialEncounter(client, area);

        if(shouldIgnoreDeath(deathType, specialEncounter)) {
            return;
        }

        String location = (area != null) ? area.getAreaName() : "Unknown location";
        DoActionRequest request = deathRequest(location);
        sendAction(request);
    }
}
