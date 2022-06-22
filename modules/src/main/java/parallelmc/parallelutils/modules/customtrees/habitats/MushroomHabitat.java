package parallelmc.parallelutils.modules.customtrees.habitats;

import com.eclipsekingdom.fractalforest.trees.habitat.IHabitat;
import com.eclipsekingdom.fractalforest.util.TreeUtil;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Set;

public class MushroomHabitat implements IHabitat {

	@Override
	public boolean canPlantAt(Location location) {
		Block above = location.clone().add(0, 1, 0).getBlock();
		return TreeUtil.isPassable(above.getType()) && !liquid.contains(above.getType());
	}

	@Override
	public boolean isSoil(Material material) {
		return true;
	}

	private Set<Material> liquid = new ImmutableSet.Builder<Material>()
			.add(Material.WATER)
			.add(Material.LAVA)
			.build();
}
