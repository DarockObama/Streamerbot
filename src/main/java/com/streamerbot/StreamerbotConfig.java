package com.streamerbot;

import com.streamerbot.domain.*;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("streamerbot")
public interface StreamerbotConfig extends Config
{
	@ConfigItem(
			keyName = "streamerbotAddress",
			name = "Streamerbot address",
			description = "The address of the HTTP server that your Streamerbot instance is running and this plugin will send requests to.",
			position = 0
	)
	default String streamerbotAddress()
	{
		return "127.0.0.1";
	}

	@ConfigItem(
			keyName = "streamerbotPort",
			name = "Streamerbot port",
			description = "The port of the HTTP server that your Streamerbot instance is running and this plugin will send requests to.",
			position = 1
	)
	default String streamerbotPort()
	{
		return "7474";
	}

	@ConfigSection(
			name = "Death",
			description = "Settings for triggering Streamerbot when you die.",
			position = 2,
			closedByDefault = true
	)
	String deathSection = "Death";

	@ConfigItem(
			keyName = "deathEnabled",
			name = "Enable Death",
			description = "Enable Streamerbot triggers for when you die.",
			position = 2,
			section = deathSection
	)
	default boolean deathEnabled() {
		return false;
	}

	@ConfigItem(
			keyName = "deathActionName",
			name = "Streamerbot action name",
			description = "The exact name of the Streamerbot action to be triggered when you die.",
			position = 3,
			section = deathSection
	)
	default String deathActionName() {
		return "";
	}

	@ConfigItem(
			keyName = "deathActionId",
			name = "Streamerbot action ID",
			description = "The ID corresponding to the Streamerbot action to be triggered when you die. <br/>" +
					"To copy this ID, right-click the action in Streamerbot and select 'Copy Action Id'.",
			position = 4,
			section = deathSection
	)
	default String deathActionId() {
		return "";
	}


    @ConfigItem(
            keyName = "ignoreSafeDeathPolicy",
            name = "Ignore",
            description = "The type of safe deaths that should be ignored",
            position = 5,
            section = deathSection
    )

    default IgnoreSafeDeathPolicy ignoreSafeDeathPolicy() {
        return IgnoreSafeDeathPolicy.NONE;
    }

	@ConfigItem(
			keyName = "ignoreResurrection",
			name = "Ignore resurrections",
			description = "Do not send a death trigger upon undergoing the Karamja Elite Diary resurrection in the Fight Caves <br/>" +
					"and Western Elite Diary resurrection at Zulrah.",
			position = 6,
			section = deathSection
	)
	default boolean ignoreResurrection() {
		return false;
	}

	@ConfigItem(
			keyName = "ignorePvp",
			name = "Ignore PvP deaths",
			description ="Do not send a trigger upon death by another player",
			position = 7,
			section = deathSection
	)
	default boolean ignorePvp() {
		return false;
	}
}
