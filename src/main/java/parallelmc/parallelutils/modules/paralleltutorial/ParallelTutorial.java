package parallelmc.parallelutils.modules.paralleltutorial;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.parallelchat.ParallelChat;
import parallelmc.parallelutils.modules.paralleltutorial.commands.ParallelLeaveTutorial;
import parallelmc.parallelutils.modules.paralleltutorial.commands.ParallelListTutorials;
import parallelmc.parallelutils.modules.paralleltutorial.commands.ParallelReloadTutorials;
import parallelmc.parallelutils.modules.paralleltutorial.commands.ParallelStartTutorial;
import parallelmc.parallelutils.modules.paralleltutorial.handlers.OnLeaveDuringTutorial;
import parallelmc.parallelutils.modules.paralleltutorial.handlers.OnSpectatorTeleport;
import parallelmc.parallelutils.modules.paralleltutorial.scripting.Instruction;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Stream;

public class ParallelTutorial implements ParallelModule {

    private Parallelutils puPlugin;

    private ProtocolManager protManager;

    public static HashMap<Player, Location> playersInTutorial = new HashMap<>();

    // mostly for use in server crashes/shutdown
    public static HashMap<Player, BukkitTask> runningTutorials = new HashMap<>();

    private final HashMap<String, ArrayList<Instruction>> tutorials = new HashMap<>();

    // doing this again cuz lazy
    private static ParallelTutorial Instance;

    @Override
    public void onEnable() {
        PluginManager manager = Bukkit.getPluginManager();
        Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

        if (plugin == null) {
            Parallelutils.log(Level.SEVERE, "Unable to enable ParallelTutorial. Plugin " + Constants.PLUGIN_NAME
                    + " does not exist!");
            return;
        }

        this.puPlugin = (Parallelutils) plugin;

        if (!puPlugin.registerModule("ParallelTutorial", this)) {
            Parallelutils.log(Level.SEVERE, "Unable to register module ParallelChat! " +
                    "Module may already be registered. Quitting...");
            return;
        }

        this.protManager = ProtocolLibrary.getProtocolManager();

        LoadTutorials();

        manager.registerEvents(new OnSpectatorTeleport(), puPlugin);
        manager.registerEvents(new OnLeaveDuringTutorial(), puPlugin);

        puPlugin.getCommand("starttutorial").setExecutor(new ParallelStartTutorial());
        puPlugin.getCommand("listtutorials").setExecutor(new ParallelListTutorials());
        puPlugin.getCommand("reloadtutorials").setExecutor(new ParallelReloadTutorials());
        puPlugin.getCommand("leavetutorial").setExecutor(new ParallelLeaveTutorial());

        Instance = this;
    }

    @Override
    public void onDisable() {
        // if anyone is in a tutorial take them out of it
        runningTutorials.forEach((p, t) -> {
            t.cancel();
            Location start = playersInTutorial.get(p);
            p.teleport(start, PlayerTeleportEvent.TeleportCause.PLUGIN);
            p.setGameMode(GameMode.SURVIVAL);
            p.setFlySpeed(0.1f);
            playersInTutorial.remove(p);
        });
        runningTutorials.clear();
    }

    public static ParallelTutorial get(){
        return Instance;
    }

    public HashMap<String, ArrayList<Instruction>> GetTutorials() {
        return tutorials;
    }

