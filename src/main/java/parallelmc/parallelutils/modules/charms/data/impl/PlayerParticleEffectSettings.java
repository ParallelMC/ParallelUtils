package parallelmc.parallelutils.modules.charms.data.impl;

import dev.esophose.playerparticles.particles.ParticleEffect;
import dev.esophose.playerparticles.particles.data.ColorTransition;
import dev.esophose.playerparticles.particles.data.OrdinaryColor;
import dev.esophose.playerparticles.particles.data.Vibration;
import dev.esophose.playerparticles.styles.ParticleStyle;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;

public class PlayerParticleEffectSettings extends IEffectSettings {

	public PlayerParticleEffectSettings(HashMap<String, EncapsulatedType> settings) {
		super(settings);
	}

	@NotNull
	@Override
	public HashMap<String, EncapsulatedType> getSettings() {
		return settings;
	}

	@NotNull
	@Override
	public HandlerType getType() {
		return HandlerType.PLAYER_PARTICLE;
	}

	@Nullable
	public ParticleEffect getEffect() {
		EncapsulatedType type = settings.get("effect");

		if (type == null) return null;

		if (type.getType() != Types.STRING) return null;

		String effect = (String) type.getVal();

		return ParticleEffect.fromName(effect);
	}

	@Nullable
	public ParticleStyle getStyle() {
		EncapsulatedType type = settings.get("style");

		if (type == null) return null;

		if (type.getType() != Types.STRING) return null;

		String style = (String) type.getVal();

		return ParticleStyle.fromName(style);
	}

	@Nullable
	public OrdinaryColor getColor() {
		EncapsulatedType type = settings.get("color");

		if (type == null) return null;

		if (type.getType() != Types.STRING) return null;

		String val = (String) type.getVal();

		return parseColor(val);
	}

	@Nullable
	public ColorTransition getColorTransition() {
		EncapsulatedType type = settings.get("transition");

		if (type == null) return null;

		if (type.getType() != Types.STRING) return null;

		String val = (String) type.getVal();

		String[] parts = val.split(",");

		if (parts.length != 2) return null;

		OrdinaryColor color1 = parseColor(parts[0]);
		OrdinaryColor color2 = parseColor(parts[1]);

		if (color1 == null || color2 == null) return null;

		return new ColorTransition(color1, color2);
	}

	@Nullable
	public Vibration getVibration() {
		EncapsulatedType type = settings.get("vibration");

		if (type == null) return null;

		if (type.getType() != Types.INT) return null;

		Integer val = (Integer) type.getVal();

		return new Vibration(val);
	}

	@Nullable
	public Material getMaterial() {
		EncapsulatedType type = settings.get("material");

		if (type == null) return null;

		if (type.getType() != Types.STRING) return null;

		String val = (String) type.getVal();

		return Material.getMaterial(val);
	}

	@Nullable
	private OrdinaryColor parseColor(String val) {
		String[] parts = val.split(",");

		if (parts.length != 3) return null;

		try {
			int r = Integer.parseInt(parts[0]);
			int g = Integer.parseInt(parts[1]);
			int b = Integer.parseInt(parts[2]);

			return new OrdinaryColor(r, g, b);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
