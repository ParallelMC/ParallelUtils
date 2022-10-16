# ParallelUtils

<a href="https://parallelmc.org/">![Website](https://img.shields.io/badge/website-000000?style=for-the-badge&logo=About.me&logoColor=white)</a>
<a href="https://discord.gg/7PSDuCbg7Y">![Discord](https://img.shields.io/badge/Discord-%237289DA.svg?style=for-the-badge&logo=discord&logoColor=white)</a>
<a href="https://parallelmc.tumblr.com/">![Tumblr](https://img.shields.io/badge/Tumblr-%2336465D.svg?style=for-the-badge&logo=Tumblr&logoColor=white)</a>
<a href="https://twitter.com/ParallelMC">![Twitter](https://img.shields.io/badge/Twitter-%231DA1F2.svg?style=for-the-badge&logo=Twitter&logoColor=white)</a>
<a href="https://www.instagram.com/parallelserver/">![Instagram](https://img.shields.io/badge/Instagram-%23E4405F.svg?style=for-the-badge&logo=Instagram&logoColor=white)</a>
<a href="https://www.youtube.com/channel/UCc-73_rEe0Eci2S-Zl6_a2Q">![YouTube](https://img.shields.io/badge/YouTube-%23FF0000.svg?style=for-the-badge&logo=YouTube&logoColor=white)</a>

ParallelUtils is a plugin that enables a large variety of features, intended to be used for the Parallel SMP server at play.parallelmc.org.

# Structure

The main plugin is in the `api/` folder. This contains the basics required for ParallelUtils to function.

Modules are all contained in the `module/` folder.

  - To build the API, run `api:reobfJar`
  - To run the server, run `api:runServer`
  - To build a module, run `modules:reobf-modulename`

# Modules
This is a list of the current modules for ParallelUtils
 - BitsAndBobs
   - Adds random small features that are too small for their own module
  
 - Charms
   - Add Charms, which can be applied to items to add cosmetic effects 

 - ChestShops
   - Another Chest Shop plugin

- CustomMobs
   - Allows the creation of mobs with custom metadata, nbt, behaviors, and more
    
- CustomTrees
   - Adds custom trees for an [updated](https://github.com/ParallelMC/FractalForest) version of [FractalForest](https://www.spigotmc.org/resources/fractal-forest.75850/)
    
- DiscordIntegration
   - Adds and special Advancement notified, Join/Quit Suppressor, and more to a Discord bot
    
- EffectExtender
   - Allows for the stacking of potion effect durations up to 2x the original duration

- ExpStorage
  - Store experience in your ender chest!
 
- Gamemode4
  - These are plugin adaptations of the [Gamemode4][https://gm4.co/] datapacks

- ParallelChat
  - Adds various chat improvements, currently only supporting the linking of items into chat
  
- ParallelFlags
  - Adds custom WorldGuard flags
    
- ParallelItems
  - Adds custom items for use on play.parallelmc.org
  
- ParallelTutorial
  - Create scriptable tutorials to show people around your server
  
- PerformanceTools
  - Adds tools to help diagnose performance issues
  
## Why are some modules not accessible?
These modules either contain lore spoilers or are plugins very specifically designed for Parallel. We chose to make these plugins private (at least for now) to provide a better experience for the community.
