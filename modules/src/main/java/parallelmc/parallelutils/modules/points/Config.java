package parallelmc.parallelutils.modules.points;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config implements parallelmc.parallelutils.Config {
	@Override
	public @NotNull List<String> getHardDepends() {
		return List.of("ParallelChat");
	}

	@Override
	public @NotNull List<String> getSoftDepends() {
		return new ArrayList<>();
	}
}
