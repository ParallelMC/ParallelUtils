package parallelmc.parallelutils;

import org.jetbrains.annotations.NotNull;

public interface ParallelModule
{
	public void onLoad();

	public void onEnable();

	public void onDisable();

	@NotNull
	public String getName();
}
