package parallelmc.parallelutils.modules.npcshops.maggieshop;

import net.kyori.adventure.text.Component;

import java.util.List;

public record ShopCharm(String charmName, List<Component> lore, int price, String requiredRank) { }
