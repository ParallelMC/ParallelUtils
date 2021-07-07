package parallelmc.parallelutils.modules.parallelflags;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;

public class CustomFlagRegistry {

	private static CustomFlagRegistry instance;

	private final HashMap<String, StateFlag> stateFlags;
	private final HashMap<String, IntegerFlag> integerFlags;

	private CustomFlagRegistry() {
		stateFlags = new HashMap<>();
		integerFlags = new HashMap<>();
	}

	public static CustomFlagRegistry getInstance() {
		if (instance == null) {
			instance = new CustomFlagRegistry();
		}
		return instance;
	}

	public boolean addStateflag(String name, boolean defaultVal) {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			StateFlag flag = new StateFlag(name, defaultVal);
			registry.register(flag);
			stateFlags.put(name, flag);
			return true;
		} catch (FlagConflictException e) {
			return false;
		}
	}

	@Nullable
	public StateFlag getStateFlag(String name) {
		return stateFlags.get(name);
	}

	public boolean addIntegerFlag(String name) {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			IntegerFlag flag = new IntegerFlag(name);
			registry.register(flag);
			integerFlags.put(name, flag);
			return true;
		} catch (FlagConflictException e) {
			return false;
		}
	}

	@Nullable
	public IntegerFlag getIntegerFlag(String name) {
		return integerFlags.get(name);
	}
}
