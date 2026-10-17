/**
 * Recursive Mathematical Functions.
 * 
 * DSA Component: Recursion
 * Demonstrates a mathematical recursive function applied to gameplay scoring.
 */
public class RecursionMath {

    /**
     * Recursively calculates bonus food points based on snake segment length.
     * 
     * Base Case: length <= 3 -> base reward of 10 points
     * Recursive Step: 2 bonus points + calculateBonus(length - 1)
     * 
     * Time Complexity: O(N)
     * Space Complexity: O(N) stack frames
     * 
     * @param length current snake body length
     * @return dynamically computed score points
     */
    public static int calculateScoreBonus(int length) {
        if (length <= 3) {
            return 10;
        }
        return 2 + calculateScoreBonus(length - 1);
    }
}
