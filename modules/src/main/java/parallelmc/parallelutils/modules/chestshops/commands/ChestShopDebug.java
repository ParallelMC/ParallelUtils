package parallelmc.parallelutils.modules.chestshops.commands;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.chestshops.ChestShops;
import parallelmc.parallelutils.modules.chestshops.Shop;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import net.kyori.adventure.text.Component;

import java.util.List;

public class ChestShopDebug extends ChestShopCommand {
    private final String USAGE = "/chestshop debug";

    public ChestShopDebug() { super("debug", "Prints debug info for the ChestShop you are looking at."); }

    @Override
    public boolean execute(@NotNull Player player, @NotNull Command command, @NotNull String[] args) {
        if (args.length != 1) {
            player.sendMessage(USAGE);
            return false;
        }

        Block target = player.getTargetBlock(null, 6);
        if (target instanceof Sign sign) {
            List<Shop> shops = ChestShops.get().getAllShopsFromSignPos(sign.getLocation());
            if (shops.isEmpty()) {
                ParallelChat.sendParallelMessageTo(player, "No ChestShop data found at this location! This sign is safe to delete.");
                return true;
            }
            for (Shop shop : shops) {
                OfflinePlayer owner = Bukkit.getOfflinePlayer(shop.owner());

                Component text = Component.text("----------------------\n", NamedTextColor.RED)
                        .append(Component.text("ChestShop Debug Info:\n", NamedTextColor.YELLOW))
                        .append(Component.text("----------------------\n", NamedTextColor.RED))
                        .append(Component.text("Shop ID: " + shop.id().toString(), NamedTextColor.AQUA)).append(Component.newline())
                        .append(Component.text("Owner:" + owner.getName(), NamedTextColor.AQUA)).append(Component.newline())
                        .append(Component.text(String.format("Chest Position: %d %d %d", shop.chestPos().getBlockX(), shop.chestPos().getBlockY(), shop.chestPos().getBlockZ()), NamedTextColor.AQUA)).append(Component.newline())
                        .append(Component.text(String.format("Sign Position: %d %d %d", shop.signPos().getBlockX(), shop.signPos().getBlockY(), shop.signPos().getBlockZ()), NamedTextColor.AQUA)).append(Component.newline())
                        .append(Component.text("Sold Item: " + shop.item(), NamedTextColor.AQUA)).append(Component.newline())
                        .append(Component.text("Buy Amount: " + shop.buyAmt() + " diamonds", NamedTextColor.AQUA)).append(Component.newline())
                        .append(Component.text("Sell Amount: " + shop.sellAmt() + " items", NamedTextColor.AQUA)).append(Component.newline());
                player.sendMessage(text);
            }
            if (shops.size() > 1) {
                player.sendMessage(Component.text("WARNING: Multiple shops found at this location!", NamedTextColor.RED, TextDecoration.BOLD));
            }

        }
        else {
            ParallelChat.sendParallelMessageTo(player, "Please look at a ChestShop sign and run the command again.");
        }
        return true;
    }

    @Override
    public List<String> getTabComplete(@NotNull Player player, @NotNull String[] args) {
        return null;
    }
}
