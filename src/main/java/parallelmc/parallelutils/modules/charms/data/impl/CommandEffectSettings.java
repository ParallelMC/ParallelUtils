package parallelmc.parallelutils.modules.charms.data.impl;

import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;

public class CommandEffectSettings extends IEffectSettings {
	public CommandEffectSettings(HashMap<String, EncapsulatedType> settings) {
		super(settings);
	}

	@Override
	public HashMap<String, EncapsulatedType> getSettings() {
		return settings;
	}

	@Override
	public HandlerType getType() {
		return null;
	}

	public String getCommand() {
		EncapsulatedType type = settings.get("command");

		if (type == null) return null;

		if (type.getType() != Types.STRING) return null;

		String command = (String) type.getVal();

		return command;
	}
}
