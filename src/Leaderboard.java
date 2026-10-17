/**
 * Manages game high scores using a plain array data structure.
 * 
 * DSA Features:
 *   - Plain Array Data Structure (LeaderboardEntry[])
 *   - Bubble Sort descending re-ranking
 *   - Linear Search by player name with step trace
 *   - Recursive Binary Search by score
 */
public class Leaderboard {

    private LeaderboardEntry[] entries;
    private int count;
    private static final int INITIAL_CAPACITY = 20;

    public Leaderboard() {
        this.entries = new LeaderboardEntry[INITIAL_CAPACITY];
        this.count = 0;
        loadInitialData();
    }

    /**
     * Loads the initial sample dataset into the plain array.
     */
    private void loadInitialData() {
        addRawEntry(new LeaderboardEntry("Damidu", 850));
        addRawEntry(new LeaderboardEntry("Kasun", 400));
        addRawEntry(new LeaderboardEntry("Amal", 950));
        addRawEntry(new LeaderboardEntry("Nimal", 650));
    }

    private void addRawEntry(LeaderboardEntry entry) {
        if (count >= entries.length) {
            resizeArray();
        }
        entries[count++] = entry;
    }

    private void resizeArray() {
        LeaderboardEntry[] newArr = new LeaderboardEntry[entries.length * 2];
        for (int i = 0; i < count; i++) {
            newArr[i] = entries[i];
        }
        entries = newArr;
    }

    /**
     * Demonstrates the Bubble Sort sorting process in terminal.
     */
    public void demonstrateBubbleSort() {
        System.out.println("===================================================");
        System.out.println(" Leaderboard (Before Bubble Sort)");
        System.out.println("===================================================");
        for (int i = 0; i < count; i++) {
            System.out.println(entries[i]);
        }

        // Apply Bubble Sort on Plain Array
        BubbleSort.sort(entries, count);

        System.out.println("\nAfter Bubble Sort (Descending - Highest First):");
        System.out.println("===================================================");
        System.out.println(" Leaderboard");
        System.out.println("===================================================");
        for (int i = 0; i < count; i++) {
            System.out.println(entries[i]);
        }
        System.out.println("===================================================\n");
    }

    /**
     * Demonstrates Linear Search and Recursive Binary Search in terminal.
     */
    public void demonstrateSearchAlgorithms() {
        System.out.println("===================================================");
        System.out.println(" 1. Linear Search Demonstration");
        System.out.println("===================================================");
        LinearSearch.SearchResult linResult = LinearSearch.searchByName(entries, count, "Damidu");
        if (linResult.found) {
            System.out.println("Result: Player: " + linResult.entry.getName() + " | Score: " + linResult.entry.getScore());
        }

        System.out.println("\n===================================================");
        System.out.println(" 2. Recursive Binary Search Demonstration");
        System.out.println("===================================================");
        BinarySearch.SearchResult binResult = BinarySearch.searchByScore(entries, count, 850);
        for (String step : binResult.trace) {
            System.out.println(step);
        }
        System.out.println("===================================================\n");
    }

    /**
     * Searches for a player using Linear Search DSA on the plain array.
     */
    public LinearSearch.SearchResult searchPlayer(String name) {
        return LinearSearch.searchByName(entries, count, name);
    }

    /**
     * Searches for a score using Recursive Binary Search on the sorted array.
     */
    public BinarySearch.SearchResult searchByScore(int targetScore) {
        return BinarySearch.searchByScore(entries, count, targetScore);
    }

    /**
     * Records a score for a player.
     * Updates personal record if higher, or adds new record.
     * Re-sorts the plain array using Bubble Sort DSA.
     */
    public void recordScore(String name, int score) {
        if (name == null || name.trim().isEmpty()) {
            name = "Player";
        }

        boolean found = false;
        for (int i = 0; i < count; i++) {
            if (entries[i].getName().equalsIgnoreCase(name.trim())) {
                found = true;
                if (score > entries[i].getScore()) {
                    entries[i].setScore(score);
                    System.out.println(">> Leaderboard: Updated " + name + "'s high score to " + score);
                }
                break;
            }
        }

        if (!found) {
            addRawEntry(new LeaderboardEntry(name.trim(), score));
            System.out.println(">> Leaderboard: Added new player " + name + " with score " + score);
        }

        // Re-sort via Bubble Sort DSA
        BubbleSort.sort(entries, count);
    }

    /**
     * Returns an active plain array copy of the leaderboard entries.
     */
    public LeaderboardEntry[] getEntries() {
        LeaderboardEntry[] result = new LeaderboardEntry[count];
        for (int i = 0; i < count; i++) {
            result[i] = entries[i];
        }
        return result;
    }

    public int getCount() {
        return count;
    }
}
