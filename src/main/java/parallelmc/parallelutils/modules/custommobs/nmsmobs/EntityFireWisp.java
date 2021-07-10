package parallelmc.parallelutils.modules.custommobs.nmsmobs;

import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftZombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.custommobs.bukkitmobs.CraftFireWisp;
import parallelmc.parallelutils.modules.custommobs.particles.ParticleTask;
import parallelmc.parallelutils.modules.custommobs.registry.EntityRegistry;
import parallelmc.parallelutils.modules.custommobs.registry.ParticleRegistry;

import java.util.logging.Level;

public class EntityFireWisp extends EntityZombie {
    public EntityFireWisp(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world);
        initPathfinder();
    }

    public EntityFireWisp(World world) {
        super(world);
        initPathfinder();
    }

    @Override
    public void initPathfinder() {
        initPathfinder(this);
    }

    public static void initPathfinder(EntityZombie zombie) {
        //clearing Zombie goals
        CustomEntityHelper.clearGoals(zombie);

        zombie.bP.a(0, new PathfinderGoalMeleeAttack(zombie, 1.0, false));
        zombie.bP.a(1, new PathfinderGoalRandomStroll(zombie, 1.0));

        zombie.bQ.a(0, new PathfinderGoalNearestAttackableTarget<>(zombie,
                EntityPlayer.class, true));
    }

    public static EntityFireWisp spawn(JavaPlugin plugin, CraftServer server, CraftWorld world, Location l) {
        return spawn(plugin, server, world, l, SpawnReason.UNKNOWN, null);
    }

    public static EntityFireWisp spawn(JavaPlugin plugin, CraftServer server, CraftWorld world, Location l,
                                   SpawnReason reason, Location origin) {
        EntityFireWisp wisp = new EntityFireWisp(world.getHandle());
        CraftZombie zombie = (CraftZombie) CraftEntity.getEntity(server, wisp);

        setup(plugin, zombie);
        wisp.setPosition(l.getX(), l.getY(), l.getZ());
        boolean spawned = world.getHandle().addEntity(wisp, CreatureSpawnEvent.SpawnReason.CUSTOM);

        if (!spawned) {
            Parallelutils.log(Level.INFO, "Unable to spawn entity");
            return null;
        }

        EntityRegistry.getInstance().registerEntity(zombie.getUniqueId().toString(), "fire_wisp", wisp, reason, origin);

        return wisp;
    }

    public static EntityZombie setup(JavaPlugin plugin, CraftZombie mob) {
        CraftFireWisp.setupNBT(mob);

        EntityZombie wisp = mob.getHandle();

        EntityFireWisp.initPathfinder(wisp);

        if (!ParticleRegistry.getInstance().particleTaskRunning) {
            BukkitTask task = new ParticleTask().runTaskTimer(plugin, 0, 10);
            ParticleRegistry.getInstance().particleTaskRunning = true;
        }

        return wisp;
    }
}
