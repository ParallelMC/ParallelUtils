package parallelmc.parallelutils.modules.paralleltutorial;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config implements parallelmc.parallelutils.Config {
	@NotNull
	@Override
	public List<String> getHardDepends() {
		return Arrays.asList("ParallelChat");
	}

	@NotNull
	@Override
	public List<String> getSoftDepends() {
		return new ArrayList<>();
	}
}
