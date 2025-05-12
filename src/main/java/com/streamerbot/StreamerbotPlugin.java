package com.streamerbot;
import com.google.inject.Provides;
import javax.inject.Inject;
import com.streamerbot.triggers.DeathTrigger;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.ActorDeath;


@Slf4j
@PluginDescriptor(
		name = "Streamerbot",
		description = "Lets in-game events trigger Streamerbot actions",
		tags = {"streamer", "events", "trigger", "OBS", "Streamerbot", "Twitch"}
)

public class StreamerbotPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private StreamerbotConfig config;

	@Inject
	DeathTrigger deathTrigger;

    public StreamerbotPlugin() {
    }

    @Override
	protected void startUp() {
		log.info("Streamerbot started!");
	}

	@Override
	protected void shutDown()
	{
		log.info("Streamerbot stopped!");
	}

	@Subscribe
	public void onActorDeath(ActorDeath actorDeath) {
		deathTrigger.onActorDeath(actorDeath);
	}

	@Provides
	StreamerbotConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StreamerbotConfig.class);
	}
}
