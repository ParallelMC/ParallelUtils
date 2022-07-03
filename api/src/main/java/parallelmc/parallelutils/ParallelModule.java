package parallelmc.parallelutils;

import org.jetbrains.annotations.NotNull;

public interface ParallelModule
{
	public void onLoad();

	public void onEnable();


	public void onDisable();

	/**
	 * This MUST remove all references to every class so it can be successfully unloaded
	 */
	public void onUnload();

	public default boolean canUnload() {
		return false;
	}

	@NotNull
	public String getName();
}
