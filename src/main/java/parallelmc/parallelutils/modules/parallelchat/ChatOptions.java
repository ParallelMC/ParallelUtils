package parallelmc.parallelutils.modules.parallelchat;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("ChatOptions")
public class ChatOptions implements ConfigurationSerializable {

	public static ChatOptions DEFAULT = new ChatOptions("<{DISPLAYNAME}> {MESSAGE}");

	private String format;

	public ChatOptions(String format) {
		this.format = format;
	}

	public ChatOptions(Map<String, Object> map) {
		this.format = (String) map.get("format");
	}

	public String getFormat() {
		return format;
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("format", format);
		return map;
	}

	public static ChatOptions deserialize(Map<String, Object> map) {
		return new ChatOptions(map);
	}
}
