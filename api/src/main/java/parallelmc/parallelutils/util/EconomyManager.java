package parallelmc.parallelutils.util;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import parallelmc.parallelutils.ParallelUtils;

import java.util.logging.Level;

public class EconomyManager {
    private static EconomyManager Instance;

    public static EconomyManager get() {
        if (Instance == null)
            Instance = new EconomyManager();
        return Instance;
    }

    private Economy economy = null;

    public EconomyManager() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            ParallelUtils.log(Level.SEVERE, "Vault not found, skipping loading economy!");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            ParallelUtils.log(Level.SEVERE, "Failed to retrieve Economy class from services manager!");
            return;
        }
        economy = rsp.getProvider();
    }

    public Economy getEconomy() { return economy; }

    public boolean addRiftcoins(Player player, double amount) {
        if (economy == null)
            return false;
        EconomyResponse response = economy.depositPlayer(player, amount);
        if (response.transactionSuccess()) {
            ParallelUtils.log(Level.INFO, String.format("[EconomyManager] Gave %f riftcoins to %s", amount, player.getName()));
            return true;
        }
        else {
            ParallelUtils.log(Level.SEVERE, String.format("[EconomyManager] Failed to give %f riftcoins to %s: \n%s", amount, player.getName(), response.errorMessage));
            return false;
        }
    }

    public boolean removeRiftcoins(Player player, double amount) {
        if (economy == null)
            return false;
        EconomyResponse response = economy.withdrawPlayer(player, amount);
        if (response.transactionSuccess()) {
            ParallelUtils.log(Level.INFO, String.format("[EconomyManager] Took %f riftcoins from %s", amount, player.getName()));
            return true;
        }
        else {
            ParallelUtils.log(Level.SEVERE, String.format("[EconomyManager] Failed to take %f riftcoins from %s: \n%s", amount, player.getName(), response.errorMessage));
            return false;
        }
    }

    public double getBalance(Player player) {
        return economy.getBalance(player);
    }
}
