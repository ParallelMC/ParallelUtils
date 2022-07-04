package parallelmc.parallelutils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Config {

	@NotNull
	public List<String> getHardDepends();

	@NotNull
	public List<String> getSoftDepends();
}
