package com.streamerbot;
import com.google.inject.Provides;
import javax.inject.Inject;
import com.streamerbot.triggers.DeathTrigger;
import com.streamerbot.util.Utils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.InteractingChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.ActorDeath;


@Slf4j
@PluginDescriptor(
		name = "Streamerbot",
		description = "Lets in-game events trigger Streamerbot actions. <br/>"
		+ "Requires user to run the Streamerbot application",
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

	@Subscribe(priority = 1) //run before the base loot tracker plugin
	public void onChatMessage(ChatMessage message) {
		String chatMessage = Utils.sanitize(message.getMessage());
		String source = message.getName() !=null && !message.getName().isEmpty() ? message.getName() : message.getSender();

		switch (message.getType()) {
			case GAMEMESSAGE:
				if("runelite".equals(source)) {
					return; // filter out plugin-sourced chat messages
				}
				deathTrigger.onGameMessage(chatMessage);
				break;

			default:
				break;
		}
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event) {
		deathTrigger.onInteraction(event);
	}

	@Provides
	StreamerbotConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StreamerbotConfig.class);
	}
}
