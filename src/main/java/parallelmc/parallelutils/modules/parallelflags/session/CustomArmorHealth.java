package parallelmc.parallelutils.modules.parallelflags.session;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.world.gamemode.GameModes;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelflags.CustomFlagRegistry;
import parallelmc.parallelutils.modules.parallelflags.events.ParallelFlagsDeathListener;

import java.util.logging.Level;

public class CustomArmorHealth extends Handler {

	public static final Factory FACTORY = new Factory();

	public static class Factory extends Handler.Factory<CustomArmorHealth> {
		@Override
		public CustomArmorHealth create(Session session) {
			return new CustomArmorHealth(session);
		}
	}

	private long lastDamage = 0;
	private final CustomFlagRegistry customFlagRegistry;
	private final NamespacedKey key;

	public CustomArmorHealth(Session session) {
		super(session);
		customFlagRegistry = CustomFlagRegistry.getInstance();
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to initialize session handler. Plugin "
					+ Constants.PLUGIN_NAME + " does not exist!");
			key = null;
			return;
		}
		key = new NamespacedKey(plugin, "ParallelItem");
	}

	@Override
	public void tick(LocalPlayer player, ApplicableRegionSet set) {
		if (player.getHealth() <= 0) {
			return;
		}

		long now = System.currentTimeMillis();

		Integer damageAmount = set.queryValue(player, customFlagRegistry.getIntegerFlag("custom-armor-damage-amount"));
		Integer damageDelay = set.queryValue(player, customFlagRegistry.getIntegerFlag("custom-armor-damage-delay"));

		// Do this to see if we should bother first
		if (damageAmount == null || damageDelay == null || damageAmount == 0 || damageDelay < 0) {
			return;
		}
		if (getSession().isInvincible(player)
				|| (player.getGameMode() != GameModes.SURVIVAL && player.getGameMode() != GameModes.ADVENTURE)
				|| getSession().getManager().hasBypass(player, player.getWorld())) {
			// don't damage invincible players
			return;
		}

		BukkitPlayer bukkitPlayer;

		if (!(player instanceof BukkitPlayer) || key == null) {
			return;
		}

		bukkitPlayer = (BukkitPlayer) player;

		Integer helmVal = set.queryValue(player, customFlagRegistry.getIntegerFlag("wearing-custom-helm-damage"));

		boolean allowed = true;

		if (helmVal != null && helmVal > 0) { // The zero ensures that it exists. Setting it to null or 0 ignores
			ItemStack helm = bukkitPlayer.getPlayer().getInventory().getItem(EquipmentSlot.HEAD);
			Integer itemVal = -1;
			if (helm != null) {
				itemVal = helm.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
			}

			if (itemVal == null || !itemVal.equals(helmVal)) {
				allowed = false;
			}
		}

		Integer chestVal = set.queryValue(player, customFlagRegistry.getIntegerFlag("wearing-custom-chestplate-damage"));
		if (allowed && chestVal != null && chestVal > 0) {
			ItemStack chest = bukkitPlayer.getPlayer().getInventory().getItem(EquipmentSlot.CHEST);

			Integer itemVal = -1;
			if (chest != null) {
				itemVal = chest.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
			}

			if (itemVal == null || !itemVal.equals(chestVal)) {
				allowed = false;
			}
		}

		Integer leggingsVal = set.queryValue(player, customFlagRegistry.getIntegerFlag("wearing-custom-leggings-damage"));
		if (allowed && leggingsVal != null && leggingsVal > 0) {
			ItemStack leggings = bukkitPlayer.getPlayer().getInventory().getItem(EquipmentSlot.LEGS);

			Integer itemVal = -1;
			if (leggings != null) {
				itemVal = leggings.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
			}
			if (itemVal == null || !itemVal.equals(leggingsVal)) {
				allowed = false;
			}
		}

		Integer bootsVal = set.queryValue(player, customFlagRegistry.getIntegerFlag("wearing-custom-boots-damage"));
		if (allowed && bootsVal != null && bootsVal > 0) {
			ItemStack boots = bukkitPlayer.getPlayer().getInventory().getItem(EquipmentSlot.FEET);

			Integer itemVal = -1;
			if (boots != null) {
				itemVal = boots.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
			}
			if (itemVal == null || !itemVal.equals(bootsVal)) {
				allowed = false;
			}
		}

		if (allowed) return;

		if (damageDelay <= 0) {
			String deathMessage = set.queryValue(player, customFlagRegistry.getStringFlag("custom-armor-damage-death"));

			if (deathMessage != null) {
				ParallelFlagsDeathListener.setPlayerDeathMessage(bukkitPlayer.getName(),
						bukkitPlayer.getName() + " " + deathMessage);
			}

			EntityDamageEvent event = new EntityDamageEvent(bukkitPlayer.getPlayer(),
					EntityDamageEvent.DamageCause.CUSTOM, damageAmount);
			bukkitPlayer.getPlayer().setLastDamageCause(event);

			bukkitPlayer.getPlayer().damage(player.getMaxHealth());

			lastDamage = now;
			String message = set.queryValue(player, customFlagRegistry.getStringFlag("custom-armor-damage-message"));

			if (message != null) {
				player.printRaw(CommandUtils.replaceColorMacros(message));
			}
		} else if (now - lastDamage > damageDelay * 1000) {
			// clamp health between minimum and maximum
			if (bukkitPlayer.getPlayer().getHealth() - damageAmount <= 0) {
				String deathMessage = set.queryValue(player, customFlagRegistry.getStringFlag("custom-armor-damage-death"));

				if (deathMessage != null) {
					ParallelFlagsDeathListener.setPlayerDeathMessage(bukkitPlayer.getName(),
							bukkitPlayer.getName() + " " + deathMessage);
				}

				EntityDamageEvent event = new EntityDamageEvent(bukkitPlayer.getPlayer(),
						EntityDamageEvent.DamageCause.CUSTOM, damageAmount);
				bukkitPlayer.getPlayer().setLastDamageCause(event);
			}

			bukkitPlayer.getPlayer().damage(damageAmount);

			lastDamage = now;
			String message = set.queryValue(player, customFlagRegistry.getStringFlag("custom-armor-damage-message"));

			if (message != null) {
				player.printRaw(CommandUtils.replaceColorMacros(message));
			}
		}
	}
}
