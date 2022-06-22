package parallelmc.parallelutils.modules.custommobs.registry;

import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.custommobs.particles.ParticleOptions;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * This singleton class is responsible for keeping track of particle types and their corresponding options
 */
public class ParticleRegistry {

	private HashMap<String, ParticleOptions> entityParticles;

	private static ParticleRegistry registry;

	private ParticleRegistry() {
		entityParticles = new HashMap<>();
	}

	/**
	 * Retrieve the singleton instance of this class
	 * @return The instance of this class
	 */
	public static ParticleRegistry getInstance() {
		if (registry == null) {
			registry = new ParticleRegistry();
		}

		return registry;
	}

	/**
	 * Register a particle type with a given set of options
	 * @param type The name of the particle type being registered
	 * @param options The configuration for this particle type
	 */
	public void registerParticles(String type, ParticleOptions options) {
		Parallelutils.log(Level.INFO, "Registering particles for " + type);
		entityParticles.put(type, options);
	}

	/**
	 * Return the ParticleOptions object associated with a given particle name
	 * @param type The name of the particle to lookup
	 * @return Returns the ParticleOptions object for the given type or null if the type cannot be found
	 */
	public ParticleOptions getParticleOptions(String type) {
		return entityParticles.get(type);
	}

	// This is true when the BukkitRunnable that handles particle spawning exists
	public boolean particleTaskRunning = false;
}
