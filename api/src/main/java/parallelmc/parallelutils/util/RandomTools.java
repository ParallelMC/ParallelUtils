package parallelmc.parallelutils.util;

import java.util.Random;

/**
 * A helper class for functions related to obtaining random values
 */
public class RandomTools {

    /**
     * Calculates a random integer between two integer values
     * @param low The first integer
     * @param high The second integer
     * @return The random integer between low and high, inclusive
     */
    public static int betweenTwoIntegers(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low + 1) + low;
    }
}
