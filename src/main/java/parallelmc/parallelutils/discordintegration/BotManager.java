package parallelmc.parallelutils.discordintegration;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.rest.entity.RestChannel;
import parallelmc.parallelutils.Parallelutils;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * This class handles functions related to Discord bot integration
 */
public class BotManager {

	private final DiscordClient client;
	private final HashMap<String, String> channels;

	private final int NUM_TRIES = 5;

	private static BotManager manager;

	/**
	 * Instantiate a new BotManager with the given Discord bot token
	 * @param token The Discord bot token
	 */
	public BotManager(String token) {
		client = DiscordClient.create(token);
		channels = new HashMap<>();

		if (manager == null) {
			manager = this;
		}
	}

	/**
	 * Returns the singleton instance of BotManager
	 * @return The instance
	 */
	@Nullable
	public static BotManager getInstance() {
		return manager;
	}

	/**
	 * Add a channel and ID pair to the map of channels. This is used to assign useful names to channel ids in code
	 * @param name The assigned name for the channel id pair
	 * @param id The id of the channel
	 */
	public void addChannel(String name, String id) {
		channels.put(name, id);
	}

	/**
	 * Sends a message in the specified channel
	 * @param channel The shortname of the channel to send a message to
	 * @param message The message that is being sent
	 * @return Returns true if the message was sent successfully, false otherwise
	 */
	public boolean sendMessage(String channel, String message) {
		RestChannel restChannel = client.getChannelById(Snowflake.of(channels.get(channel)));

		boolean success = false;

		for (int i = 0; i < NUM_TRIES; i++) {
			try {
				restChannel.createMessage(message).block(Duration.ofSeconds(1));
			} catch (Exception e) {
				Parallelutils.log(Level.INFO, "Failed to send message. Trying again.");
				continue;
			}
			success = true;
			break;
		}

		return success;
	}
}
