package parallelmc.parallelutils.modules.customtrees.themes;

import com.eclipsekingdom.fractalforest.util.X.FMaterial;
import com.eclipsekingdom.fractalforest.util.X.XMaterial;
import com.eclipsekingdom.fractalforest.util.theme.ITheme;
import com.eclipsekingdom.fractalforest.util.theme.material.IMaterialFactory;
import com.eclipsekingdom.fractalforest.util.theme.material.MaterialJumble;
import com.eclipsekingdom.fractalforest.util.theme.material.MaterialSingleton;
import com.eclipsekingdom.fractalforest.util.theme.material.WeightedMaterialJumble;
import org.bukkit.Material;

import java.util.Collections;
import java.util.Set;

public class RandomTheme implements ITheme {

	@Override
	public IMaterialFactory getLeaf() {
		return new WeightedMaterialJumble().add(FMaterial.OAK_LEAVES, 10).add(FMaterial.ACACIA_LEAVES, 10)
				.add(FMaterial.BIRCH_LEAVES, 10).add(FMaterial.DARK_OAK_LEAVES, 10)
				.add(FMaterial.JUNGLE_LEAVES, 10).add(FMaterial.SPRUCE_LEAVES, 10)
				.add(new FMaterial(XMaterial.WARPED_WART_BLOCK), 10)
				.add(new FMaterial(XMaterial.NETHER_WART_BLOCK), 10)
				.add(new FMaterial(XMaterial.SHROOMLIGHT), 2);
	}

	@Override
	public IMaterialFactory getThickBranch() {
		return new MaterialJumble().add(FMaterial.OAK_WOOD).add(FMaterial.ACACIA_WOOD).add(FMaterial.BIRCH_WOOD)
				.add(FMaterial.DARK_OAK_WOOD).add(FMaterial.JUNGLE_WOOD).add(FMaterial.SPRUCE_WOOD)
				.add(new FMaterial(XMaterial.WARPED_HYPHAE)).add(new FMaterial(XMaterial.CRIMSON_HYPHAE));
	}

	@Override
	public IMaterialFactory getThinBranch() {
		return new MaterialJumble().add(FMaterial.OAK_FENCE).add(FMaterial.ACACIA_FENCE).add(FMaterial.BIRCH_FENCE)
				.add(FMaterial.DARK_OAK_FENCE).add(FMaterial.JUNGLE_FENCE).add(FMaterial.SPRUCE_FENCE)
				.add(new FMaterial(XMaterial.WARPED_FENCE)).add(new FMaterial(XMaterial.CRIMSON_FENCE));
	}

	@Override
	public IMaterialFactory getRoot() {
		return new MaterialJumble().add(FMaterial.OAK_WOOD).add(FMaterial.ACACIA_WOOD).add(FMaterial.BIRCH_WOOD)
				.add(FMaterial.DARK_OAK_WOOD).add(FMaterial.JUNGLE_WOOD).add(FMaterial.SPRUCE_WOOD)
				.add(new FMaterial(XMaterial.WARPED_HYPHAE)).add(new FMaterial(XMaterial.CRIMSON_HYPHAE));
	}

	@Override
	public Set<Material> getSelfMaterials() {
		return Collections.EMPTY_SET;
	}
}
