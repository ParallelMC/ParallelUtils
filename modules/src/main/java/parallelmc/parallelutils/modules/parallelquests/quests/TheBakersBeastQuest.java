package parallelmc.parallelutils.modules.parallelquests.quests;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;
import parallelmc.parallelutils.modules.parallelquests.QuestStatus;
import parallelmc.parallelutils.modules.parallelquests.dialogue.Dialogue;
import parallelmc.parallelutils.modules.parallelquests.dialogue.DialogueBuilder;

public class TheBakersBeastQuest extends Quest {
    public TheBakersBeastQuest() {
        super("thebakersbeast", "The Baker's Beast", "The Baker has lost their award-winning pie! Find it for them to receive a slice!");
    }

    @Override
    public void init() {
        registerDialogue("thebakersbeast.prequest", Dialogue.withSpeaker("Baker", DialogueBuilder.node()
                .text("Excuse me, have you seen my prize-winning Sweet Berry Pie? I left it on the window sill to cool overnight, and now it’s gone! " +
                        "I can’t leave the oven unattended… won’t you take a look around for me? Find it, and you’ll be sure to get a slice!")
                .option("Sure, I'll look around.", DialogueBuilder.node()
                        .text("Thank you! I'll be waiting here if you find it.")
                        .action(player -> ParallelQuests.get().startQuest(player.getUniqueId(), questId, "notfound"))
                )
                .option("Sorry, I'm a little busy...", DialogueBuilder.node()
                        .text("Oh...that's okay, I understand. If you end up changing your mind, I'll be here.")
                ).build()
        ));

        registerDialogue("thebakersbeast.notfound", Dialogue.withSpeaker("Baker", DialogueBuilder.node()
                .text("Did you find it? Hmm, I thought not. It could be anywhere by now... try to look for crumbs!")
                .build()
        ));

        registerDialogue("thebakersbeast.found", Dialogue.withSpeaker("Baker", DialogueBuilder.node()
                .text("What's that? My pie! Thank you ever so - oh. It um, it's covered in fur. On second thought, you can keep it. " +
                        "Still, you deserve an award! Have a fresh one straight from the oven!")
                .action(player ->  {
                    player.getInventory().addItem(new ItemStack(Material.PUMPKIN_PIE, 1));
                    completeQuest(player);
                })
                .build()
        ));

        registerDialogue("thebakersbeast.completed", Dialogue.withSpeaker("Baker", DialogueBuilder.node()
                .text("Thank you again for finding my pie! At least someone was able to enjoy it...")
                .build()
        ));
    }

    @Override
    public void enter(Player player) {
        var status = ParallelQuests.get().getQuestStatus(player.getUniqueId(), questId);
        if (status.isEmpty()) {
            showDialogue(player, "thebakersbeast.prequest");
            return;
        }

        if (status.get().isCompleted()) {
            showDialogue(player, "thebakersbeast.completed");
            return;
        }

        if (player.getInventory().contains(Material.PUMPKIN_PIE)) {
            showDialogue(player, "thebakersbeast.found");
            return;
        }

        showDialogue(player, "thebakersbeast.notfound");
    }
}
