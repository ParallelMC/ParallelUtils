package parallelmc.parallelutils;

import org.jetbrains.annotations.NotNull;

import java.net.URLClassLoader;

public abstract class ParallelModule
{

	protected URLClassLoader classLoader;

	public ParallelModule(URLClassLoader classLoader) {
		this.classLoader = classLoader;
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

	public URLClassLoader getClassLoader() {
		return classLoader;
	}
}
