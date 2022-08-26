package parallelmc.parallelutils.modules.parallelflags;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import javax.annotation.Nullable;
import java.util.HashMap;

public class CustomFlagRegistry {

	private static CustomFlagRegistry instance;

	private final HashMap<String, StateFlag> stateFlags;
	private final HashMap<String, IntegerFlag> integerFlags;
	private final HashMap<String, DoubleFlag> doubleFlags;
	private final HashMap<String, StringFlag> stringFlags;
	private final HashMap<String, LocationFlag> locationFlags;

	private final HashMap<String, Object> miscFlags;

	//private final HashMap<String, MapFlag<Object, Object>> mapFlags;

	private CustomFlagRegistry() {
		stateFlags = new HashMap<>();
		integerFlags = new HashMap<>();
		doubleFlags = new HashMap<>();
		stringFlags = new HashMap<>();
		locationFlags = new HashMap<>();
		//mapFlags = new HashMap<>();
		miscFlags = new HashMap<>();
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

	public boolean addDoubleFlag(String name) {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			DoubleFlag flag = new DoubleFlag(name);
			registry.register(flag);
			doubleFlags.put(name, flag);
			return true;
		} catch (FlagConflictException e) {
			return false;
		}
	}

	@Nullable
	public DoubleFlag getDoubleFlag(String name) {
		return doubleFlags.get(name);
	}

	public boolean addStringFlag(String name) {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			StringFlag flag = new StringFlag(name);
			registry.register(flag);
			stringFlags.put(name, flag);
			return true;
		} catch (FlagConflictException e) {
			return false;
		}
	}

	@Nullable
	public StringFlag getStringFlag(String name) {return stringFlags.get(name);}

	public boolean addLocationFlag(String name) {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			LocationFlag flag = new LocationFlag(name);
			registry.register(flag);
			locationFlags.put(name, flag);
			return true;
		} catch (FlagConflictException e) {
			return false;
		}
	}

	@Nullable
	public LocationFlag getLocationFlag(String name) { return locationFlags.get(name); }

	/*
	// Screw this. If you want map flags, register them yourself.
	public <T, V> boolean addMapFlag(String name, Flag<T> key, Flag<V> val) {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			MapFlag<T, V> flag = new MapFlag<T, V>(name, key, val);
			registry.register(flag);
			mapFlags.put(name, flag);
			return true;
		} catch (FlagConflictException e) {
			return false;
		}
	}

	public <T, V> MapFlag<T, V> getMapFlag(String name) {
		MapFlag<?, ?> mapFlag = mapFlags.get(name);

		if (mapFlag.getKeyFlag() instanceof Flag<T>)

		if (mapFlag instanceof MapFlag<T, V>) return mapFlag;
		return ;
	}*/


	public <V, T extends Flag<V>> boolean addMiscFlag(String name, T flag) {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try {
			registry.register(flag);
			miscFlags.put(name, flag);
			return true;
		} catch (FlagConflictException e) {
			return false;
		}
	}

	@Nullable
	public Object getMiscFlag(String name) {
		return miscFlags.get(name);
	}

}
