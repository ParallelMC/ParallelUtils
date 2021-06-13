package parallelmc.parallelutils.modules.custommobs.spawners;

/**
 * A data structure that encapsulates parameters for spawners
 */
public class SpawnerOptions {
	public int radiusH; // The horizontal entity spread
	public int radiusV; // The vertical entity spread
	public int maxMobs; // The max number of mobs spawned at once
	public int mobsPerSpawn; // The number of mobs to spawn in one cycle
	public int cooldown; // The amount of time between spawn cycles in ticks
	public int warmup; // The amount of time from spawner creation it takes to start spawning
	public boolean checkForPlayers; // If true, spawner only spawns if a player is within the activation range
	public int activationRange; // The range in which to check for players if checkForPlayers is true
	public int leashRange; // The range in which leashed entities are brought back to the spawner
	public boolean resetHealthOnLeash; // If true, leashed entities are healed when brought back to the spawner
	public boolean resetThreatOnLeash; // If true, leashed entities lose target when brought back to the spawner

	/**
	 * Create a new set of spawner options with the given options
 	 * @param radiusH The horizontal entity spread
	 * @param radiusV The vertical entity spread
	 * @param maxMobs The max number of mobs spawned at once
	 * @param mobsPerSpawn The number of mobs to spawn in one cycle
	 * @param cooldown The amount of time between spawn cycles in ticks
	 * @param warmup The amount of time from spawner creation it takes to start spawning
	 * @param checkForPlayers If true, spawner only spawns if a player is within the activation range
	 * @param activationRange The range in which to check for players if {@code checkForPlayers} is true
	 * @param leashRange The range in which leashed entities are brought back to the spawner
	 * @param resetHealthOnLeash If true, leashed entities are healed when brought back to the spawner
	 * @param resetThreatOnLeash If true, leashed entities lose target when brought back to the spawner
	 */
	public SpawnerOptions(int radiusH, int radiusV, int maxMobs, int mobsPerSpawn, int cooldown, int warmup, boolean checkForPlayers, int activationRange, int leashRange, boolean resetHealthOnLeash, boolean resetThreatOnLeash) {
		this.radiusH = radiusH;
		this.radiusV = radiusV;
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
