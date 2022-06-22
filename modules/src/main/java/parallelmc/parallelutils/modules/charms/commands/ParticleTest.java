package parallelmc.parallelutils.modules.charms.commands;

import dev.esophose.playerparticles.PlayerParticles;
import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import dev.esophose.playerparticles.particles.ParticleEffect;
import dev.esophose.playerparticles.styles.DefaultStyles;
import dev.esophose.playerparticles.styles.ParticleStyleCube;
import dev.esophose.playerparticles.styles.ParticleStyleNormal;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.commands.ParallelCommand;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.List;
import java.util.logging.Level;

public class ParticleTest extends ParallelCommand {
	private PlayerParticlesAPI ppAPI;

	public ParticleTest() {
		super("particleTest", new ParallelPermission("parallelutils.particletest"));

		if (Bukkit.getPluginManager().isPluginEnabled("PlayerParticles")) {
			//PlayerParticles pp = (PlayerParticles) Bukkit.getPluginManager().getPlugin("PlayerParticles");

			this.ppAPI = PlayerParticlesAPI.getInstance();
		}
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {

		if (ppAPI == null) return false;

		if (sender instanceof Player player) {
			ppAPI.addActivePlayerParticle(player, ParticleEffect.EXPLOSION, DefaultStyles.CUBE);
		}

		return true;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}
}
