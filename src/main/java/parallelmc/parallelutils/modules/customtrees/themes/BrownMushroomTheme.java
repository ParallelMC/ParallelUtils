package parallelmc.parallelutils.modules.customtrees.themes;

import com.eclipsekingdom.fractalforest.util.X.FMaterial;
import com.eclipsekingdom.fractalforest.util.X.XMaterial;
import com.eclipsekingdom.fractalforest.util.theme.ITheme;
import com.eclipsekingdom.fractalforest.util.theme.material.IMaterialFactory;
import com.eclipsekingdom.fractalforest.util.theme.material.MaterialSingleton;
import org.bukkit.Material;

import java.util.Collections;
import java.util.Set;

public class BrownMushroomTheme implements ITheme {

	@Override
	public IMaterialFactory getLeaf() {
		return new MaterialSingleton(new FMaterial(XMaterial.BROWN_MUSHROOM_BLOCK));
	}

	@Override
	public IMaterialFactory getThickBranch() {
		return new MaterialSingleton(FMaterial.MUSHROOM_STEM);
	}

	@Override
	public IMaterialFactory getThinBranch() {
		return new MaterialSingleton(FMaterial.MUSHROOM_STEM);
	}

	@Override
	public IMaterialFactory getRoot() {
		return new MaterialSingleton(FMaterial.MUSHROOM_STEM);
	}

	@Override
	public Set<Material> getSelfMaterials() {
		return Collections.EMPTY_SET;
	}
}
