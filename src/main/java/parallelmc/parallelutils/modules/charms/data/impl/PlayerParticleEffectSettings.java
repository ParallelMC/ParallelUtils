package parallelmc.parallelutils.modules.charms.data.impl;

import dev.esophose.playerparticles.particles.ParticleEffect;
import dev.esophose.playerparticles.styles.ParticleStyle;
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
}
