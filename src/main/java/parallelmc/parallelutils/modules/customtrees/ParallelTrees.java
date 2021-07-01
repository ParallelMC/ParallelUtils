package parallelmc.parallelutils.modules.customtrees;

import com.eclipsekingdom.fractalforest.sapling.MagicSapling;
import com.eclipsekingdom.fractalforest.trees.Species;
import com.eclipsekingdom.fractalforest.trees.effect.EffectType;
import com.eclipsekingdom.fractalforest.trees.gen.fractal.genome.GenomeType;
import com.eclipsekingdom.fractalforest.trees.habitat.HabitatType;
import com.eclipsekingdom.fractalforest.util.Scale;
import com.eclipsekingdom.fractalforest.util.X.FSapling;
import com.eclipsekingdom.fractalforest.util.X.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import parallelmc.parallelutils.Constants;
import parallelmc.parallelutils.ParallelModule;
import parallelmc.parallelutils.Parallelutils;
import parallelmc.parallelutils.modules.customtrees.habitats.MushroomHabitat;
import parallelmc.parallelutils.modules.customtrees.habitats.NetherExtHabitat;
import parallelmc.parallelutils.modules.customtrees.themes.*;

import java.util.logging.Level;

public class ParallelTrees implements ParallelModule {
	@Override
	public void onEnable() {
		PluginManager manager = Bukkit.getPluginManager();
		Plugin plugin = manager.getPlugin(Constants.PLUGIN_NAME);

		if (plugin == null) {
			Parallelutils.log(Level.SEVERE, "Unable to enable ParallelTrees. Plugin " + Constants.PLUGIN_NAME
					+ " does not exist!");
			return;
		}

		Parallelutils puPlugin = (Parallelutils) plugin;

		if (!puPlugin.registerModule("ParallelTrees", this)) {
			Parallelutils.log(Level.SEVERE, "Unable to register module ParallelTrees! " +
					"Module may already be registered. Quitting...");
			return;
		}

		if (manager.getPlugin("FractalForest") == null) {
			Parallelutils.log(Level.WARNING, "Could not find FractalForest. Will not continue initialization");
			return;
		}

		Species.addSpecies(new Species("CORAL_TREE", Scale.MEDIUM, GenomeType.CYGNI.value(), FSapling.SEAGRASS,
				new CoralTheme(), HabitatType.UNDERWATER.getHabitat(), EffectType.FOREST.getEffects()));

		MagicSapling.saplingMaterials.add(XMaterial.CRIMSON_FUNGUS.parseMaterial());
		Species.addSpecies(new Species("CRIMSON_TREE", Scale.MASSIVE, GenomeType.OAK.value(),
				new FSapling(XMaterial.CRIMSON_FUNGUS), new CrimsonTheme(), new NetherExtHabitat(), EffectType.NETHER.getEffects()));

		MagicSapling.saplingMaterials.add(XMaterial.WARPED_FUNGUS.parseMaterial());
		Species.addSpecies(new Species("WARPED_TREE", Scale.MASSIVE, GenomeType.OAK.value(),
				new FSapling(XMaterial.WARPED_FUNGUS), new WarpedTheme(), new NetherExtHabitat(), EffectType.NETHER.getEffects()));

		MagicSapling.saplingMaterials.add(XMaterial.BROWN_MUSHROOM_BLOCK.parseMaterial());
		Species.addSpecies(new Species("BROWN_MUSHROOM", Scale.MEDIUM, GenomeType.SECCHI.value(),
				new FSapling(XMaterial.BROWN_MUSHROOM_BLOCK), new BrownMushroomTheme(), new MushroomHabitat(), EffectType.FOREST.getEffects()));

		MagicSapling.saplingMaterials.add(XMaterial.RED_MUSHROOM_BLOCK.parseMaterial());
		Species.addSpecies(new Species("RED_MUSHROOM", Scale.MEDIUM, GenomeType.SECCHI.value(),
				new FSapling(XMaterial.RED_MUSHROOM_BLOCK), new RedMushroomTheme(), new MushroomHabitat(), EffectType.FOREST.getEffects()));

		Species.addSpecies(new Species("RANDOM_TREE", Scale.MASSIVE, GenomeType.ELM.value(),
				FSapling.OAK_SAPLING, new RandomTheme(), new MushroomHabitat(), EffectType.FOREST.getEffects()));
	}

	@Override
	public void onDisable() {

	}
}
