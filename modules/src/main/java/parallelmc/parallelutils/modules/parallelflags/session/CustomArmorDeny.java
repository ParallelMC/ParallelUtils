package parallelmc.parallelutils.modules.parallelflags.session;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.commands.CommandUtils;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.parallelflags.CustomFlagRegistry;

import java.util.Set;
import java.util.logging.Level;

public class CustomArmorDeny extends Handler {

	public static final Factory FACTORY = new Factory();
	public static class Factory extends Handler.Factory<CustomArmorDeny> {
		@Override
		public CustomArmorDeny create(Session session) {
			return new CustomArmorDeny(session);
		}
	}

	private static final long MESSAGE_THRESHOLD = 1000 * 2;

	private final CustomFlagRegistry customFlagRegistry;
	private long lastMessage;

	private final NamespacedKey key;


	public CustomArmorDeny(Session session) {
		super(session);
		customFlagRegistry = CustomFlagRegistry.getInstance();

		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			ParallelUtils.log(Level.SEVERE, "Unable to initialize session handler. Plugin "
					+ Constants.PLUGIN_NAME + " does not exist!");
			key = null;
			return;
		}
		key = new NamespacedKey(plugin, "ParallelItem");
	}

	@Override
	public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {

		BukkitPlayer bukkitPlayer;

		if (!(player instanceof BukkitPlayer) || key == null) {
			return false;
		}

		if (entered.size() == 0) return true;

		bukkitPlayer = (BukkitPlayer) player;

		Integer helmVal = toSet.queryValue(player, customFlagRegistry.getIntegerFlag("wearing-custom-helm-deny"));

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

		Integer chestVal = toSet.queryValue(player, customFlagRegistry.getIntegerFlag("wearing-custom-chestplate-deny"));
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

		Integer leggingsVal = toSet.queryValue(player, customFlagRegistry.getIntegerFlag("wearing-custom-leggings-deny"));
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

		Integer bootsVal = toSet.queryValue(player, customFlagRegistry.getIntegerFlag("wearing-custom-boots-deny"));
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

		if (!getSession().getManager().hasBypass(player, (World) to.getExtent()) && !allowed && moveType.isCancellable()) {
			String message = toSet.queryValue(player, customFlagRegistry.getStringFlag("custom-armor-deny-message"));

			if (message == null) {
				message = toSet.queryValue(player, Flags.ENTRY_DENY_MESSAGE);
			}

			long now = System.currentTimeMillis();

			if ((now - lastMessage) > MESSAGE_THRESHOLD && message != null && !message.isEmpty()) {
				player.printRaw(CommandUtils.replaceColorMacros(message));
				lastMessage = now;
			}

			return false;
		} else {
			return true;
		}
	}

}
