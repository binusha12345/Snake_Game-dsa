/**
 * Bubble Sort Algorithm Implementation.
 * 
 * DSA Component: Sorting Algorithm
 * Time Complexity:
 *   - Best Case: O(N) with early exit optimization
 *   - Average / Worst Case: O(N^2)
 * Space Complexity: O(1) in-place
 * Order: Descending (highest score to lowest score)
 * 
 * Operates purely on plain Java arrays without relying on Java Collection Framework.
 */
public class BubbleSort {

    /**
     * Sorts a primitive integer array of scores in descending order.
     * 
     * @param scores array of scores to sort in-place
     */
    public static void sort(int[] scores) {
        if (scores == null || scores.length <= 1) {
            return;
        }

        for (int i = 0; i < scores.length - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < scores.length - i - 1; j++) {
                if (scores[j] < scores[j + 1]) {
                    int temp = scores[j];
                    scores[j] = scores[j + 1];
                    scores[j + 1] = temp;
                    swapped = true;
                }
            }

            // Early exit optimization
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Sorts a plain array of LeaderboardEntry objects up to a specific element count.
     * 
     * @param entries array of LeaderboardEntry items
     * @param count number of active entries in the array
     */
    public static void sort(LeaderboardEntry[] entries, int count) {
        if (entries == null || count <= 1) {
            return;
        }

        for (int i = 0; i < count - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < count - i - 1; j++) {
                if (entries[j].getScore() < entries[j + 1].getScore()) {
                    LeaderboardEntry temp = entries[j];
                    entries[j] = entries[j + 1];
                    entries[j + 1] = temp;
                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Convenience method to sort entire LeaderboardEntry array.
     */
    public static void sort(LeaderboardEntry[] entries) {
        if (entries != null) {
            sort(entries, entries.length);
        }
    }
}
