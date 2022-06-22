package parallelmc.parallelutils.modules.charms.data;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CharmRegistry {

	// This is loaded from the config files
	private HashMap<Integer, CharmOptions> charmOptions;

	// This is loaded from the database
	private ArrayList<Charm> charms;
}
