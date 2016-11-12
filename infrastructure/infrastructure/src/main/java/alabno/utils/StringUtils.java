package alabno.utils;

import java.util.Random;

public class StringUtils {
    
    private static Random random = new Random();

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    /**
     * @param lhs
     *            First string to be compared
     * @param rhs
     *            Second string to be compared
     * @return The Levenshtein distance between the two strings
     * 
     *         Please see
     *         https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
     *         original source
     */
    public static int computeLevenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        return distance[lhs.length()][rhs.length()];
    }
    
    /**
     * @param length The length of the random string to be generated
     * @return a fixed length random numerical string
     */
    public static String randomAsciiStringNumerical(int length) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int generated = random.nextInt(10);
            out.append(generated);
        }
        return out.toString();
    }

}
