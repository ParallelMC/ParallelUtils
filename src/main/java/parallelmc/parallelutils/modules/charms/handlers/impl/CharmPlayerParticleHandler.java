package parallelmc.parallelutils.modules.charms.handlers.impl;

import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import dev.esophose.playerparticles.particles.ParticleEffect;
import dev.esophose.playerparticles.particles.ParticlePair;
import dev.esophose.playerparticles.particles.data.ColorTransition;
import dev.esophose.playerparticles.particles.data.OrdinaryColor;
import dev.esophose.playerparticles.particles.data.Vibration;
import dev.esophose.playerparticles.styles.DefaultStyles;
import dev.esophose.playerparticles.styles.ParticleStyle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.ParallelCharms;
import parallelmc.parallelutils.modules.charms.data.Charm;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.data.impl.PlayerParticleEffectSettings;
import parallelmc.parallelutils.modules.charms.events.PlayerSlotChangedEvent;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmApplyHandler;
import parallelmc.parallelutils.modules.charms.handlers.ICharmHandler;
import parallelmc.parallelutils.modules.charms.util.Util;

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

		if (remainingItem != null && remainingItem.getType() != Material.AIR) {

			// Check here for specific inventory slot if desired
			PlayerInventory inventory = player.getInventory();

			// If in armor, off-hand, or held item

			int rawHeld = inventory.getHeldItemSlot() + 36; // Crude way to "convert" an inventory slot to a raw slot

			// Can only get raw slots from event :/

			boolean isArmor = Util.isArmor(item);

			if ((isArmor && (event.getRawSlot() >= 5 && event.getRawSlot() <= 8)) || (!isArmor && (event.getRawSlot() == 45 ||
					event.getRawSlot() == rawHeld))) {

				Charm remainingCharm = Charm.parseCharm(pCharms, remainingItem, player);

				if (remainingCharm != null) {

					if (removedItem != null && removedItem.getType() != Material.AIR) {

						Charm removedCharm = Charm.parseCharm(pCharms, removedItem, player);

						if (removedCharm != null) {
							if (remainingCharm.getUUID().equals(removedCharm.getUUID())) {
								return; // Item taking durability or something
							}
						}
					}

					CharmOptions remainingOptions = remainingCharm.getOptions();

					if (remainingOptions != null) {
						final HashMap<HandlerType, IEffectSettings> effects = remainingOptions.getEffects();

						IEffectSettings settings = effects.get(HandlerType.PLAYER_PARTICLE);

						if (settings != null) {
							ItemMeta meta = remainingItem.getItemMeta();
							if (meta != null) {
								// Parse settings

								if (settings.getType() == HandlerType.PLAYER_PARTICLE && settings instanceof PlayerParticleEffectSettings ppSettings) {

									ParticleEffect effect = ppSettings.getEffect();
									ParticleStyle style = ppSettings.getStyle();

									if (effect == null || style == null) {
										Parallelutils.log(Level.WARNING, "Unknown effect or style!");
										return;
									}

									ParticlePair pair = null;

									if (effect.hasProperty(ParticleEffect.ParticleProperty.COLORABLE)) {
										OrdinaryColor color = ppSettings.getColor();
										if (color != null) {
											pair = ppAPI.addActivePlayerParticle(player, effect, style, color);
										}
									} else if (effect.hasProperty(ParticleEffect.ParticleProperty.COLORABLE_TRANSITION)) {
										ColorTransition colorTransition = ppSettings.getColorTransition();
										if (colorTransition != null) {
											pair = ppAPI.addActivePlayerParticle(player, effect, style, colorTransition);
										}
									} else if (effect.hasProperty(ParticleEffect.ParticleProperty.VIBRATION)) {
										Vibration vibration = ppSettings.getVibration();
										if (vibration != null) {
											pair = ppAPI.addActivePlayerParticle(player, effect, style, vibration);
										}
									} else if (effect.hasProperty(ParticleEffect.ParticleProperty.REQUIRES_MATERIAL_DATA)) {
										 Material material = ppSettings.getMaterial();
										 if (material != null) {
											 pair = ppAPI.addActivePlayerParticle(player, effect, style, material);
										 }
									}
									if (pair == null) {
										pair = ppAPI.addActivePlayerParticle(player, effect, style);
									}

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
			}
		}

		if (removedItem != null && removedItem.getType() != Material.AIR) {
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

							pdc.remove(key);

							removedItem.setItemMeta(meta);

							try {
								ppAPI.removeActivePlayerParticle(player, id);
							} catch (IllegalArgumentException e) {
								//Parallelutils.log(Level.INFO, "Particle does not exist!");
							}
						}
					}
				}
			}
		}
	}
}
