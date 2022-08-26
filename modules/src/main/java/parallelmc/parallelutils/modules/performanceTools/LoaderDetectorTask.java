package parallelmc.parallelutils.modules.performanceTools;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.modules.discordintegration.BotManager;
import parallelmc.parallelutils.modules.discordintegration.DiscordIntegration;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class LoaderDetectorTask extends BukkitRunnable {

    private final JavaPlugin plugin;

    BotManager discord;
    public LoaderDetectorTask(){
        PluginManager manager = Bukkit.getPluginManager();
        plugin = (JavaPlugin) manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            ParallelUtils.log(Level.SEVERE, "Unable to create LoaderDetectorTask. Plugin " + Constants.PLUGIN_NAME + "does not exist!");
            throw new IllegalPluginAccessException("Unable to create LoaderDetectorTask. Plugin " + Constants.PLUGIN_NAME + "does not exist!");
        }

        discord = null;
        if(plugin instanceof ParallelUtils puplugin) {
            if(puplugin.getModule("DiscordIntegration") instanceof DiscordIntegration discordIntegration) {
                discord = discordIntegration.getBotManager();
            }
        }
    }

    @Override
    public void run() {
        List<World> worlds = plugin.getServer().getWorlds();
        List<Chunk> stillLoaded;
        Chunk[] chunks;
        for(World w : worlds){
            chunks = w.getLoadedChunks();
            stillLoaded = Arrays.stream(chunks).filter(c -> w.getPlayers().stream().noneMatch(p ->
                    p.getLocation().distanceSquared(
                            new Location(w, c.getX()*16, p.getLocation().getBlockY(), c.getZ()*16)) <
                            w.getViewDistance() * w.getViewDistance())).toList();
            if(!stillLoaded.isEmpty()){
                //scream about it
                StringBuilder msg = new StringBuilder("```CHUNKS ARE LOADED WITH NO PLAYERS NEARBY!\n");
                msg.append("These chunks may be part of a portal farm or other legitimate loader. " +
                        "Any unfamiliar chunks should be checked for chunk loaders.\n");
                msg.append("Loaded chunks:");
                for(Chunk c : stillLoaded){
                    msg.append("X: ");
                    msg.append(c.getX());
                    msg.append(" Z: ");
                    msg.append(c.getZ());
                    msg.append("\n");
                }
                msg.append("TOTAL: ").append(stillLoaded.size());
                msg.append("```");
                if(discord !=null){
                    discord.sendMessage("staff", msg.toString());
                }
                else{
                    ParallelUtils.log(Level.SEVERE, "-----------------\n" + msg +"\n------------------");
                }
            }
        }
    }
}
