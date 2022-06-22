package parallelmc.parallelutils.modules.parallelflags.session;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.MapFlag;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import parallelmc.parallelutils.modules.parallelflags.CustomFlagRegistry;

import java.util.Map;
import java.util.Set;

public class EffectFlagSession extends Handler {

	public static final EffectFlagSession.Factory FACTORY = new EffectFlagSession.Factory();

	public static class Factory extends Handler.Factory<EffectFlagSession> {
		@Override
		public EffectFlagSession create(Session session) {
			return new EffectFlagSession(session);
		}
	}

	private MapFlag<?, ?> effectFlag = null;

	/**
	 * Create a new handler.
	 *
	 * @param session The session
	 */
	public EffectFlagSession(Session session) {
		super(session);

		CustomFlagRegistry customFlagRegistry = CustomFlagRegistry.getInstance();

		Object effectObj = customFlagRegistry.getMiscFlag("effect");
		if (effectObj instanceof MapFlag val) {
			effectFlag = val;
		}
	}

	private long prev = 0;

	@Override
	public void tick(LocalPlayer player, ApplicableRegionSet set) {
		long now = System.currentTimeMillis();

		if (player instanceof BukkitPlayer bukkitPlayer) {
			if (now - prev > 4000) { // 4 seconds
				Map<?, ?> val = set.queryValue(player, effectFlag); // This should be a Map<String, Integer>

				if (val == null) return;

				Set<?> effects = val.keySet();

				for (Object e : effects) {
					if (e instanceof String effect) {
						if (val.get(effect) instanceof Integer strength) {
							PotionEffectType type = PotionEffectType.getByName(effect);

							if (type != null) {
								PotionEffect finalEffect = type.createEffect(20*20, strength);

								bukkitPlayer.getPlayer().removePotionEffect(type);
								bukkitPlayer.getPlayer().addPotionEffect(finalEffect);
							}
						}
					}
				}

				prev = now;
			}
		}
	}
}
