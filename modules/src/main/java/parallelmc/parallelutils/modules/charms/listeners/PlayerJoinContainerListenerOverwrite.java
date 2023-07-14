package parallelmc.parallelutils.modules.charms.listeners;

import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.charms.data.NonNullListRemember;
import parallelmc.parallelutils.modules.charms.events.PlayerSlotChangedEvent;

import java.lang.reflect.Field;

public class PlayerJoinContainerListenerOverwrite implements Listener {

	private final Field containerSynchronizer;
	private final Field containerListener;

	public PlayerJoinContainerListenerOverwrite() {
		Field field;
		Field field1;
		try {
			field = ServerPlayer.class.getDeclaredField("cW");
			field.setAccessible(true);
			field1 = ServerPlayer.class.getDeclaredField("cX");
			field1.setAccessible(true);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			field = null;
			field1 = null;
		}

		containerSynchronizer = field;
		containerListener = field1;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		CraftPlayer craftPlayer = (CraftPlayer) player;

		ServerPlayer serverPlayer = craftPlayer.getHandle();

		try {

			ContainerSynchronizer oldSynchronizer = (ContainerSynchronizer) containerSynchronizer.get(serverPlayer);

			if (oldSynchronizer != null) {
				containerSynchronizer.set(serverPlayer, new ContainerSynchronizer() {
					@Override
					public void sendInitialData(@NotNull AbstractContainerMenu handler, @NotNull NonNullList<ItemStack> stacks, @NotNull ItemStack cursorStack, @NotNull int[] properties) {
						handler.lastSlots = NonNullListRemember.of(handler.lastSlots);
						oldSynchronizer.sendInitialData(handler, stacks, cursorStack, properties);
					}

					@Override
					public void sendSlotChange(@NotNull AbstractContainerMenu handler, int slot, @NotNull ItemStack stack) {
						oldSynchronizer.sendSlotChange(handler, slot, stack);
					}

					@Override
					public void sendCarriedChange(@NotNull AbstractContainerMenu handler, @NotNull ItemStack stack) {
						oldSynchronizer.sendCarriedChange(handler, stack);
					}

					@Override
					public void sendDataChange(@NotNull AbstractContainerMenu handler, int property, int value) {
						oldSynchronizer.sendDataChange(handler, property, value);
					}
				});
			}

			ContainerListener oldListener = (ContainerListener) containerListener.get(serverPlayer);

			if (oldListener != null) {

				containerListener.set(serverPlayer, new ContainerListener() {
					@Override
					public void slotChanged(@NotNull AbstractContainerMenu handler, int slotId, @NotNull ItemStack stack) {
						try {
							Slot slot = handler.getSlot(slotId);

							if (!(slot instanceof ResultSlot)  && slot.container == serverPlayer.getInventory()) {
								// Should always be true
								if (handler.lastSlots instanceof NonNullListRemember<ItemStack> oldSlots) {
									// This was an actual interaction with the player's inventory

									ItemStack old = oldSlots.getOld(slotId);

									org.bukkit.inventory.ItemStack oldBukkit = null;

									if (old != null) {
										oldBukkit = old.getBukkitStack();
									}

									// stack is the item left in the slot (either item added or whatever was left)
									PlayerSlotChangedEvent slotChangedEvent = new PlayerSlotChangedEvent(craftPlayer, oldBukkit, slot.getItem().getBukkitStack(), slotId);
									Bukkit.getPluginManager().callEvent(slotChangedEvent);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						oldListener.slotChanged(handler, slotId, stack);
					}

					@Override
					public void dataChanged(@NotNull AbstractContainerMenu handler, int property, int value) {
						oldListener.dataChanged(handler, property, value);
					}
				});
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
