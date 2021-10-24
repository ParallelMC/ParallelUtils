package parallelmc.parallelutils.modules.charms.data;

import org.bukkit.Material;

import java.util.UUID;

public class Charm {

	// Each charm is unique, mostly to track things for counters
	// but can also be used for player-specific text
	// This is also needed to bind a charm to an item. UUID will be in NBT
	private UUID charmId;

	// Has this charm been applied to an item?
	private boolean applied;

	private CharmOptions options;
}
