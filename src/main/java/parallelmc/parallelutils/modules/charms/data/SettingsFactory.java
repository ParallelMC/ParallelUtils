package parallelmc.parallelutils.modules.charms.data;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.charms.data.impl.GenericEffectSettings;
import parallelmc.parallelutils.modules.charms.data.impl.PlayerParticleEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;

import java.util.HashMap;
import java.util.logging.Level;

public class SettingsFactory {

	private final HandlerType type;

	public SettingsFactory(HandlerType type) {
		this.type = type;
	}

	public IEffectSettings getSettings(HashMap<String, EncapsulatedType> settings) {
		switch (type) {
			case PLAYER_PARTICLE -> {
				if (Bukkit.getPluginManager().isPluginEnabled("PlayerParticles")) {
					return new PlayerParticleEffectSettings(settings);
				} else {
					return new GenericEffectSettings(settings);
				}
			}
			default -> {
				return new GenericEffectSettings(settings);
			}
		}
	}


}
