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

	public boolean canUnload() {
		return false;
	}

	@NotNull
	public abstract String getName();

	public URLClassLoader getClassLoader() {
		return classLoader;
	}
}
