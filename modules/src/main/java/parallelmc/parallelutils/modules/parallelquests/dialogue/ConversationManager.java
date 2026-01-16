package parallelmc.parallelutils.modules.parallelquests.dialogue;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import parallelmc.parallelutils.ParallelUtils;

import java.util.*;
import java.util.logging.Level;

public class ConversationManager {
    private final HashMap<UUID, Conversation> ActiveConversations = new HashMap<>();
    private final HashMap<String, Dialogue> RegisteredDialogue = new HashMap<>();

    private static final NamespacedKey ATTRIBUTE_KEY = new NamespacedKey("parallelutils", "frozen");
    private static final Set<Attribute> ATTRIBUTES = new HashSet<>(Arrays.asList(Attribute.MOVEMENT_SPEED, Attribute.JUMP_STRENGTH));
    private static final AttributeModifier ZERO = new AttributeModifier(ATTRIBUTE_KEY, -1, AttributeModifier.Operation.MULTIPLY_SCALAR_1);

    public void startConversation(Player player, String dialogueId) {
        Dialogue dialogue = RegisteredDialogue.getOrDefault(dialogueId, null);
        if (dialogue == null) {
            ParallelUtils.log(Level.SEVERE, "Tried to start a conversation with invalid dialogue ID " + dialogueId);
            return;
        }
        startConversation(player, dialogue);
    }

    public void startConversation(Player player, Dialogue dialogue) {
        if (ActiveConversations.containsKey(player.getUniqueId())) {
            // if the player is already in a conversation, don't start a new one
            return;
        }
        Conversation c = new Conversation(dialogue);
        c.enter(player);
        // only freeze and track the player if the conversation has options
        // if not, it's already finished
        if (c.isFinished()) {
            return;
        }
        freezePlayer(player);
        ActiveConversations.put(player.getUniqueId(), c);
    }

    public void endConversation(Player player) {
        unfreezePlayer(player);
        ActiveConversations.remove(player.getUniqueId());
    }

    public @Nullable Conversation getActiveConversation(Player player) {
        return ActiveConversations.getOrDefault(player.getUniqueId(), null);
    }

    public boolean isInConversation(Player player) {
        return getActiveConversation(player) != null;
    }

    public void registerDialogue(String id, Dialogue dialogue) {
        if (RegisteredDialogue.putIfAbsent(id, dialogue) != null) {
            ParallelUtils.log(Level.WARNING, "Dialogue with ID " + id + " is already registered. Ignoring!");
        }
    }

    private void freezePlayer(Player player) {
        for (Attribute attribute : ATTRIBUTES) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance != null)
                instance.addTransientModifier(ZERO);
        }

        if (player.getVehicle() instanceof Attributable vehicle) {
            for (Attribute attribute : ATTRIBUTES) {
                AttributeInstance instance = vehicle.getAttribute(attribute);
                if (instance != null)
                    instance.addTransientModifier(ZERO);
            }
        }
    }

    private void unfreezePlayer(Player player) {
        for (Attribute attribute : ATTRIBUTES) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance != null)
                instance.removeModifier(ATTRIBUTE_KEY);
        }

        if (player.getVehicle() instanceof Attributable vehicle) {
            for (Attribute attribute : ATTRIBUTES) {
                AttributeInstance instance = vehicle.getAttribute(attribute);
                if (instance != null)
                    instance.removeModifier(ATTRIBUTE_KEY);
            }
        }
    }
}
