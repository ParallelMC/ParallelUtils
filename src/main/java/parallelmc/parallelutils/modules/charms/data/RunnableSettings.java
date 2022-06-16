package parallelmc.parallelutils.modules.charms.data;

import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;

public abstract class RunnableSettings extends IEffectSettings {
	public RunnableSettings(HashMap<String, EncapsulatedType> settings) {
		super(settings);
	}

	public long getDelay() {
		EncapsulatedType type = settings.get("delay");

		if (type == null) return 0;

		if (type.getType() != Types.LONG) return 0;

		return (long) type.getVal();
	}

	public long getPeriod() {
		EncapsulatedType type = settings.get("period");

		if (type == null) return 0;

		if (type.getType() != Types.LONG) return 0;

		return (long) type.getVal();
	}
}
