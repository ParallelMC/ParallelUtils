package parallelmc.parallelutils.modules.charms.playerparticles.styles;

import dev.esophose.playerparticles.PlayerParticles;
import dev.esophose.playerparticles.manager.DataManager;
import dev.esophose.playerparticles.manager.ParticleManager;
import dev.esophose.playerparticles.particles.PParticle;
import dev.esophose.playerparticles.particles.PPlayer;
import dev.esophose.playerparticles.particles.ParticlePair;
import dev.esophose.playerparticles.styles.DefaultStyles;
import dev.esophose.playerparticles.styles.ParticleStyle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class ParticleStyleKill implements ParticleStyle, Listener {

	private final int MULTIPLIER = 15;

	@Override
	public List<PParticle> getParticles(ParticlePair particle, Location location) {
		List<PParticle> particles = new ArrayList<>();

		for (int i = 0; i < MULTIPLIER; i++) {
			particles.addAll(DefaultStyles.NORMAL.getParticles(particle, location));
		}

		return particles;
	}

	@Override
	public List<PParticle> getParticles(ParticlePair particle, Location location, Player player) {
		return getParticles(particle, location);
	}

	@Override
	public void updateTimers() {
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getInternalName() {
		return "kill";
	}

	@Override
	public Material getGuiIconMaterial() {
		return Material.BONE;
	}

	@Override
	public boolean canBeFixed() {
		return false;
	}

	@Override
	public boolean canToggleWithMovement() {
		return ParticleStyle.super.canToggleWithMovement();
	}

	@Override
	public boolean canToggleWithCombat() {
		return ParticleStyle.super.canToggleWithCombat();
	}

	@Override
	public double getFixedEffectOffset() {
		return ParticleStyle.super.getFixedEffectOffset();
	}

	@Override
	public boolean hasLongRangeVisibility() {
		return true;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKill(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();

		Player player = entity.getKiller();

		if (player == null) return;

		ParticleManager particleManager = PlayerParticles.getInstance().getManager(ParticleManager.class);

		PPlayer pplayer = PlayerParticles.getInstance().getManager(DataManager.class).getPPlayer(player.getUniqueId());

		if (pplayer == null) return;

		for (ParticlePair particle : pplayer.getActiveParticlesForStyle(ParallelStyles.KILL)) {
			Location loc = entity.getLocation().add(0, 1, 0);
			particleManager.displayParticles(pplayer, player.getWorld(), particle, ParallelStyles.KILL.getParticles(particle, loc), false);
		}
	}
}
