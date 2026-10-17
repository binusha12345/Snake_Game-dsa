import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

/**
 * Represents Food in the Snake DSA Game.
 * Handles deterministic random placement avoiding snake coordinates,
 * score points, and visual rendering.
 * 
 * Safe Algorithm:
 * Gathers all unoccupied cells to guarantee O(W * H) deterministic placement
 * with zero risk of infinite looping when the snake fills the board.
 */
public class Food {

    private int x;
    private int y;
    private int points;
    private final Random random;

    public Food(int gridWidth, int gridHeight, Snake snake) {
        this.random = new Random();
        this.points = 10;
        respawn(gridWidth, gridHeight, snake);
    }

    /**
     * Positions food on an empty grid cell without infinite loop hazard.
     * 
     * @param gridWidth columns
     * @param gridHeight rows
     * @param snake active snake body
     * @return true if food was placed, false if board is 100% full (Game Won)
     */
    public boolean respawn(int gridWidth, int gridHeight, Snake snake) {
        int maxCells = gridWidth * gridHeight;
        int[] freeX = new int[maxCells];
        int[] freeY = new int[maxCells];
        int freeCount = 0;

        // Scan the grid to find all unoccupied coordinates
        for (int r = 0; r < gridHeight; r++) {
            for (int c = 0; c < gridWidth; c++) {
                if (snake == null || !snake.contains(c, r)) {
                    freeX[freeCount] = c;
                    freeY[freeCount] = r;
                    freeCount++;
                }
            }
        }

        // Board Full / Win Condition
        if (freeCount == 0) {
            this.x = -1;
            this.y = -1;
            return false;
        }

        // Deterministic single random selection among free cells
        int selected = random.nextInt(freeCount);
        this.x = freeX[selected];
        this.y = freeY[selected];
        return true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    /**
     * Renders the food item onto the game canvas.
     */
    public void draw(Graphics g, int cellSize, int offsetY) {
        if (x < 0 || y < 0) {
            return; // Board full
        }

        int px = x * cellSize;
        int py = offsetY + (y * cellSize);

        // Outer red apple body
        g.setColor(new Color(231, 76, 60));
        g.fillOval(px + 4, py + 4, cellSize - 8, cellSize - 8);

        // Apple shine / reflection highlight
        g.setColor(new Color(241, 148, 138));
        g.fillOval(px + 10, py + 8, (cellSize - 8) / 3, (cellSize - 8) / 3);

        // Stem / Leaf
        g.setColor(new Color(46, 204, 113));
        g.fillRect(px + (cellSize / 2) - 2, py + 2, 4, 5);
    }
}
