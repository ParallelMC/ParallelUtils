package parallelmc.parallelutils.modules.customtrees.themes;

import com.eclipsekingdom.fractalforest.util.X.FMaterial;
import com.eclipsekingdom.fractalforest.util.theme.ITheme;
import com.eclipsekingdom.fractalforest.util.theme.material.IMaterialFactory;
import com.eclipsekingdom.fractalforest.util.theme.material.MaterialSingleton;
import com.eclipsekingdom.fractalforest.util.theme.material.WeightedMaterialJumble;
import org.bukkit.Material;

import java.util.Collections;
import java.util.Set;

public class ChristmasTheme implements ITheme {
	@Override
	public IMaterialFactory getLeaf() {
		return new WeightedMaterialJumble().add(FMaterial.SPRUCE_LEAVES, 18)
				.add(FMaterial.RED_WOOL, 1)
				.add(FMaterial.YELLOW_WOOL, 1)
				.add(FMaterial.GREEN_WOOL, 1)
				.add(FMaterial.BLUE_WOOL, 1);
	}

	@Override
	public IMaterialFactory getThickBranch() {
		return new MaterialSingleton(FMaterial.SPRUCE_WOOD);
	}

	@Override
	public IMaterialFactory getThinBranch() {
		return new MaterialSingleton(FMaterial.SPRUCE_FENCE);
	}

	@Override
	public IMaterialFactory getRoot() {
		return new MaterialSingleton(FMaterial.SPRUCE_WOOD);
	}

	@Override
	public Set<Material> getSelfMaterials() {
		return Collections.EMPTY_SET;
	}
}
