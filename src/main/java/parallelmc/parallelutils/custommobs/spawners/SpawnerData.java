package parallelmc.parallelutils.custommobs.spawners;

import org.bukkit.Location;

public class SpawnerData {

	private String uuid;
	private String type;
	private Location location;
	private boolean hasLeash;

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
