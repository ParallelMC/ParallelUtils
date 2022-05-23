package parallelmc.parallelutils.modules.charms.data;

import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;
import parallelmc.parallelutils.modules.charms.helper.Types;

import java.util.HashMap;

public abstract class RunnableSettings extends IEffectSettings {
	public RunnableSettings(long delay, long period) {
		super(new HashMap<>());

		settings.put("delay", new EncapsulatedType(Types.LONG, delay));
		settings.put("period", new EncapsulatedType(Types.LONG, period));
	}
}
