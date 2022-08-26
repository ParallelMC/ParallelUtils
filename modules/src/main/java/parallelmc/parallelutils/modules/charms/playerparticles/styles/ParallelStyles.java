package parallelmc.parallelutils.modules.charms.playerparticles.styles;

import dev.esophose.playerparticles.event.ParticleStyleRegistrationEvent;
import dev.esophose.playerparticles.styles.ParticleStyle;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.ParallelUtils;

public class ParallelStyles implements Listener {

	public static final ParticleStyle KILL = new ParticleStyleKill();

	public static void initStyles(ParallelUtils puPlugin) {
		PluginManager pluginManager = Bukkit.getPluginManager();
		// Register event
		pluginManager.registerEvents(new ParallelStyles(), puPlugin);

		pluginManager.registerEvents((Listener) KILL, puPlugin);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onParticleStyleRegistration(ParticleStyleRegistrationEvent event) {
		event.registerEventStyle(KILL);
	}
}
