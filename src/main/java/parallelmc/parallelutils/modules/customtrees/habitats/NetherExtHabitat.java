package parallelmc.parallelutils.modules.customtrees.habitats;

import com.eclipsekingdom.fractalforest.trees.habitat.IHabitat;
import com.eclipsekingdom.fractalforest.util.TreeUtil;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Set;

public class NetherExtHabitat implements IHabitat {

	@Override
	public boolean canPlantAt(Location location) {
		Block above = location.clone().add(0, 1, 0).getBlock();
		return soilMaterials.contains(location.getBlock().getType()) && TreeUtil.isPassable(above.getType()) && !liquid.contains(above.getType());
	}

	@Override
	public boolean isSoil(Material material) {
		return soilMaterials.contains(material);
	}

	private Set<Material> soilMaterials = new ImmutableSet.Builder<Material>()
			.add(Material.SOUL_SAND)
			.add(Material.NETHERRACK)
			.add(Material.CRIMSON_NYLIUM)
			.add(Material.WARPED_NYLIUM)
			.add(Material.SOUL_SOIL)
			.add(Material.GRAVEL)
			.build();


	private Set<Material> liquid = new ImmutableSet.Builder<Material>()
			.add(Material.WATER)
			.add(Material.LAVA)
			.build();
}
