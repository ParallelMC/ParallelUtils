package parallelmc.parallelutils.modules.customtrees.themes;

import com.eclipsekingdom.fractalforest.util.X.FMaterial;
import com.eclipsekingdom.fractalforest.util.X.XMaterial;
import com.eclipsekingdom.fractalforest.util.theme.ITheme;
import com.eclipsekingdom.fractalforest.util.theme.material.IMaterialFactory;
import com.eclipsekingdom.fractalforest.util.theme.material.MaterialJumble;
import org.bukkit.Material;

import java.util.Collections;
import java.util.Set;

public class CoralTheme implements ITheme {

	@Override
	public IMaterialFactory getLeaf() {
		return new MaterialJumble().add(new FMaterial(XMaterial.BRAIN_CORAL_BLOCK))
				.add(new FMaterial(XMaterial.FIRE_CORAL_BLOCK))
				.add(new FMaterial(XMaterial.HORN_CORAL_BLOCK));
	}

	@Override
	public IMaterialFactory getThickBranch() {
		return new MaterialJumble().add(new FMaterial(XMaterial.TUBE_CORAL_BLOCK))
				.add(new FMaterial(XMaterial.BUBBLE_CORAL_BLOCK));
	}

	@Override
	public IMaterialFactory getThinBranch() {
		return new MaterialJumble().add(new FMaterial(XMaterial.TUBE_CORAL_BLOCK))
				.add(new FMaterial(XMaterial.BUBBLE_CORAL_BLOCK));
	}

	@Override
	public IMaterialFactory getRoot() {
		return new MaterialJumble().add(new FMaterial(XMaterial.TUBE_CORAL_BLOCK))
				.add(new FMaterial(XMaterial.BUBBLE_CORAL_BLOCK));
	}

	@Override
	public Set<Material> getSelfMaterials() {
		return Collections.EMPTY_SET;
	}
}
