package parallelmc.parallelutils.modules.discordintegration;

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
import parallelmc.parallelutils.ParallelUtils;

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
	private final HashMap<String, String> messages;

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
				ParallelUtils.log(Level.INFO, "JDA Ready");
			}
		}).build();
		channels = new HashMap<>();
		messages = new HashMap<>();

		this.serverId = serverId.strip().toLowerCase();
		this.staffId = staffId;

		client.addEventListener(this);

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
	 * Adds a message and ID pair to the map of messages. This is used to assign useful names to messages in code
	 * @param name The assigned name for the message id pair
	 * @param id The id of the message
	 */
	public void addMessage(String name, String id) {
		messages.put(name, id);
	}

	/**
	 * Sends a message in the specified channel
	 *
	 * @param channel The shortname of the channel to send a message to
	 * @param message The message that is being sent
	 * @return Returns true if the message was sent successfully, false otherwise
	 */
	public boolean sendMessage(String channel, String message) {
		ParallelUtils.log(Level.INFO, "Attempting to send a message");
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

	/**
	 * Edits a message sent by ParallelBot
	 * @param channel The channel of the message to edit
	 * @param messageId The id of the message to edit
	 * @param message The new message
	 * @return true if the message was edited successfully
	 */
	public boolean editMessage(String channel, String messageId, String message) {
		ParallelUtils.log(Level.INFO, "Attempting to edit message");

		boolean success = false;
		if (ready) {
			TextChannel textChannel = client.getTextChannelById(channels.get(channel));
			if (textChannel != null) {
				textChannel.editMessageById(messages.get(messageId), message).queue();
				success = true;
			} else {
				ParallelUtils.log(Level.WARNING, "Unable to find text channel");
			}
		}

		return success;
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.TEXT)) {
			// Only look in the correct server
			if (event.getGuild().getId().strip().toLowerCase().equals(serverId)) {
				// Is this message a command?
				String message = event.getMessage().getContentStripped();
				if (message.length() > 0 && message.charAt(0) == PREFIX) {
					String command = message.substring(1);

					// Can this member actually execute mod commands?
					Member member = event.getMember();
					if (member != null) {
						List<Role> roles = member.getRoles();
						for (Role r : roles) {
							if (r.getId().equals(staffId)) {
								ParallelUtils.log(Level.INFO, "Trying to run command...");
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

	/**
	 * Execute a discord command from a given message event
	 * @param event The event the command originated from
	 * @param command The command to execute
	 * @param args The arguments for the command
	 */
	private void executeCommand(@NotNull MessageReceivedEvent event, String command, String[] args) {
		if (command.strip().equals("vanish")) {
			if (args.length <= 0) return;

			String target = args[0].strip();
			synchronized (JoinQuitSuppressorListener.hiddenUsersLock) {
				if (!JoinQuitSuppressorListener.hiddenUsers.contains(target)) {
					JoinQuitSuppressorListener.hiddenUsers.add(target);
					event.getTextChannel().sendMessage("Vanished user " + target).queue();
				} else {
					JoinQuitSuppressorListener.hiddenUsers.remove(target);
					event.getTextChannel().sendMessage("Un-vanished user " + target).queue();
				}
			}
		}
	}

	/**
	 * Shutsdown JDA
	 */
	public void disable() {
		try {
			client.shutdownNow();
		} catch (NoClassDefFoundError e) {
			ParallelUtils.log(Level.WARNING, "JDA Class not found! This is a bug with JDA. Handling");
		}
	}
}
