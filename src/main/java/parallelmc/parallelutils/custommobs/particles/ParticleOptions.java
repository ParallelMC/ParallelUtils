package parallelmc.parallelutils.custommobs.particles;

import org.bukkit.Particle;

/**
 * A data structure that encapsulates the data relevant to spawning particles for mobs
 */
public class ParticleOptions {
	public Particle particle;
	public int amount;
	public double hSpread;
	public double vSpread;
	public double speed;

	/**
	 * Create a new ParticleOptions
	 * @param particle The Particle to spawn
	 * @param amount The number of particles to spawn
	 * @param hSpread The horizontal spread of the particles
	 * @param vSpread The vertical spread of the particles
	 * @param speed The speed at which the particles move
	 */
	public ParticleOptions(Particle particle, int amount, double hSpread, double vSpread, double speed) {
		this.particle = particle;
		this.amount = amount;
		this.hSpread = hSpread;
		this.vSpread = vSpread;
		this.speed = speed;
	}
}
