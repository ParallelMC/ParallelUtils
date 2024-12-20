package parallelmc.parallelutils.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import parallelmc.parallelutils.Constants;

/**
 * A set of utilities to make sending messages easier
 */
public class MessageTools {

    /**
     * Sends a message to a player with the Parallel prefix prepended to it
     * (This version of the method sends an "INFO" style message)
     * @param player The player to send the message to
     * @param message The message without the prefix
     */
    public static void sendMessage(Player player, String message) {
        TextComponent finalMessage = (TextComponent) Constants.PLUGIN_PREFIX
                .append(Component.text(message, NamedTextColor.AQUA));
        player.sendMessage(finalMessage);
    }

    /**
     * Sends a message to a player with the Parallel prefix prepended to it
     * @param player The player to send the message to
     * @param message The message without the prefix
     * @param messageType The type of message; this determines the message color
     */
    public static void sendMessage(Player player, String message, MessageType messageType) {
        TextComponent finalMessage = switch (messageType) {
            case INFO -> (TextComponent) Constants.PLUGIN_PREFIX.append(Component.text(message, NamedTextColor.AQUA));
            case SUCCESS -> (TextComponent) Constants.PLUGIN_PREFIX.append(Component.text(message, NamedTextColor.GREEN));
            case ERROR -> (TextComponent) Constants.PLUGIN_PREFIX.append(Component.text(message, NamedTextColor.RED));
        };
        player.sendMessage(finalMessage);
    }

    /**
     * Sends a message to console with the Parallel prefix prepended to it
     * (This version of the method sends an "INFO" style message)
     * @param message The message without the prefix
     */
    public static void sendConsoleMessage(String message) {
        TextComponent finalMessage = (TextComponent) Constants.PLUGIN_PREFIX
                .append(Component.text(message, NamedTextColor.AQUA));
        Bukkit.getConsoleSender().sendMessage(finalMessage);
    }

    /**
     * Sends a message to console with the Parallel prefix prepended to it
     * @param message The message without the prefix
     * @param messageType The type of message; this determines the message color
     */
    public static void sendConsoleMessage(String message, MessageType messageType) {
        TextComponent finalMessage = switch (messageType) {
            case INFO -> (TextComponent) Constants.PLUGIN_PREFIX.append(Component.text(message, NamedTextColor.AQUA));
            case SUCCESS -> (TextComponent) Constants.PLUGIN_PREFIX.append(Component.text(message, NamedTextColor.GREEN));
            case ERROR -> (TextComponent) Constants.PLUGIN_PREFIX.append(Component.text(message, NamedTextColor.RED));
        };
        Bukkit.getConsoleSender().sendMessage(finalMessage);
    }
}
