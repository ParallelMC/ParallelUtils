package parallelmc.parallelutils.modules.charms.data;

import org.bukkit.Bukkit;
import parallelmc.parallelutils.modules.charms.data.impl.CommandEffectSettings;
import parallelmc.parallelutils.modules.charms.data.impl.GenericEffectSettings;
import parallelmc.parallelutils.modules.charms.data.impl.PlayerParticleEffectSettings;
import parallelmc.parallelutils.modules.charms.data.impl.RunnableCommandSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;

import java.util.HashMap;

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
			case COMMAND_KILL, COMMAND_HIT, COMMAND_APPLY -> {
				return new CommandEffectSettings(settings);
			}
			case COMMAND_RUNNABLE -> {
				return new RunnableCommandSettings(settings);
			}
			default -> {
				return new GenericEffectSettings(settings);
			}
		}
	}


}
