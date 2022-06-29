package parallelmc.parallelutils;

import org.jetbrains.annotations.NotNull;

/**
 * This doesn't do anything right now but I'm just gonna keep it here
 */
@Module
public interface ParallelModule
{
	public void onLoad();

	public void onEnable();

	public void onDisable();

	@NotNull
	public String getName();
}
