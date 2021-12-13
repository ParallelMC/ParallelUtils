package parallelmc.parallelutils.modules.customtrees.genomes;

import com.eclipsekingdom.fractalforest.trees.gen.fractal.genome.Genome;
import com.eclipsekingdom.fractalforest.trees.gen.fractal.genome.gene.*;
import com.eclipsekingdom.fractalforest.util.math.functions.Exponential;
import com.eclipsekingdom.fractalforest.util.math.range.Bounds;

public class ChristmasGenome extends Genome {
	public ChristmasGenome() {
		super(new ClumpGene(0.05D),
				new SplitGene(1, 2),
				new AngleGene(new Bounds(0.0D, 0.1D)),
				new DecayGene(new Bounds(0.2, 0.5)),
				new TrunkGene(new Bounds(1.0, 8.0), new Bounds(0.5, 0.9), new Bounds(-0.1, 0.1)),
				new LeafGene(1.0, 2.8),
				new RootGene(0, 1, new Exponential(1.0, 0.5), new Bounds(1.0, 1.0), new Bounds(0.0, 2.0)));
	}
}
