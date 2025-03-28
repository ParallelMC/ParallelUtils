package parallelmc.parallelutils.util;

import java.util.Random;

/**
 * A helper class for functions related to obtaining random values
 */
public class RandomTools {

    private static Random r;

    private RandomTools() {
        r = new Random();
    }

    /**
     * Calculates a random integer between two integer values
     * @param low The first integer
     * @param high The second integer
     * @return The random integer between low and high, inclusive
     */
    public static int betweenTwoNumbers(int low, int high) {
        return r.nextInt(high - low + 1) + low;
    }
}
