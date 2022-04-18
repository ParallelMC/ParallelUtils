package parallelmc.parallelutils.modules.charms.handlers.impl;

import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import dev.esophose.playerparticles.particles.ParticleEffect;
import dev.esophose.playerparticles.particles.ParticlePair;
import dev.esophose.playerparticles.styles.DefaultStyles;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmApplyHandler;

import java.util.logging.Level;

public class CharmPlayerParticleHandler extends ICharmApplyHandler {

	private final Parallelutils puPlugin;
	private final PlayerParticlesAPI ppAPI;

	public CharmPlayerParticleHandler(Parallelutils puPlugin, PlayerParticlesAPI ppAPI) {
		this.puPlugin = puPlugin;
		this.ppAPI = ppAPI;
	}

	@Override
	public void apply(Player player, @NotNull ItemStack item, @NotNull CharmOptions options) {
		ParticlePair pair = ppAPI.addActivePlayerParticle(player, ParticleEffect.CLOUD, DefaultStyles.CUBE);

		if (pair == null) {
			Parallelutils.log(Level.WARNING, "Could not add active player particle for player " + player.getName());
			return;
		}

		int id = pair.getId();

		ItemMeta meta = item.getItemMeta();

		if (meta == null) return;

		PersistentDataContainer pdc = meta.getPersistentDataContainer();

		NamespacedKey key = new NamespacedKey(puPlugin, "ParallelCharm.ppId");

		pdc.set(key, PersistentDataType.INTEGER, id);

		item.setItemMeta(meta);
	}

	@Override
	public void remove(Player player, ItemStack item, CharmOptions options) {
		ItemMeta meta = item.getItemMeta();

		if (meta == null) return;

		PersistentDataContainer pdc = meta.getPersistentDataContainer();

		NamespacedKey key = new NamespacedKey(puPlugin, "ParallelCharm.ppId");

		Integer id = pdc.get(key, PersistentDataType.INTEGER);

		if (id == null) {
			Parallelutils.log(Level.WARNING, "Player " + player.getName() + " tried to remove a charm that doesn't exist!");
			return;
		}

		ppAPI.removeActivePlayerParticle(player, id);
	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.PLAYER_PARTICLE;
	}
}
