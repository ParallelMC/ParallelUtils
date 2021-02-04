package parallelmc.parallelutils.custommobs.registry;

import org.bukkit.Location;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.custommobs.spawners.SpawnerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class SpawnerRegistry {

	private HashMap<String, SpawnerOptions> spawners;

	private HashMap<Location, Integer> mobCounts;

	private HashMap<Location, Integer> spawnTaskID;

	private HashMap<Location, Integer> leashTaskID;

	private HashMap<String, Location> leashedEntityLocations;
	private HashMap<Location, ArrayList<String>> leashedEntityLists;

	private static SpawnerRegistry registry;

	private SpawnerRegistry() {
		spawners = new HashMap<>();
	}

	public static SpawnerRegistry getInstance() {
		if (registry == null) {
			registry = new SpawnerRegistry();
		}

		return registry;
	}

	public void registerSpawner(String type, SpawnerOptions options){
		Parallelutils.log(Level.INFO, "Registering spawner for " + type);
		spawners.put(type, options);
	}

	public SpawnerOptions getSpawnerOptions(String type){ return spawners.get(type);}

	public void addCount(Location loc, int count){
		Parallelutils.log(Level.INFO, "Registering counter for " + loc.toString());
		mobCounts.put(loc, count);
	}

	public int getMobCount(Location loc){return mobCounts.get(loc);}

	public void setMobCount(Location loc, int count){mobCounts.replace(loc, count); }

	public void incrementMobCount(Location loc){mobCounts.replace(loc, mobCounts.get(loc)+1);}
	public void decrementMobCount(Location loc){mobCounts.replace(loc, mobCounts.get(loc)-1);}

	public void removeMobCount(Location loc){mobCounts.remove(loc);}

	public void addSpawnTaskID(Location loc, int id){spawnTaskID.put(loc, id);}

	public int getSpawnTaskID(Location loc){return spawnTaskID.get(loc);}

	public void removeSpawnTaskID(Location loc){spawnTaskID.remove(loc);}

	public void addLeashTaskID(Location loc, int id){leashTaskID.put(loc, id);}

	public int getLeashTaskID(Location loc){return leashTaskID.get(loc);}

	public void removeLeashTaskID(Location loc){leashTaskID.remove(loc);}

	public void addLeashedEntity(Location loc, String id){
		leashedEntityLocations.put(id, loc);
		if(!leashedEntityLists.containsKey(loc)){
			leashedEntityLists.put(loc, new ArrayList<String>());
		}
		leashedEntityLists.get(loc).add(id);
	}

	public ArrayList<String> getLeashedEntityList(Location loc){return leashedEntityLists.get(loc);}

	public Location getLeashedEntitySpawner(String id){return leashedEntityLocations.get(id);}

	public void removeLeashedEntity(String id){
		Location loc = leashedEntityLocations.get(id);
		leashedEntityLocations.remove(id);
		leashedEntityLists.get(loc).remove(id);
	}

	public void removeSpawnerLeash(Location loc){
		ArrayList<String> mobs = leashedEntityLists.get(loc);
		leashedEntityLists.remove(loc);
		for(String id : mobs){
			leashedEntityLocations.remove(id);
		}
	}
}
