package parallelmc.parallelutils.discordintegration;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * This class handles functions related to Discord bot integration
 */
public class BotManager extends ListenerAdapter {

	private final JDA client;
	private final HashMap<String, String> channels;

	private final int NUM_TRIES = 5;

	private boolean ready = false;

	private static BotManager manager;

	private final char PREFIX = '!';

	private final String serverId;
	private final String staffId;

	/**
	 * Instantiate a new BotManager with the given Discord bot token
	 *
	 * @param token The Discord bot token
	 */
	public BotManager(String token, String serverId, String staffId) throws LoginException {
		client = JDABuilder.createDefault(token).addEventListeners((EventListener) event -> {
			if (event instanceof ReadyEvent) {
				ready = true;
				Parallelutils.log(Level.INFO, "JDA Ready");
			}
		}).build();
		channels = new HashMap<>();

		this.serverId = serverId;
		this.staffId = staffId;

		if (manager == null) {
			manager = this;
		}
	}

	/**
	 * Returns the singleton instance of BotManager
	 *
	 * @return The instance
	 */
	@Nullable
	public static BotManager getInstance() {
		return manager;
	}

	/**
	 * Add a channel and ID pair to the map of channels. This is used to assign useful names to channel ids in code
	 *
	 * @param name The assigned name for the channel id pair
	 * @param id   The id of the channel
	 */
	public void addChannel(String name, String id) {
		channels.put(name, id);
	}

	/**
	 * Sends a message in the specified channel
	 *
	 * @param channel The shortname of the channel to send a message to
	 * @param message The message that is being sent
	 * @return Returns true if the message was sent successfully, false otherwise
	 */
	public boolean sendMessage(String channel, String message) {
		Parallelutils.log(Level.INFO, "Attempting to send a message");
		boolean success = false;
		if (ready) {
			TextChannel textChannel = client.getTextChannelById(channels.get(channel));
			if (textChannel != null) {
				textChannel.sendMessage(message).queue();
				success = true;
			}
		}

		return success; // Yes this is dumb, no I'm not changing it. It's an artifact of old code
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.TEXT)) {
			// Only look in the correct server
			if (event.getTextChannel().getId().equals(serverId)) {
				// Is this message a command?
				String message = event.getMessage().getContentStripped();
				if (message.charAt(0) == PREFIX) {
					String command = message.substring(1);

					// Can this member actually execute mod commands?
					Member member = event.getMember();
					if (member != null) {
						List<Role> roles = member.getRoles();
						for (Role r : roles) {
							if (r.getId().equals(staffId)) {
								// Can be executed
								String[] parts = command.split(" ");
								executeCommand(event, parts[0],  Arrays.copyOfRange(parts, 1, parts.length));
								break;
							}
						}
					}
				}
			}
		}
	}

	private void executeCommand(@NotNull MessageReceivedEvent event, String command, String[] args) {
		if (command.strip().equals("vanish")) {
			String target = args[0].strip();
			if (!JoinSuppressorListener.hiddenUsers.contains(target)) {
				JoinSuppressorListener.hiddenUsers.add(target);
				event.getTextChannel().sendMessage("Vanished user " + target).queue();
			} else {
				JoinSuppressorListener.hiddenUsers.remove(target);
				event.getTextChannel().sendMessage("Un-vanished user " + target).queue();
			}
		}
	}
}
