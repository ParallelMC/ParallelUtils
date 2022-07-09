package parallelmc.parallelutils.modules.chestshops;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.UUID;

// keep track of both chest and sign pos to make handling certain events easier
public record Shop(UUID owner, Location chestPos, Location signPos, Material item, int sellAmt, int buyAmt) { }
