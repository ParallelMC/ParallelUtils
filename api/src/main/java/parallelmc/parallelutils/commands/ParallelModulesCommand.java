package parallelmc.parallelutils.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.ParallelUtils;
import parallelmc.parallelutils.commands.permissions.ParallelPermission;

import java.util.List;

public class ParallelModulesCommand extends ParallelCommand {

	private final ParallelUtils puPlugin;

	public ParallelModulesCommand(ParallelUtils puPlugin) {
		super("modules", new ParallelPermission("parallelutils.modules.list"));
		this.puPlugin = puPlugin;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String[] args) {

		List<String> modules = puPlugin.getModules();

		TextComponent.Builder builder = Component.text()
				.append(Component.text("--------- ", NamedTextColor.YELLOW))
				.append(Component.text("Enabled Modules"))
				.append(Component.text(" --------------------\n", NamedTextColor.YELLOW));

		for (String mod : modules) {
			builder.append(Component.text(mod, NamedTextColor.GREEN))
					.append(Component.newline());
		}

		sender.sendMessage(builder.build());

		return true;
	}

	@Override
	public List<String> getTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
		return null;
	}
}
