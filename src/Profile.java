/**
 * Represents a user profile in the Snake DSA Game.
 * Tracks player identity, personal high score, and game sessions.
 */
public class Profile {

    private String name;
    private int highScore;
    private int gamesPlayed;

    public Profile(String name, int highScore) {
        this.name = name;
        this.highScore = highScore;
        this.gamesPlayed = 0;
    }

    public String getName() {
        return name;
    }

    public int getHighScore() {
        return highScore;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    /**
     * Updates personal high score if current score exceeds it.
     * 
     * @param score new score
     * @return true if a new high score was set, false otherwise
     */
    public boolean updateHighScore(int score) {
        if (score > this.highScore) {
            this.highScore = score;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name + " (High Score: " + highScore + ")";
    }
}
