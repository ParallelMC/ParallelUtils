package parallelmc.parallelutils.modules.charms.data.impl;

import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	public List<String> getCommands() {
		EncapsulatedType type = settings.get("command");

		if (type == null) return null;

		if (type.getType() != Types.STRING) return null;

		String base = (String) type.getVal();

		List<String> commands = new ArrayList<>();
		commands.add(base);

		int index = 1;
		while (true) {
			EncapsulatedType iType = settings.get("command" + index);

			if (iType == null) break;

			if (iType.getType() != Types.STRING) break;

			String val = (String) iType.getVal();

			commands.add(val);
			index++;
		}

		return commands;
	}
}
