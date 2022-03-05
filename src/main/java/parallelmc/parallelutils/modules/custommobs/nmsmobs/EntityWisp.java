package parallelmc.parallelutils.modules.custommobs.nmsmobs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftZombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.custommobs.particles.ParticleTask;
import parallelmc.parallelutils.modules.custommobs.bukkitmobs.CraftWisp;
import parallelmc.parallelutils.modules.custommobs.registry.EntityRegistry;
import parallelmc.parallelutils.modules.custommobs.registry.ParticleRegistry;

public class EntityWisp extends Zombie {
	public EntityWisp(EntityType<? extends Zombie> entitytypes, Level world) {
		super(entitytypes, world);
		registerGoals();
	}

	public EntityWisp(Level world) {
		super(world);
		registerGoals();
	}

	@Override
	public void registerGoals() {
		initPathfinder(this);
	}

	public static void initPathfinder(Zombie zombie) {
		//clearing Zombie goals
		CustomEntityHelper.clearGoals(zombie);

		zombie.goalSelector.addGoal(0, new MeleeAttackGoal(zombie, 1.0, false));
		zombie.goalSelector.addGoal(1, new RandomStrollGoal(zombie, 1.0));

		zombie.targetSelector.addGoal(0, new HurtByTargetGoal(zombie));
	}

	public static EntityWisp spawn(JavaPlugin plugin, CraftServer server, CraftWorld world, Location l) {
		return spawn(plugin, server, world, l, SpawnReason.UNKNOWN, null);
	}

	public static EntityWisp spawn(JavaPlugin plugin, CraftServer server, CraftWorld world, Location l,
	                               SpawnReason reason, Location origin) {
		EntityWisp wisp = new EntityWisp(world.getHandle());
		CraftZombie zombie = (CraftZombie) CraftEntity.getEntity(server, wisp);

		setup(plugin, zombie);
		wisp.setPos(l.getX(), l.getY(), l.getZ());
		boolean spawned = world.getHandle().addFreshEntity(wisp, CreatureSpawnEvent.SpawnReason.CUSTOM);

		if (!spawned) {
			Parallelutils.log(java.util.logging.Level.INFO, "Unable to spawn entity");
			return null;
		}

		EntityRegistry.getInstance().registerEntity(zombie.getUniqueId().toString(), "wisp", wisp, reason, origin);

		return wisp;
	}

	public static Zombie setup(JavaPlugin plugin, CraftZombie mob) {
		CraftWisp.setupNBT(mob);

		Zombie wisp = mob.getHandle();

		EntityWisp.initPathfinder(wisp);

		if (!ParticleRegistry.getInstance().particleTaskRunning) {
			BukkitTask task = new ParticleTask().runTaskTimer(plugin, 0, 10);
			ParticleRegistry.getInstance().particleTaskRunning = true;
		}

		return wisp;
	}

	@Override
	public boolean alwaysAccepts() {
		return super.alwaysAccepts();
	}
}
