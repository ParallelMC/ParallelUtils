package parallelmc.parallelutils;

import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

public class Registry {

	private static HashMap<String, EntityInsentient> entities = new HashMap<>();

	public static void registerEntity(String uuid, EntityInsentient entity) {
		Bukkit.getLogger().log(Level.INFO, "[ParallelUtils] Registering entity " + uuid);
		entities.put(uuid, entity);
	}

	public static EntityInsentient getEntity(String uuid) {
		return entities.get(uuid);
	}

	public static Collection<String> getUUIDs() {
		return entities.keySet();
	}

	public static Collection<EntityInsentient> getEntities() {
		return entities.values();
	}

	public static EntityInsentient removeEntity(String uuid) {
		Bukkit.getLogger().log(Level.INFO, "[ParallelUtils] Removing entity " + uuid);
		return entities.remove(uuid);
	}

	public static boolean contains(String uuid) {
		return entities.containsKey(uuid);
	}
}
