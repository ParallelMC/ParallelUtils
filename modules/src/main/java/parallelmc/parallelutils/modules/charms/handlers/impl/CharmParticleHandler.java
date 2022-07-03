package parallelmc.parallelutils.modules.charms.handlers.impl;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.handlers.ICharmRunnableHandler;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;
import java.util.logging.Level;

public class CharmParticleHandler extends ICharmRunnableHandler {
	@Override
	public HandlerType getHandlerType() {
		return HandlerType.PARTICLE;
	}

	@Nullable
	@Override
	public BukkitRunnable getRunnable(Player player, ItemStack item, CharmOptions options) {

		ParallelUtils.log(Level.INFO, "GET");

		HashMap<HandlerType, IEffectSettings> effects = options.getEffects();
		IEffectSettings settings = effects.get(HandlerType.PARTICLE);

		HashMap<String, EncapsulatedType> settingsMap = settings.getSettings();

		// Particle type

		EncapsulatedType particleType = settingsMap.get("particle");

		if (particleType == null) {
			return null;
		}

		if (particleType.getType() != Types.STRING) {
			return null;
		}

		String particleName = (String) particleType.getVal();

		Particle particle = null;

		try {
			particle = Particle.valueOf(particleName);
		} catch (IllegalArgumentException e) {
			ParallelUtils.log(Level.WARNING, "Invalid particle name!");
			e.printStackTrace();
			return null;
		}

		// General stuff

		ParticleBuilder builder = new ParticleBuilder(particle);
		builder.allPlayers(); // This could be changed later if desired
		builder.source(player);
		//builder.force(true); // ???

		// Color

		EncapsulatedType colorRType = settingsMap.get("colorR");
		EncapsulatedType colorGType = settingsMap.get("colorG");
		EncapsulatedType colorBType = settingsMap.get("colorB");

		if (colorRType != null && colorGType != null && colorBType != null) {
			if (colorRType.getType() == Types.INT && colorGType.getType() == Types.INT && colorBType.getType() == Types.INT) {
				Integer colorR = (Integer) colorRType.getVal();
				Integer colorG = (Integer) colorGType.getVal();
				Integer colorB = (Integer) colorBType.getVal();

				if (colorR != null && colorG != null && colorB != null) {
					builder.color(colorR, colorG, colorB);
				}
			}
		}

		// Count
		EncapsulatedType countType = settingsMap.get("count");

		if (countType != null) {
			if (countType.getType() == Types.INT) {
				Integer count = (Integer) countType.getVal();

				if (count != null) {
					builder.count(count);
				}
			}
		}

		// Offset

		EncapsulatedType offsetXType = settingsMap.get("offsetX");
		EncapsulatedType offsetYType = settingsMap.get("offsetY");
		EncapsulatedType offsetZType = settingsMap.get("offsetZ");

		if (offsetXType != null && offsetYType != null && offsetZType != null) {
			if (offsetXType.getType() == Types.INT && offsetYType.getType() == Types.INT && offsetZType.getType() == Types.INT) {
				Integer offsetX = (Integer) offsetXType.getVal();
				Integer offsetY = (Integer) offsetYType.getVal();
				Integer offsetZ = (Integer) offsetZType.getVal();

				if (offsetX != null && offsetY != null && offsetZ != null) {
					builder.offset(offsetX, offsetY, offsetZ);
				}
			}
		}

		// Extra

		EncapsulatedType extraType = settingsMap.get("extra");

		if (extraType != null) {
			if (extraType.getType() == Types.DOUBLE) {
				Double extra = (Double) extraType.getVal();

				if (extra != null) {
					builder.extra(extra);
				}
			}
		}

		// Data

		switch (particle) {
			case ITEM_CRACK, BLOCK_CRACK, BLOCK_DUST, FALLING_DUST, BLOCK_MARKER -> {
				// Takes Material as input

				EncapsulatedType dataMatType = settingsMap.get("dataMaterial");

				if (dataMatType != null) {
					if (dataMatType.getType() == Types.STRING) {
						String dataMat = (String) dataMatType.getVal();

						if (dataMat != null) {
							try {
								Material material = Material.valueOf(dataMat);

								if (particle == Particle.ITEM_CRACK) {
									if (material.isItem()) {
										ItemStack itemStack = new ItemStack(material);

										builder.data(itemStack);
									} else {
										ParallelUtils.log(Level.WARNING, "Material is not item!");
									}
								} else {
									if (material.isBlock()) {

										EncapsulatedType dataBlockDataType = settingsMap.get("dataBlockDataType");

										if (dataBlockDataType.getType() == Types.STRING) {
											String blockData = (String) dataBlockDataType.getVal();

											BlockData data = CraftBlockData.newData(material, blockData);

											builder.data(data);
										}
									} else {
										ParallelUtils.log(Level.WARNING, "Material is not block!");
									}
								}
							} catch (IllegalArgumentException e) {
								ParallelUtils.log(Level.WARNING, "Illegal Material for dataMaterial!");
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				ParallelUtils.log(Level.WARNING, "" + this.getTaskId());
				builder.location(player.getLocation());
				builder.spawn();
			}
		};
		return runnable;
	}
}
