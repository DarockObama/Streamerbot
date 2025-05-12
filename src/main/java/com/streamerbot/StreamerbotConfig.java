package com.streamerbot;

import com.streamerbot.domain.SpecialEncounter;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.util.EnumSet;
import java.util.Set;

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
			keyName = "ignoredSpecialEncounters",
			name = "Ignore selected",
			description = "Individual raids and miscellaneous PvM encounters where death should be ignored. <br/>" +
					"Hold Control while clicking on the options to select multiple encounters to ignore.",
			position = 5,
			section = deathSection
	)
	default Set<SpecialEncounter> ignoredSpecialEncounters() {
		return EnumSet.noneOf(SpecialEncounter.class);
	}

	@ConfigItem(
			keyName = "ignoreSafeDeath",
			name = "Ignore other safe deaths",
			description = "Ignore all safe deaths excluded in the list above. <br/>" +
			"This follows the rule of safe deaths for solo hardcore ironman accounts.",
			position = 6,
			section = deathSection
	)
	default boolean ignoreSafeDeath() {
		return false;
	}

	@ConfigItem(
			keyName = "ignoreResurrection",
			name = "Ignore resurrections",
			description = "Do not send a death trigger upon undergoing the Karamja Elite Diary resurrection in the Fight Caves <br/>" +
					"and Western Elite Diary resurrection at Zulrah.",
			position = 7,
			section = deathSection
	)
	default boolean ignoreResurrection() {
		return true;
	}
}
