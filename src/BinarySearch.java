import java.util.ArrayList;
import java.util.List;

/**
 * Recursive Binary Search Algorithm Implementation.
 * 
 * DSA Component: Searching Algorithm & Recursion
 * Time Complexity: O(log N)
 * Space Complexity: O(log N) call-stack memory
 * Precondition: The array MUST be sorted (sorted descending by Bubble Sort).
 * 
 * Demonstrates a second mathematically rigorous recursive algorithm.
 */
public class BinarySearch {

    public static class SearchResult {
        public boolean found;
        public LeaderboardEntry entry;
        public int index;
        public int steps;
        public String[] trace;

        public SearchResult(boolean found, LeaderboardEntry entry, int index, int steps, String[] trace) {
            this.found = found;
            this.entry = entry;
            this.index = index;
            this.steps = steps;
            this.trace = trace;
        }
    }

    /**
     * Public entry point for Recursive Binary Search by score.
     */
    public static SearchResult searchByScore(LeaderboardEntry[] entries, int count, int targetScore) {
        List<String> trace = new ArrayList<>();
        trace.add("Recursive Binary Search for score: " + targetScore);

        int resultIndex = recursiveBinarySearch(entries, 0, count - 1, targetScore, 1, trace);

        boolean found = (resultIndex != -1);
        LeaderboardEntry foundEntry = found ? entries[resultIndex] : null;

        return new SearchResult(
            found,
            foundEntry,
            resultIndex,
            trace.size() - 1,
            trace.toArray(new String[0])
        );
    }

    /**
     * Pure recursive binary search over a descending sorted array.
     * 
     * Base Case 1: low > high (target not present)
     * Base Case 2: entries[mid].score == targetScore (target found)
     * Recursive Step: search left or right partition
     */
    private static int recursiveBinarySearch(
            LeaderboardEntry[] entries,
            int low,
            int high,
            int targetScore,
            int depth,
            List<String> trace) {

        // Base Case 1: Range exhausted
        if (low > high) {
            trace.add("  [Depth " + depth + "] low (" + low + ") > high (" + high + ") -> Target score " + targetScore + " NOT found.");
            return -1;
        }

        int mid = low + (high - low) / 2;
        LeaderboardEntry midEntry = entries[mid];

        trace.add("  [Depth " + depth + "] Checking index " + mid + ": " + midEntry.getName()
                + " (" + midEntry.getScore() + " pts) [Range: " + low + ".." + high + "]");

        // Base Case 2: Match found
        if (midEntry.getScore() == targetScore) {
            trace.add("  ==> MATCH FOUND at index " + mid + "! Score: " + targetScore);
            return mid;
        }

        // Recursive Step: Array is sorted in DESCENDING order
        if (targetScore > midEntry.getScore()) {
            // Higher score lies in the left half
            return recursiveBinarySearch(entries, low, mid - 1, targetScore, depth + 1, trace);
        } else {
            // Lower score lies in the right half
            return recursiveBinarySearch(entries, mid + 1, high, targetScore, depth + 1, trace);
        }
    }
}
