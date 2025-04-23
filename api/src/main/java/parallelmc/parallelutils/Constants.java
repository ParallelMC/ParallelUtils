package parallelmc.parallelutils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Constants {

	public static final Version VERSION = new Version(4, 7, 0);
	public static final String PLUGIN_NAME = "ParallelUtils";
	public static final Component PLUGIN_PREFIX = MiniMessage.miniMessage()
			.deserialize("<dark_aqua>[<white><bold>P</bold><dark_aqua>] <reset>");
	public static final String DEFAULT_WORLD = "world2";

	public static final String[] OVERWORLD_TYPE_WORLDS = {"world", "world2", "world_skyteaser"};
}
