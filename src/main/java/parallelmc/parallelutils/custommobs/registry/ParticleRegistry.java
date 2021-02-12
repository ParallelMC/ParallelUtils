package parallelmc.parallelutils.custommobs.registry;

import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.custommobs.particles.ParticleOptions;

import java.util.HashMap;
import java.util.logging.Level;

public class ParticleRegistry {

	private HashMap<String, ParticleOptions> entityParticles;

	private static ParticleRegistry registry;

	private ParticleRegistry() {
		entityParticles = new HashMap<>();
	}

	public static ParticleRegistry getInstance() {
		if (registry == null) {
			registry = new ParticleRegistry();
		}

		return registry;
	}

	public void registerParticles(String type, ParticleOptions options) {
		Parallelutils.log(Level.INFO, "Registering particles for " + type);
		entityParticles.put(type, options);
	}

	public ParticleOptions getParticleOptions(String type) {
		return entityParticles.get(type);
	}

	public boolean particleTaskRunning = false;
}
