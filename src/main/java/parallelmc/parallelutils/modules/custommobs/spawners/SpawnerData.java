package parallelmc.parallelutils.modules.custommobs.spawners;

import org.bukkit.Location;

/**
 * A data structure that encapsulates data used for mob spawners
 */
public class SpawnerData {

	private final String uuid;
	private final String type;
	private final Location location;
	private final boolean hasLeash;

	/**
	 * Create a new SpawnerData object with the spawner's UUID and location as well as the spawner's type and if it has a leash
	 * @param uuid The UUID of the spawner
	 * @param type The type the spawner
	 * @param location The location of the spawner
	 * @param hasLeash True if this spawner has a leash
	 */
	public SpawnerData(String uuid, String type, Location location, boolean hasLeash) {
		this.uuid = uuid;
		this.type = type;
		this.location = location;
		this.hasLeash = hasLeash;
	}

	public String getUuid() {
		return uuid;
	}

	public String getType() {
		return type;
	}

	public Location getLocation() {
		return location;
	}

	public boolean hasLeash() {
		return hasLeash;
	}

	@Override
	public String toString() {
		return "SpawnerData{" +
				"uuid='" + uuid + '\'' +
				", type='" + type + '\'' +
				", location=" + location +
				", hasLeash=" + hasLeash +
				'}';
	}
}
