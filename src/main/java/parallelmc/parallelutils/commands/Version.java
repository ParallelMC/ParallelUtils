package parallelmc.parallelutils.commands;

import org.jetbrains.annotations.NotNull;

public class Version implements Comparable{

	private int major;
	private int minor;
	private int hotfix;
	private String flavor;

	public Version(int major, int minor, int hotfix, String flavor) {
		this.major = major;
		this.minor = minor;
		this.hotfix = hotfix;
		this.flavor = flavor;
	}

	public Version(int major, int minor, int hotfix) {
		this(major, minor, hotfix, "");
	}

	public Version(int major, int minor) {
		this(major, minor, 0);
	}

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
	public int compareTo(@NotNull Object o) {
		if (o instanceof Version) {
			Version other = (Version)o;

			if (major > other.major) {
				return 1;
			} else if (major < other.major) {
				return -1;
			} else {
				if (minor > other.minor) {
					return 1;
				} else if (minor < other.minor) {
					return -1;
				} else {
					if (hotfix > other.hotfix) {
						return 1;
					} else if (hotfix < other.hotfix){
						return -1;
					} else {
						return 0; // Ignore flavor because it's weird
					}
				}
			}
		}
		return -9999;
	}
}
