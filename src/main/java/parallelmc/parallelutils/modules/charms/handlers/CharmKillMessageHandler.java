package parallelmc.parallelutils.modules.charms.handlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import parallelmc.parallelutils.modules.charms.data.CharmOptions;
import parallelmc.parallelutils.modules.charms.data.IEffectSettings;
import parallelmc.parallelutils.modules.charms.helper.EncapsulatedType;

import java.util.HashMap;

public class CharmKillMessageHandler implements ICharmHandler {

	@Override
	public HandlerType getHandlerType() {
		return HandlerType.MESSAGE_KILL;
	}

	@Override
	public void handle(Player player, ItemStack item) { // Need event as well
		CharmOptions options = CharmOptions.parseOptions(item);

		if (options == null) {
			return;
		}

		HashMap<HandlerType, IEffectSettings> effects = options.getEffects();

		IEffectSettings killMessageSettings = effects.get(HandlerType.MESSAGE_KILL);

		if (killMessageSettings == null) {
			return;
		}

		HashMap<String, EncapsulatedType> settings = killMessageSettings.getSettings();
	}


}
