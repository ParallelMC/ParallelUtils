package parallelmc.parallelutils.modules.charms.data;

import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;

import java.util.HashMap;

public class GenericEffectSettings implements IEffectSettings{

	private final HashMap<String, EncapsulatedType> settings;

	public GenericEffectSettings(HashMap<String, EncapsulatedType> settings) {
		this.settings = settings;
	}

	@Override
	public HashMap<String, EncapsulatedType> getSettings() {
		return settings;
	}
}
