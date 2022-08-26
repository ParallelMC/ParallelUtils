package parallelmc.parallelutils.modules.parallelchat;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Config implements parallelmc.parallelutils.Config {
	@Override
	public @NotNull List<String> getHardDepends() {
		return List.of();
	}

	@Override
	public @NotNull List<String> getSoftDepends() {
		return List.of("DiscordIntegration");
	}
}
