package parallelmc.parallelutils;

import org.jetbrains.annotations.NotNull;

/**
 * A class to make manipulating versions easier
 */
public class Version implements Comparable<Version> {

	private final int major;
	private final int minor;
	private final int hotfix;
	private final String flavor;

	/**
	 * Create a Version object with a given major, minor, hotfix, and flavor text
	 * @param major The major version of this Version object
	 * @param minor The minor version of this Version object
	 * @param hotfix The hotfix version of this Version object
	 * @param flavor The flavor text of this Version object
	 */
	public Version(int major, int minor, int hotfix, String flavor) {
		this.major = major;
		this.minor = minor;
		this.hotfix = hotfix;
		this.flavor = flavor;
	}

	/**
	 * Create a Version object with a given major, minor, and hotfix
	 * @param major The major version of this Version object
	 * @param minor The minor version of this Version object
	 * @param hotfix The hotfix version of this Version object
	 */
	public Version(int major, int minor, int hotfix) {
		this(major, minor, hotfix, "");
	}

	/**
	 * Create a Version object with a given major and minor version
	 * @param major The major version of this Version object
	 * @param minor The minor version of this Version object
	 */
	public Version(int major, int minor) {
		this(major, minor, 0);
	}

	/**
	 * Create a Version object with a given major version
	 * @param major The major version of this Version object
	 */
	public Version(int major) {
		this(major, 0);
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getHotfix() {
		return hotfix;
	}

	public String getFlavor() {
		return flavor;
	}

	public String toString() {
		String flavorMod = "";
		if (!flavor.equals("")) {
			flavorMod = "-" + flavor;
		}
		return "v" + major + "." + minor + "." + hotfix + flavorMod;
	}

	@Override
	public int compareTo(@NotNull Version o) {
		if (major > o.major) {
			return 1;
		} else if (major < o.major) {
			return -1;
		} else {
			if (minor > o.minor) {
				return 1;
			} else if (minor < o.minor) {
				return -1;
			} else {
				return Integer.compare(hotfix, o.hotfix);
			}
		}
	}
}
