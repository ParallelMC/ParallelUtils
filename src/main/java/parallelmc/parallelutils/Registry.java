package parallelmc.parallelutils;

import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import parallelmc.parallelutils.custommobs.EntityPair;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

public class Registry {

	private static HashMap<String, EntityPair> entities = new HashMap<>();

	public static void registerEntity(String uuid, String type, Entity entity) {
		Bukkit.getLogger().log(Level.INFO, "[ParallelUtils] Registering entity " + uuid);
		entities.put(uuid, new EntityPair(type, entity));
	}

	public static EntityPair getEntity(String uuid) {
		return entities.get(uuid);
	}

	public static Collection<String> getUUIDs() {
		return entities.keySet();
	}

	public static Collection<EntityPair> getEntities() {
		return entities.values();
	}

	public static EntityPair removeEntity(String uuid) {
		Bukkit.getLogger().log(Level.INFO, "[ParallelUtils] Removing entity " + uuid);
		return entities.remove(uuid);
	}

	public static boolean contains(String uuid) {
		Bukkit.getLogger().log(Level.ALL, "[ParallelUtils] Does contain " + uuid);
		return entities.containsKey(uuid);
	}
}
