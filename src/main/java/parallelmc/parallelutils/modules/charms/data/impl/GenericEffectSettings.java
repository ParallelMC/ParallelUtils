package parallelmc.parallelutils.modules.charms.data.impl;

import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;

import java.util.HashMap;

public class GenericEffectSettings extends IEffectSettings {

	public GenericEffectSettings(HashMap<String, EncapsulatedType> settings) {
		super(settings);
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
