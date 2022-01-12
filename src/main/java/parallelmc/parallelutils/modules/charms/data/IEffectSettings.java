package parallelmc.parallelutils.modules.charms.data;

import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;

import java.util.HashMap;

public interface IEffectSettings {

	HashMap<String, EncapsulatedType> getSettings();


	default String string() {
		HashMap<String, EncapsulatedType> settings = getSettings();
		StringBuilder sb = new StringBuilder();

		sb.append("{\n\t");

		for (String n : settings.keySet()) {
			EncapsulatedType val = settings.get(n);

			sb.append(n);
			sb.append(": ");
			sb.append(val.getType().name());
			sb.append(", ");
			sb.append(val.getVal().toString());
			sb.append("\n\t");
		}
		sb.append("}");

		return sb.toString();
	}
}
