package parallelmc.parallelutils.modules.charms.data.impl;

import parallelmc.parallelutils.modules.charms.data.RunnableSettings;
import parallelmc.parallelutils.modules.charms.handlers.HandlerType;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;

public class RunnableCommandSettings extends RunnableSettings {

	public RunnableCommandSettings(HashMap<String, EncapsulatedType> settings) {
		super(settings);
	}

	@Override
	public HashMap<String, EncapsulatedType> getSettings() {
		return settings;
	}

	@Override
	public HandlerType getType() {
		return HandlerType.COMMAND_RUNNABLE;
	}

	public String getCommand() {
		EncapsulatedType type = settings.get("command");

		if (type == null) return null;

		if (type.getType() != Types.STRING) return null;

		return (String) type.getVal();
	}
}
