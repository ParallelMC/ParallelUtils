package parallelmc.parallelutils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.net.URLClassLoader;
import java.util.List;

public abstract class ParallelModule
{

	protected ParallelClassLoader classLoader;
	protected List<String> dependents; // Any modules that depend on this module. Used in unloading

	public ParallelModule(ParallelClassLoader classLoader, List<String> dependents) {
		this.classLoader = classLoader;
		this.dependents = List.copyOf(dependents); // Ensure it's immutable
	}

	public abstract void onLoad();

	public abstract void onEnable();


	public abstract void onDisable();

	/**
	 * This MUST remove all references to every class so it can be successfully unloaded
	 */
	public abstract void onUnload();

	/**
	 * Overload this method and set to true when {@link #onUnload() onUnload} properly removes references to all classes in the module
	 * @return True if this module can be safely and properly unloaded. False otherwise
	 */
	public boolean canUnload() {
		return false;
	}

	@NotNull
	public abstract String getName();

	public ParallelClassLoader getClassLoader() {
		return classLoader;
	}

	@NotNull
	@Unmodifiable
	public List<String> getDependents() {
		return dependents;
	}
}
