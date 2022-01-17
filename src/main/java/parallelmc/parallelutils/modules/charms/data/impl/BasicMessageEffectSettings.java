package parallelmc.parallelutils.modules.charms.data.impl;

import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;

public class BasicMessageEffectSettings implements IEffectSettings {

	private final HashMap<String, EncapsulatedType> settings;

	public BasicMessageEffectSettings(String miniMessage) {
		settings = new HashMap<>();
		settings.put("message", new EncapsulatedType(Types.STRING, miniMessage));
	}

	@Override
	public HashMap<String, EncapsulatedType> getSettings() {
		return settings;
	}
}
