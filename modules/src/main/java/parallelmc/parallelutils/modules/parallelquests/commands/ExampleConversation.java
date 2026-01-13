package parallelmc.parallelutils.modules.parallelquests.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;
import parallelmc.parallelutils.modules.parallelquests.dialogue.Dialogue;
import parallelmc.parallelutils.modules.parallelquests.dialogue.DialogueBuilder;

public class ExampleConversation implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, String[] args) {
        if (commandSender instanceof Player player) {
            Dialogue dialogue = Dialogue.withSpeaker("Villager",
                    DialogueBuilder.node()
                            .text("Hello there, can I help you with something?")
                            .option("Can you spare me an emerald?", DialogueBuilder.node()
                                    .text("I guess. Here, I have one for you.")
                                    .action(p -> p.getInventory().addItem(new ItemStack(Material.EMERALD, 1)))
                            )
                            .option("Nothing, goodbye.", DialogueBuilder.node()
                                    .text("Alright. See you around.")
                            )
                            .build()
            );
            ParallelQuests.get().startConversation(player, dialogue);
            return true;
        }
        return false;
    }
}
