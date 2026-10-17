import java.util.ArrayList;
import java.util.List;

/**
 * Linear Search Algorithm Implementation.
 * 
 * DSA Component: Searching Algorithm
 * Time Complexity:
 *   - Best Case: O(1)
 *   - Average / Worst Case: O(N)
 * Space Complexity: O(1)
 * 
 * Operates purely on plain arrays and produces detailed step diagnostics
 * usable by both console output and in-window GUI modal displays.
 */
public class LinearSearch {

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
     * Searches a plain array of LeaderboardEntry items by player name.
     * 
     * @param entries array of leaderboard entries
     * @param count active entry count
     * @param targetName player name to query
     * @return SearchResult containing match status, entry, and step trace
     */
    public static SearchResult searchByName(LeaderboardEntry[] entries, int count, String targetName) {
        List<String> trace = new ArrayList<>();

        if (entries == null || targetName == null || targetName.trim().isEmpty() || count <= 0) {
            trace.add("Invalid search query or empty leaderboard.");
            return new SearchResult(false, null, -1, 0, trace.toArray(new String[0]));
        }

        String query = targetName.trim();
        trace.add("Starting Linear Search for: '" + query + "'");

        System.out.println("--- Starting Linear Search for: '" + query + "' ---");

        for (int i = 0; i < count; i++) {
            LeaderboardEntry current = entries[i];
            String stepMsg = "Step " + (i + 1) + ": Inspecting index " + i + " -> " + current.getName() + " (" + current.getScore() + " pts)";
            trace.add(stepMsg);
            System.out.println("  " + stepMsg);

            if (current.getName().equalsIgnoreCase(query)) {
                String matchMsg = "==> MATCH FOUND at index " + i + "! [Comparisons: " + (i + 1) + "]";
                trace.add(matchMsg);
                System.out.println("  " + matchMsg);

                return new SearchResult(true, current, i, i + 1, trace.toArray(new String[0]));
            }
        }

        String notFoundMsg = "==> Player '" + query + "' NOT FOUND after checking all " + count + " entries.";
        trace.add(notFoundMsg);
        System.out.println("  " + notFoundMsg);

        return new SearchResult(false, null, -1, count, trace.toArray(new String[0]));
    }

    /**
     * Standard linear search over a primitive integer array.
     */
    public static int search(int[] array, int target) {
        if (array == null) {
            return -1;
        }

        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }
}
