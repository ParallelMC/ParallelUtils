package parallelmc.parallelutils.modules.charms.data.impl;

import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;

public class BasicMessageEffectSettings extends IEffectSettings {

	public BasicMessageEffectSettings(String miniMessage) {
		super(new HashMap<>());
		settings.put("message", new EncapsulatedType(Types.STRING, miniMessage));
	}

	@Override
	public HashMap<String, EncapsulatedType> getSettings() {
		return settings;
	}

	@Override
	public HandlerType getType() {
		return HandlerType.NONE;
	}
}
