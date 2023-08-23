package parallelmc.parallelutils.modules.npcshops.maggieshop;

import java.util.List;

public record ShopCharm(String charmId, String charmName, List<String> lore, int price, String requiredRank) { }
