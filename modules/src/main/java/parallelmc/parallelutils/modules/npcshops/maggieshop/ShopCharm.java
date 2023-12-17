package parallelmc.parallelutils.modules.npcshops.maggieshop;

import net.kyori.adventure.text.Component;

import java.util.List;

public record ShopCharm(String charmName, List<Component> lore, ShopCategory category, int price, String requiredRank) { }
