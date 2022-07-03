# ParallelUtils
ParallelUtils is a plugin that enables a large variety of features, intended to be used for the Parallel SMP server at play.parallelmc.org.

# OLD ----- Procedure to Start Development ---- OLD

## NOTE: This isn't used anymore! The gradle plugin handles everything. Just build with `./gradlew reobfJar` and it will work. You do still need to place the libraries in `libs/`

1. Ensure Git, Java and Maven are installed
2. Download BuildTools from https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
3. Run `java -jar BuildTools.jar --rev 1.17`
4. Clone `https://github.com/PaperMC/Paper.git`. Change directory into the Paper directory.
5. Run `git submodule update --init --recursive`. If the repo has been cloned before and submodules have already been updated, run `git submodule update --recursive`
6. Run `./gradlew applyPatches`
7. Run `./gradlew publishToMavenLocal`
8. Place `FractalForest-1.12.jar`, `FrozenJoin-2.2.0.jar`, and `VotePart-2.26.jar` (or rename the jars in pom.xml) in the `libs` folder of ParallelUtils. These jars must be the ParallelMC versions.

# Current Structure
All folders in `src/main/java/parallelmc.parallelutils/` are part of the base plugin except `modules/`.

The `modules/` folder contains modules for ParallelUtils, which will be separated into separate plugins in the future.

The `commands/` folder contains the ParallelUtils Commands and Permissions APIs to allow easily adding commands, tab completes, and creating a specific set of Permission requirements.

The `util/` folder contains miscellaneous helper classes and methods.

The `versionchecker/` folder contains classes related to the version checker and updater.

The `Constants` class contains constants that are useful for ParallelUtils

The `ParallelModule` interface is used by modules to be recognized by ParallelUtils. See the [New Structure](https://github.com/ParallelMC/ParallelUtils#New_Structure) section.

The `Parallelutils` class is the base Plugin file. 

The `Version` class is a class that is used to manipulate plugin versions

# New Structure

Version 2 of ParallelUtils will split the modules into separate plugins. This will allow users to choose which modules to use and will prevent a single module from crashing the entire plugin.
After this change, the API will not change at all from the view of the modules.

# Modules
This is a list of the current modules for ParallelUtils
- CustomMobs
  - Allows the creation of mobs with custom metadata, nbt, behaviors, and more
    
- CustomTrees
  - Adds custom trees for an [updated](https://github.com/ParallelMC/FractalForest) version of [FractalForest](https://www.spigotmc.org/resources/fractal-forest.75850/)
    
- DiscordIntegration
  - Adds and special Advancement notified, Join/Quit Suppressor, and more to a Discord bot
    
- EffectExtender
  - Allows for the stacking of potion effect durations up to 2x the original duration
    
- Gamemode4
  - These are plugin adaptations of the [Gamemode4][https://gm4.co/] datapacks

- ParallelChat
  - Adds various chat improvements, currently only supporting the linking of items into chat
  
- ParallelFlags
  - Adds custom WorldGuard flags
    
- ParallelItems
  - Adds custom items for use on play.parallel.ga
  
- PerformanceTools
  - Adds tools to help diagnose performance issues
