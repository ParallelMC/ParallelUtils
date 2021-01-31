package parallelmc.parallelutils.custommobs.nmsmobs;


import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftZombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.Registry;
import parallelmc.parallelutils.custommobs.ParticleTask;
import parallelmc.parallelutils.custommobs.bukkitmobs.CraftWisp;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class EntityWisp extends EntityZombie {
    public EntityWisp(EntityTypes<? extends EntityZombie> entitytypes, World world) {
        super(entitytypes, world);
        initPathfinder();
    }

    public EntityWisp(World world) {
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

        zombie.goalSelector.a(0, new PathfinderGoalMeleeAttack(zombie,1.0, false));
        zombie.goalSelector.a(1, new PathfinderGoalRandomStroll(zombie, 1.0));

        zombie.targetSelector.a(0, new PathfinderGoalHurtByTarget(zombie));
    }

    public static EntityWisp spawn(JavaPlugin plugin, CraftServer server, CraftWorld world, Location l) {
        EntityWisp wisp = new EntityWisp(world.getHandle());
        CraftZombie zombie = (CraftZombie) CraftEntity.getEntity(server, wisp);

        setup(plugin, zombie);
        wisp.setPosition(l.getX(), l.getY(), l.getZ());
        world.getHandle().addEntity(wisp, CreatureSpawnEvent.SpawnReason.CUSTOM);

        Registry.getInstance().registerEntity(zombie.getUniqueId().toString(), "wisp", wisp);

        return wisp;
    }

    public static EntityZombie setup(JavaPlugin plugin, CraftZombie mob) {
        CraftWisp.setupNBT(plugin, mob);

        EntityZombie wisp = mob.getHandle();

        EntityWisp.initPathfinder(wisp);

        if(!Registry.getInstance().particleTaskRunning){
            BukkitTask task = new ParticleTask(plugin).runTaskTimer(plugin, 0, 10);
            Registry.getInstance().particleTaskRunning = true;
        }

        return wisp;
    }
}
