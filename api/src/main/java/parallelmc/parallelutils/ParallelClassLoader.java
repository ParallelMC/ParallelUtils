package parallelmc.parallelutils;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ParallelClassLoader extends URLClassLoader {

	private final List<String> loadedClasses = new ArrayList<>();

	public ParallelClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> result = null;
		try {
			result = super.loadClass(name, resolve);
		} finally {
			if (result != null) {
				loadedClasses.add(name);
			}
		}
		return result;
	}

	// Expose addURL
	public void addURL(URL url) {
		super.addURL(url);
	}

	public List<String> getLoadedClasses() {
		return loadedClasses;
	}
}