    public void LoadTutorials() {
        tutorials.clear();
        AtomicInteger line = new AtomicInteger();
        // TODO: Make this not throw gross warnings when the path doesn't exist. Just create the folder
        try (Stream<Path> paths = Files.walk(Paths.get(puPlugin.getDataFolder() + "/tutorials"))) {
            paths.filter(Files::isRegularFile).forEach((f -> {
                ArrayList<Instruction> instructions = new ArrayList<>();
                try (Stream<String> stream = Files.lines(f)) {
                    stream.forEach(s -> {
                        String[] split = s.split(" ");
                        switch (split[0]) {
                            case "START":
                            case "MOVE":
                            case "TELEPORT":
                            case "LOOKAT": {
                                if (split.length != 4) {
                                    Parallelutils.log(Level.SEVERE, "Tutorial Parse Error: Expected 3 arguments on line " + line.get());
                                }
                                instructions.add(new Instruction(split[0], new String[] { split[1], split[2], split[3] }));
                                break;
                            }
                            case "WAIT": {
                                if (split.length != 2) {
                                    Parallelutils.log(Level.SEVERE, "Tutorial Parse Error: Expected 1 argument on line " + line.get());
                                }
                                instructions.add(new Instruction("WAIT", new String[] { split[1] }));
                                break;
                            }
                            case "SAY": {
                                if (split.length < 2) {
                                    Parallelutils.log(Level.SEVERE, "Tutorial Parse Error: Expected 1 argument on line " + line.get());
                                }
                                instructions.add(new Instruction("SAY", Arrays.copyOfRange(split, 1, split.length)));
                                break;
                            }
                            case "SOUND": {
                                if (split.length < 2) {
                                    Parallelutils.log(Level.SEVERE, "Tutorial Parse Error: Expected 1 argument on line " + line.get());
                                }
                                instructions.add(new Instruction("SOUND", new String[] { split[1] }));
                                break;
                            }
                            case "SPEED": {
                                if (split.length < 2) {
                                    Parallelutils.log(Level.SEVERE, "Tutorial Parse Error: Expected 1 argument on line " + line.get());
                                }
                                instructions.add(new Instruction("SPEED", new String[] { split[1] }));
                                break;
                            }
                            case "END": {
                                instructions.add(new Instruction("END", null));
                                break;
                            }
                            default: {
                                Parallelutils.log(Level.SEVERE, "Tutorial Parse Error: Unknown instruction on line " + line.get());
                                break;
                            }
                        }
                        line.getAndIncrement();

                    });
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                // beautiful line of code
                this.tutorials.put(f.getFileName().toString().split("\\.")[0].toLowerCase(), instructions);
                Parallelutils.log(Level.INFO, "Loaded " + instructions.size() + " instructions from tutorial " + f.getFileName().toString());
            }));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean HasTutorial(String tutorial) {
        return tutorials.containsKey(tutorial.toLowerCase());
    }

    public void RunTutorialFor(@NotNull Player player, @NotNull String tutorial) {
        Bukkit.getScheduler().runTaskAsynchronously(puPlugin, new Runnable() {
            final World world = player.getWorld();
            double speed = 0.5;
            Vector lookAt = null;
            // required to prevent built in anti-cheat from not teleporting
            boolean doLook = false;
            final ArrayList<Instruction> instructions = tutorials.get(tutorial.toLowerCase());
            boolean instructionFinished = true;
            int instructionIndex = 0;
            BukkitTask loop;
            @Override
            public void run() {
                loop = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (doLook) {
                            if (lookAt != null) {
                                playerLookAt(player, lookAt);
                            } else {
                                PacketContainer packet = protManager.createPacket(PacketType.Play.Server.ENTITY_LOOK);
                                packet.getIntegers().write(0, player.getEntityId());
                                packet.getBytes().write(0, (byte) 0);
                                packet.getBytes().write(1, (byte) 0);
                                packet.getBooleans().write(0, false);
                                try {
                                    protManager.sendServerPacket(player, packet);
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // only run the next instruction if the current one is finished
                        if (instructionFinished) {
                            Instruction i = instructions.get(instructionIndex);
                            instructionFinished = false;
                            switch (i.name()) {
                                case "START" -> {
                                    Bukkit.getScheduler().runTask(puPlugin, () -> {
                                        playersInTutorial.put(player, player.getLocation());
                                        player.setGameMode(GameMode.SPECTATOR);
                                        player.teleport(new Location(world, Double.parseDouble(i.args()[0]), Double.parseDouble(i.args()[1]), Double.parseDouble(i.args()[2])));
                                        player.setFlySpeed(0F);
                                        doLook = true;
                                        instructionFinished = true;
                                    });
                                }
                                case "MOVE" -> {
                                    final Location endPoint = new Location(world, Double.parseDouble(i.args()[0]), Double.parseDouble(i.args()[1]), Double.parseDouble(i.args()[2]));
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            Location current = player.getLocation();
                                            double distance = endPoint.distanceSquared(current);
                                            if (distance <= 1D || !playersInTutorial.containsKey(player)) {
                                                // doesn't abruptly stop unless you wait a tick
                                                // causes an overshoot if you don't wait
                                                Bukkit.getScheduler().runTaskLater(puPlugin, () -> {
                                                    player.setVelocity(new Vector().zero());
                                                }, 1L);
                                                instructionFinished = true;
                                                this.cancel();
                                            }
                                            Vector vel = endPoint.toVector().subtract(current.toVector()).normalize().multiply(speed);
                                            player.setVelocity(vel);
                                        }
                                    }.runTaskTimer(puPlugin, 1L, 3L);
                                }
                                case "TELEPORT" -> {
                                    final Location newPoint = new Location(world, Double.parseDouble(i.args()[0]), Double.parseDouble(i.args()[1]), Double.parseDouble(i.args()[2]));
                                    doLook = false;
                                    // again, wait two ticks to prevent teleport weirdness
                                    Bukkit.getScheduler().runTaskLater(puPlugin, () -> {
                                        player.teleport(newPoint);
                                        doLook = true;
                                        instructionFinished = true;
                                    }, 2L);
                                }
                                case "LOOKAT" -> {
                                    lookAt = new Vector(Double.parseDouble(i.args()[0]), Double.parseDouble(i.args()[1]), Double.parseDouble(i.args()[2]));
                                    instructionFinished = true;
                                }
                                case "WAIT" -> {
                                    long ticks = Long.parseLong(i.args()[0]);
                                    Bukkit.getScheduler().runTaskLater(puPlugin, () -> {
                                        instructionFinished = true;
                                    }, ticks);
                                }
                                case "SAY" -> {
                                    String string = ParallelChat.getStringArg(i.args());
                                    player.sendMessage(MiniMessage.get().parse(string));
                                    instructionFinished = true;
                                }
                                case "SOUND" -> {
                                    player.stopSound(SoundStop.all());
                                    player.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, i.args()[0]), Sound.Source.MASTER, 1f, 1f), Sound.Emitter.self());
                                    instructionFinished = true;
                                }
                                case "SPEED" -> {
                                    speed = Double.parseDouble(i.args()[0]) / 10D;
                                    instructionFinished = true;
                                }
                                case "END" -> {
                                    doLook = false;
                                    Bukkit.getScheduler().runTask(puPlugin, () -> {
                                        Location end = playersInTutorial.get(player);
                                        // waiting two ticks seems to fix teleporting issues
                                        Bukkit.getScheduler().runTaskLater(puPlugin, () -> {
                                            player.stopSound(SoundStop.all());
                                            player.teleport(end, PlayerTeleportEvent.TeleportCause.PLUGIN);
                                            player.setGameMode(GameMode.SURVIVAL);
                                            // unnecessary for most players but still needed since we change it above
                                            player.setFlySpeed(0.1F);
                                        }, 2L);
                                        playersInTutorial.remove(player);
                                        runningTutorials.remove(player);
                                        instructionFinished = true;
                                        loop.cancel();
                                    });
                                }
                            }
                            instructionIndex++;
                        }
                    }
                }.runTaskTimer(puPlugin, 0L, 1L);

                runningTutorials.put(player, loop);
            }
        });
    }



    // player look interpolation, but can only be done 20 times a second
    // so it's still kinda shitty
    private void playerLookAt(Player player, Vector loc) {
        Location ploc = player.getLocation();
        double diffX = loc.getX() - ploc.getX();
        double diffY = loc.getY() - ploc.getY();
        double diffZ = loc.getZ() - ploc.getZ();
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = 0;
        if (diffX != 0D) {
            if (diffX < 0D)
                yaw = 1.5f * (float)Math.PI;
            else
                yaw = 0.5f * (float)Math.PI;
            yaw -= Math.atan(diffZ / diffX);
        }
        else if (diffZ < 0)
            yaw = (float)Math.PI;

        yaw = (float)-Math.toDegrees(yaw);
        float pitch = (float)Math.toDegrees(-Math.atan(diffY / diffXZ));

        PacketContainer packet = protManager.createPacket(PacketType.Play.Server.ENTITY_LOOK);
        packet.getIntegers().write(0, player.getEntityId());
        packet.getBytes().write(0, (byte)(yaw * 256f / 360f));
        packet.getBytes().write(1, (byte)((pitch * 360f) / 256f));
        packet.getBooleans().write(0, false);
        try {
            protManager.sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
