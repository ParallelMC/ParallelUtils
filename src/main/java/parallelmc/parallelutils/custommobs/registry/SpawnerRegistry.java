package parallelmc.parallelutils.custommobs.registry;

import org.bukkit.Location;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.custommobs.spawners.SpawnerData;
import parallelmc.parallelutils.custommobs.spawners.SpawnerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class SpawnerRegistry {

	private HashMap<String, SpawnerOptions> spawnerTypes;

	private HashMap<Location, SpawnerData> spawners;

	private HashMap<Location, Integer> mobCounts;

	private HashMap<Location, Integer> spawnTaskID;

	private HashMap<Location, Integer> leashTaskID;

	private HashMap<Location, ArrayList<String>> leashedEntityLists;

	private static SpawnerRegistry registry;

	private SpawnerRegistry() {
		spawnerTypes = new HashMap<>();
		spawners = new HashMap<>();
		mobCounts = new HashMap<>();
		spawnTaskID = new HashMap<>();
		leashTaskID = new HashMap<>();
		leashedEntityLists = new HashMap<>();
	}

	public static SpawnerRegistry getInstance() {
		if (registry == null) {
			registry = new SpawnerRegistry();
		}

		return registry;
	}

	public void registerSpawner(String uuid, String type, Location location, boolean hasLeash) {
		Parallelutils.log(Level.INFO, "Registering spawner " + uuid + " location: " + location.toString());
		spawners.put(location, new SpawnerData(uuid, type, location, hasLeash));
	}

	public void registerSpawner(String type, Location location, boolean hasLeash) {
		registerSpawner(UUID.randomUUID().toString(), type, location, hasLeash);
	}

	public SpawnerData getSpawner(Location location) {
		return spawners.get(location);
	}

	public Location getSpawnerLoc(String uuid) {
		for (SpawnerData d : getSpawnerData()) {
			if (d.getUuid().equals(uuid)) {
				return d.getLocation();
			}
		}
		return null;
	}

	public boolean deleteSpawner(Location location) {
		Parallelutils.log(Level.INFO, "Removing spawner " + location.toString());
		return spawners.remove(location) != null;
	}

	public Collection<Location> getSpawnerLocations() {
		return spawners.keySet();
	}

	public Collection<SpawnerData> getSpawnerData() {
		return spawners.values();
	}

	public void registerSpawnerType(String type, SpawnerOptions options) {
		Parallelutils.log(Level.INFO, "Registering spawner type for " + type);
		spawnerTypes.put(type, options);
	}

	public SpawnerOptions getSpawnerOptions(String type) {
		return spawnerTypes.get(type);
	}

	public void addCount(Location loc, int count) {
		Parallelutils.log(Level.INFO, "Registering counter for " + loc.toString());
		mobCounts.put(loc, count);
	}

	public Integer getMobCount(Location loc) {
		return mobCounts.get(loc);
	}

	public void setMobCount(Location loc, int count) {
		mobCounts.replace(loc, count);
	}

	public void incrementMobCount(Location loc) {
		Parallelutils.log(Level.INFO, "Incrementing mob count for " + loc.toString());
		if (!mobCounts.containsKey(loc)) {
			addCount(loc, 1);
		} else {
			mobCounts.replace(loc, mobCounts.get(loc) + 1);
		}
	}

	public void decrementMobCount(Location loc) {
		Parallelutils.log(Level.INFO, "Decrementing mob count for " + loc.toString());
		mobCounts.replace(loc, mobCounts.get(loc) - 1);
	}

	public void removeMobCount(Location loc) {
		mobCounts.remove(loc);
	}

	public void addSpawnTaskID(Location loc, int id) {
		Parallelutils.log(Level.INFO, "Adding spawn task ID for " + loc.toString());
		spawnTaskID.put(loc, id);
	}

	public Integer getSpawnTaskID(Location loc) {
		return spawnTaskID.get(loc);
	}

	public void removeSpawnTaskID(Location loc) {
		spawnTaskID.remove(loc);
	}

	public void addLeashTaskID(Location loc, int id) {
		leashTaskID.put(loc, id);
	}

	public Integer getLeashTaskID(Location loc) {
		return leashTaskID.get(loc);
	}

	public void removeLeashTaskID(Location loc) {
		leashTaskID.remove(loc);
	}

	public void addLeashedEntity(Location loc, String id) {
		Parallelutils.log(Level.INFO, "Adding leashed entity for " + loc.toString());
		if (!leashedEntityLists.containsKey(loc)) {
			leashedEntityLists.put(loc, new ArrayList<String>());
		}
		leashedEntityLists.get(loc).add(id);
	}

	public void removeLeashedEntity(Location loc, String id) {
		leashedEntityLists.get(loc).remove(id);
	}

	public ArrayList<String> getLeashedEntityList(Location loc) {
		return leashedEntityLists.get(loc);
	}

	public void removeSpawnerLeash(Location loc) {
		ArrayList<String> mobs = leashedEntityLists.get(loc);
		leashedEntityLists.remove(loc);
	}
}
