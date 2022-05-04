package parallelmc.parallelutils.modules.charms.handlers.impl;

import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import dev.esophose.playerparticles.particles.ParticleEffect;
import dev.esophose.playerparticles.particles.ParticlePair;
import dev.esophose.playerparticles.styles.DefaultStyles;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.events.PlayerSlotChangedEvent;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmApplyHandler;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;

import javax.naming.Name;
import java.util.HashMap;
import java.util.logging.Level;

public class CharmPlayerParticleHandler extends ICharmHandler<PlayerSlotChangedEvent> {

	private final Parallelutils puPlugin;
	private final ParallelCharms pCharms;
	private final PlayerParticlesAPI ppAPI;

	public CharmPlayerParticleHandler(Parallelutils puPlugin, ParallelCharms pCharms, PlayerParticlesAPI ppAPI) {
		super(PlayerSlotChangedEvent.class);
		this.puPlugin = puPlugin;
		this.pCharms = pCharms;
		this.ppAPI = ppAPI;
	}

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.PLAYER_PARTICLE;
	}

	@Override
	public void handle(PlayerSlotChangedEvent event, Player player, ItemStack item, CharmOptions options) {
		// Note: Options is null here! Following check is to make the IDE know that
		if (options != null) return;

		ItemStack remainingItem = event.getRemainingItem();
		ItemStack removedItem = event.getRemovedItem();

		if (remainingItem.getType() != Material.AIR) {
			// Check here for specific inventory slot if desired
			Charm remainingCharm = Charm.parseCharm(pCharms, remainingItem, player);

			if (remainingCharm != null) {

				if (removedItem.getType() != Material.AIR) {

					Charm removedCharm = Charm.parseCharm(pCharms, removedItem, player);

					if (removedCharm != null) {
						if (remainingCharm.getUUID().equals(removedCharm.getUUID())) return; // Item taking durability or something
					}
				}

				CharmOptions remainingOptions = remainingCharm.getOptions();

				if (remainingOptions != null) {
					final HashMap<HandlerType, IEffectSettings> effects = remainingOptions.getEffects();

					IEffectSettings settings = effects.get(HandlerType.PLAYER_PARTICLE);

					if (settings != null) {
						ItemMeta meta = remainingItem.getItemMeta();
						if (meta != null) {
							ParticlePair pair = ppAPI.addActivePlayerParticle(player, ParticleEffect.CLOUD, DefaultStyles.CUBE);

							if (pair == null) {
								Parallelutils.log(Level.WARNING, "Could not add active player particle for player " + player.getName());
								return;
							}

							int id = pair.getId();

							PersistentDataContainer pdc = meta.getPersistentDataContainer();

							NamespacedKey key = new NamespacedKey(puPlugin, "ParallelCharm.ppId");

							pdc.set(key, PersistentDataType.INTEGER, id);

							item.setItemMeta(meta);
						}
					}
				}
			}
		}

		if (removedItem.getType() != Material.AIR) {
			Charm removedCharm = Charm.parseCharm(pCharms, removedItem, player);

			if (removedCharm != null) {

				CharmOptions removedOptions = removedCharm.getOptions();

				if (removedOptions != null) {

					final HashMap<HandlerType, IEffectSettings> effects = removedOptions.getEffects();

					IEffectSettings settings = effects.get(HandlerType.PLAYER_PARTICLE);

					if (settings != null) {
						ItemMeta meta = removedItem.getItemMeta();

						if (meta != null) {
							PersistentDataContainer pdc = meta.getPersistentDataContainer();

							NamespacedKey key = new NamespacedKey(puPlugin, "ParallelCharm.ppId");

							Integer id = pdc.get(key, PersistentDataType.INTEGER);

							if (id == null) {
								Parallelutils.log(Level.WARNING, "Player " + player.getName() + " tried to remove a charm that doesn't exist!");
								return;
							}

							try {
								ppAPI.removeActivePlayerParticle(player, id);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							}

							pdc.remove(key);
						}
					}
				}
			}
		}
	}
}
