package parallelmc.parallelutils.modules.customtrees.themes;

import com.eclipsekingdom.fractalforest.util.X.FMaterial;
import com.eclipsekingdom.fractalforest.util.X.XMaterial;
import com.eclipsekingdom.fractalforest.util.theme.ITheme;
import com.eclipsekingdom.fractalforest.util.theme.material.IMaterialFactory;
import com.eclipsekingdom.fractalforest.util.theme.material.MaterialSingleton;
import org.bukkit.Material;

import java.util.Collections;
import java.util.Set;

public class CrimsonTheme implements ITheme {
	@Override
	public IMaterialFactory getLeaf() {
		return new MaterialSingleton(FMaterial.NETHER_WART_BLOCK);
	}

	@Override
	public IMaterialFactory getThickBranch() {
		return new MaterialSingleton(new FMaterial(XMaterial.CRIMSON_HYPHAE));
	}

	@Override
	public IMaterialFactory getThinBranch() {
		return new MaterialSingleton(new FMaterial(XMaterial.CRIMSON_FENCE));
	}

	@Override
	public IMaterialFactory getRoot() {
		return new MaterialSingleton(new FMaterial(XMaterial.CRIMSON_HYPHAE));
	}

	@Override
	public Set<Material> getSelfMaterials() {
		return Collections.EMPTY_SET;
	}
}
