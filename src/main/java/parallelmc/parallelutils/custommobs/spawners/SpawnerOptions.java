package parallelmc.parallelutils.custommobs.spawners;

public class SpawnerOptions {
	public int radiusX;
	public int radiusY;
	public int maxMobs;
	public int mobsPerSpawn;
	public int cooldown;
	public int warmup;
	public boolean checkForPlayers;
	public int activationRange;
	public int leashRange;
	public boolean resetHealthOnLeash;
	public boolean resetThreatOnLeash;

	public SpawnerOptions(int radiusX, int radiusY, int maxMobs, int mobsPerSpawn, int cooldown, int warmup, boolean checkForPlayers, int activationRange, int leashRange, boolean resetHealthOnLeash, boolean resetThreatOnLeash) {
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.maxMobs = maxMobs;
		this.mobsPerSpawn = mobsPerSpawn;
		this.cooldown = cooldown;
		this.warmup = warmup;
		this.checkForPlayers = checkForPlayers;
		this.activationRange = activationRange;
		this.leashRange = leashRange;
		this.resetHealthOnLeash = resetHealthOnLeash;
		this.resetThreatOnLeash = resetThreatOnLeash;
	}


}
