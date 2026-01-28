package parallelmc.parallelutils.modules.parallelquests.quests;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import parallelmc.parallelutils.modules.parallelquests.ParallelQuests;
import parallelmc.parallelutils.modules.parallelquests.dialogue.Dialogue;
import parallelmc.parallelutils.modules.parallelquests.dialogue.DialogueBuilder;

import java.util.List;

public class TheBakersBeastQuest extends Quest {
    public TheBakersBeastQuest() {
        super("thebakersbeast", "The Baker's Beast",
                List.of(
                        "<white>The <yellow>Baker</yellow> has lost their award-winning pie!</white>",
                        "<white>Find it for them to receive a slice!</white>"
                ));
    }

    @Override
    public void init() {
        registerStageDescription("notfound", List.of(
                "<white>Search for the <yellow>Baker's</yellow>",
                "<red>Sweet Berry Pie</red><white>."
        ));

        registerStageDescription("found", List.of(
                "<white>You found the <red>Sweet Berry Pie</red>!",
                "<white>Bring it back to the <yellow>Baker</yellow><white>."
        ));

        registerDialogue("thebakersbeast.prequest", Dialogue.withSpeaker("Baker", DialogueBuilder.node()
                .text("Excuse me, have you seen my prize-winning <red>Sweet Berry Pie?</red> I left it on the window sill to cool overnight, and now it’s gone! " +
                        "I can’t leave the oven unattended… won’t you take a look around for me? Find it, and you’ll be sure to get a slice!")
                .option("Sure, I'll look around.", DialogueBuilder.node()
                        .text("Thank you! I'll be waiting here if you find it.")
                        .action(player -> ParallelQuests.get().startQuest(player, questId, "notfound"))
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
                .text("What's that? My <red>pie!</red> Thank you ever so - oh. It um, it's covered in fur. On second thought, you can keep it. " +
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

        registerDialogue("thebakersbeast.hiker", Dialogue.withSpeaker("Hiker", DialogueBuilder.node()
                .text("Watch out, buddy! I just saw something scurry across the path into that cave... whatever it was, it smelled delicious!")
                .build()
        ));

        registerDialogue("thebakersbeast.werebear.prequest", Dialogue.withSpeaker("???", DialogueBuilder.node()
                .text("<i>Zzz...</i>")
                .build()
        ));

        registerDialogue("thebakersbeast.werebear.wakeup", Dialogue.withSpeaker("Werebear", DialogueBuilder.node()
                .text("...W-where am I? Oh no, it happened again! Every night, another pie-related felony. But it's not my fault that the moon is full every night! " +
                        "Here, take this back to the <yellow>Baker</yellow>, will you? I'd give it to him myself, but I don't think I'm quite back to normal yet.")
                .action(player ->
                {
                    player.getInventory().addItem(new ItemStack(Material.PUMPKIN_PIE));
                    setQuestStage(player, "found");
                })
                .build()
        ));

        registerDialogue("thebakersbeast.werebear.found", Dialogue.withSpeaker("Werebear", DialogueBuilder.node()
                .text("Didn't you hear me? Get that pie back to the <yellow>Baker</yellow>. I'm not quite back to normal yet.")
                .build()
        ));
    }

    @Override
    public void enter(Player player, String npcName) {
        var status = ParallelQuests.get().getQuestStatus(player, questId);
        switch (npcName) {
            case "Baker":
            {
                if (status.isEmpty()) {
                    showDialogue(player, "thebakersbeast.prequest");
                    break;
                }

                if (status.get().isCompleted()) {
                    showDialogue(player, "thebakersbeast.completed");
                    break;
                }

                if (player.getInventory().contains(Material.PUMPKIN_PIE)) {
                    showDialogue(player, "thebakersbeast.found");
                    break;
                }

                showDialogue(player, "thebakersbeast.notfound");
                break;
            }
            case "Hiker":
            {
                showDialogue(player, "thebakersbeast.hiker");
                break;
            }
            case "Werebear":
            {
                if (status.isEmpty()) {
                    showDialogue(player, "thebakersbeast.werebear.prequest");
                    break;
                }

                if (status.get().getQuestStage().equals("found")) {
                    showDialogue(player, "thebakersbeast.werebear.found");
                    break;
                }

                showDialogue(player, "thebakersbeast.werebear.wakeup");
                break;
            }
        }
    }

    @Override
    public List<String> prerequisites() {
        return List.of();
    }
}
