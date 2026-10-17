import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * GamePanel - Version 11 Advanced DSA Architecture
 * 
 * Data Structures & Algorithms Implemented:
 *  1. 2D Array Grid: int[][] board (O(1) lookups)
 *  2. Doubly Linked List with Tail Pointer: Snake (O(1) head insert, O(1) tail removal)
 *  3. Hand-Built FIFO Queue: CustomQueue<Direction> for input buffering
 *  4. Hand-Built LIFO Stack: CustomStack<String> for game event / score history
 *  5. Bubble Sort Algorithm: Plain array sorting on LeaderboardEntry[] (O(N^2))
 *  6. Linear Search Algorithm: In-Window GUI step trace display (O(N))
 *  7. Recursive Binary Search: In-Window score search on sorted array (O(log N))
 *  8. Call-Stack Recursion: countdown(3) -> countdown(0) and RecursionMath bonus
 */
public class GamePanel extends JPanel implements ActionListener {

    // Grid configuration
    public static final int GRID_WIDTH = 20;   // 20 columns
    public static final int GRID_HEIGHT = 15;  // 15 rows
    public static final int CELL_SIZE = 40;    // 40x40 pixels per cell

    // Layout offsets
    public static final int HEADER_HEIGHT = 44; // Top stats bar
    public static final int FOOTER_HEIGHT = 50; // Bottom control bar
    public static final int TOTAL_WIDTH = GRID_WIDTH * CELL_SIZE;                       // 800 px
    public static final int TOTAL_HEIGHT = HEADER_HEIGHT + (GRID_HEIGHT * CELL_SIZE) + FOOTER_HEIGHT; // 694 px

    // DSA 1: 2D Array representation
    public static final int EMPTY = 0;
    public static final int SNAKE_CELL = 1;
    public static final int FOOD_CELL = 2;

    private int[][] board;

    // DSA 2: Doubly Linked List Snake with Tail Pointer (O(1) tail removal)
    private Snake snake;

    // Food Object (Safe deterministic placement without infinite loop)
    private Food food;

    // DSA 3: Hand-Built Queue for Input Buffering
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    private CustomQueue<Direction> inputQueue = new CustomQueue<>();

    // DSA 4: Hand-Built Stack for Game Event History
    private CustomStack<String> eventStack = new CustomStack<>();

    // DSA 5: Bubble Sort & Plain Array Leaderboard
    private Leaderboard leaderboard;

    // DSA 6 & 7: Search Results Display State
    private LinearSearch.SearchResult activeLinearResult = null;
    private BinarySearch.SearchResult activeBinaryResult = null;

    // DSA 8: Recursion Countdown
    private String countdownText = "";

    // Profiles Management
    private List<Profile> profiles = new ArrayList<>();
    private Profile currentProfile = null;

    // Screen States (FSM)
    public enum ScreenState {
        MAIN_MENU,
        SELECT_PROFILE,
        COUNTDOWN,
        PLAYING,
        HOW_TO_PLAY,
        LEADERBOARD,
        SEARCH_DISPLAY,
        GAME_OVER
    }

    private ScreenState currentState = ScreenState.MAIN_MENU;

    // Movement direction
    private int dx = 1; // Initially moving RIGHT
    private int dy = 0;
    private Direction currentDirection = Direction.RIGHT;

    // Game stats & difficulty progression
    private static final int INITIAL_SPEED_MS = 140;
    private static final int MIN_SPEED_MS = 50;
    private static final int SPEED_DECREMENT_MS = 10;

    private int currentSpeed = INITIAL_SPEED_MS;
    private int score = 0;
    private int highScore = 0;
    private int foodsEaten = 0;
    private int level = 1;
    private boolean isNewHighScore = false;

    // Game states
    private boolean isPaused = false;
    private String collisionType = "";
    private String collisionDetails = "";

    // Timer for game loop
    private Timer gameTimer;

    // Mouse tracking for hover effects
    private Point mousePoint = null;

    // Interactive Button Bounds
    private final Rectangle menuPlayBtn = new Rectangle(270, 220, 260, 48);
    private final Rectangle menuLeaderboardBtn = new Rectangle(270, 285, 260, 48);
    private final Rectangle menuHowToPlayBtn = new Rectangle(270, 350, 260, 48);
    private final Rectangle menuExitBtn = new Rectangle(270, 415, 260, 48);

    private final Rectangle profileCreateBtn = new Rectangle(250, 505, 300, 46);
    private final Rectangle profileBackBtn = new Rectangle(290, 565, 220, 42);

    private final Rectangle gamePauseBtn = new Rectangle(280, TOTAL_HEIGHT - FOOTER_HEIGHT + 7, 110, 36);
    private final Rectangle gameRestartBtn = new Rectangle(410, TOTAL_HEIGHT - FOOTER_HEIGHT + 7, 110, 36);

    private final Rectangle goPlayAgainBtn = new Rectangle(270, 330, 260, 42);
    private final Rectangle goSwitchProfileBtn = new Rectangle(270, 382, 260, 42);
    private final Rectangle goLeaderboardBtn = new Rectangle(270, 434, 260, 42);
    private final Rectangle goMainMenuBtn = new Rectangle(270, 486, 260, 42);

    private final Rectangle howBackBtn = new Rectangle(290, 565, 220, 44);

    private final Rectangle lbSearchBtn = new Rectangle(160, 565, 150, 44);
    private final Rectangle lbBinSearchBtn = new Rectangle(325, 565, 175, 44);
    private final Rectangle lbBackBtn = new Rectangle(515, 565, 135, 44);

    private final Rectangle searchCloseBtn = new Rectangle(300, 565, 200, 44);

    public GamePanel() {
        this.setPreferredSize(new Dimension(TOTAL_WIDTH, TOTAL_HEIGHT));
        this.setBackground(new Color(20, 22, 27));
        this.setFocusable(true);

        board = new int[GRID_HEIGHT][GRID_WIDTH];

        initProfiles();

        // Plain array Leaderboard with Bubble Sort & Search demonstrations
        leaderboard = new Leaderboard();
        leaderboard.demonstrateBubbleSort();
        leaderboard.demonstrateSearchAlgorithms();

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getPoint());
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
                updateCursor();
                repaint();
            }
        });

        initGameData();

        gameTimer = new Timer(currentSpeed, this);
    }

    private void initProfiles() {
        profiles.add(new Profile("Damidu", 850));
        profiles.add(new Profile("Amal", 950));
        profiles.add(new Profile("Nimal", 650));
        profiles.add(new Profile("Kasun", 400));
        currentProfile = profiles.get(0);
    }

    public void initGameData() {
        // Doubly Linked List with tail pointer
        snake = new Snake(6, 7, 3);
        dx = 1;
        dy = 0;
        currentDirection = Direction.RIGHT;
        inputQueue.clear();
        eventStack.clear();
        eventStack.push("Round Initialized");

        isPaused = false;
        collisionType = "";
        collisionDetails = "";
        score = 0;
        foodsEaten = 0;
        level = 1;
        isNewHighScore = false;
        currentSpeed = INITIAL_SPEED_MS;

        if (currentProfile != null) {
            highScore = currentProfile.getHighScore();
        }

        if (gameTimer != null) {
            gameTimer.stop();
            gameTimer.setDelay(currentSpeed);
        }

        food = new Food(GRID_WIDTH, GRID_HEIGHT, snake);
        syncBoard();
    }

    public void selectProfileAndPlay(Profile profile) {
        this.currentProfile = profile;
        this.highScore = profile.getHighScore();
        System.out.println(">> Active Profile: " + currentProfile.getName()
                + " (Personal Best: " + currentProfile.getHighScore() + " pts)");
        startWithRecursiveCountdown();
    }

    public void startWithRecursiveCountdown() {
        initGameData();
        currentState = ScreenState.COUNTDOWN;
        countdownText = "3";
        repaint();

        RecursionCountdown countdown = new RecursionCountdown(new RecursionCountdown.CountdownListener() {
            @Override
            public void onTick(String text) {
                countdownText = text;
                repaint();
            }

            @Override
            public void onComplete() {
                countdownText = "";
                currentState = ScreenState.PLAYING;
                gameTimer.start();
                System.out.println("--- Round Started for: " + currentProfile.getName() + " ---");
                snake.printLinkedList();
                repaint();
            }
        });

        countdown.start(3);
    }

    private void syncBoard() {
        for (int r = 0; r < GRID_HEIGHT; r++) {
            for (int c = 0; c < GRID_WIDTH; c++) {
                board[r][c] = EMPTY;
            }
        }

        if (food != null && food.getY() >= 0 && food.getY() < GRID_HEIGHT
                && food.getX() >= 0 && food.getX() < GRID_WIDTH) {
            board[food.getY()][food.getX()] = FOOD_CELL;
        }

        SnakeNode current = snake.getHead();
        while (current != null) {
            if (current.x >= 0 && current.x < GRID_WIDTH && current.y >= 0 && current.y < GRID_HEIGHT) {
                board[current.y][current.x] = SNAKE_CELL;
            }
            current = current.next;
        }
    }

    private void updateCursor() {
        if (mousePoint == null) {
            setCursor(Cursor.getDefaultCursor());
            return;
        }

        boolean hoveringButton = false;
        switch (currentState) {
            case MAIN_MENU:
                hoveringButton = menuPlayBtn.contains(mousePoint)
                        || menuLeaderboardBtn.contains(mousePoint)
                        || menuHowToPlayBtn.contains(mousePoint)
                        || menuExitBtn.contains(mousePoint);
                break;
            case SELECT_PROFILE:
                hoveringButton = profileCreateBtn.contains(mousePoint)
                        || profileBackBtn.contains(mousePoint)
                        || getClickedProfileIndex(mousePoint) != -1;
                break;
            case PLAYING:
                hoveringButton = gamePauseBtn.contains(mousePoint) || gameRestartBtn.contains(mousePoint);
                break;
            case GAME_OVER:
                hoveringButton = goPlayAgainBtn.contains(mousePoint)
                        || goSwitchProfileBtn.contains(mousePoint)
                        || goLeaderboardBtn.contains(mousePoint)
                        || goMainMenuBtn.contains(mousePoint);
                break;
            case HOW_TO_PLAY:
                hoveringButton = howBackBtn.contains(mousePoint);
                break;
            case LEADERBOARD:
                hoveringButton = lbSearchBtn.contains(mousePoint)
                        || lbBinSearchBtn.contains(mousePoint)
                        || lbBackBtn.contains(mousePoint);
                break;
            case SEARCH_DISPLAY:
                hoveringButton = searchCloseBtn.contains(mousePoint);
                break;
            default:
                break;
        }

        setCursor(hoveringButton ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
    }

    private int getClickedProfileIndex(Point p) {
        int startY = 140;
        int cardH = 50;
        int gap = 10;
        int cardW = 440;
        int cardX = (TOTAL_WIDTH - cardW) / 2;

        for (int i = 0; i < Math.min(profiles.size(), 6); i++) {
            Rectangle cardRect = new Rectangle(cardX, startY + i * (cardH + gap), cardW, cardH);
            if (cardRect.contains(p)) {
                return i;
            }
        }
        return -1;
    }

    private void handleMouseClick(Point p) {
        switch (currentState) {
            case MAIN_MENU:
                if (menuPlayBtn.contains(p)) {
                    currentState = ScreenState.SELECT_PROFILE;
                    repaint();
                } else if (menuLeaderboardBtn.contains(p)) {
                    currentState = ScreenState.LEADERBOARD;
                    repaint();
                } else if (menuHowToPlayBtn.contains(p)) {
                    currentState = ScreenState.HOW_TO_PLAY;
                    repaint();
                } else if (menuExitBtn.contains(p)) {
                    System.exit(0);
                }
                break;

            case SELECT_PROFILE:
                int clickedIndex = getClickedProfileIndex(p);
                if (clickedIndex != -1) {
                    selectProfileAndPlay(profiles.get(clickedIndex));
                } else if (profileCreateBtn.contains(p)) {
                    promptCreateProfile();
                } else if (profileBackBtn.contains(p)) {
                    currentState = ScreenState.MAIN_MENU;
                    repaint();
                }
                break;

            case PLAYING:
                if (gamePauseBtn.contains(p)) {
                    isPaused = !isPaused;
                    repaint();
                } else if (gameRestartBtn.contains(p)) {
                    startWithRecursiveCountdown();
                }
                break;

            case GAME_OVER:
                if (goPlayAgainBtn.contains(p)) {
                    startWithRecursiveCountdown();
                } else if (goSwitchProfileBtn.contains(p)) {
                    currentState = ScreenState.SELECT_PROFILE;
                    repaint();
                } else if (goLeaderboardBtn.contains(p)) {
                    currentState = ScreenState.LEADERBOARD;
                    repaint();
                } else if (goMainMenuBtn.contains(p)) {
                    currentState = ScreenState.MAIN_MENU;
                    repaint();
                }
                break;

            case HOW_TO_PLAY:
                if (howBackBtn.contains(p)) {
                    currentState = ScreenState.MAIN_MENU;
                    repaint();
                }
                break;

            case LEADERBOARD:
                if (lbSearchBtn.contains(p)) {
                    promptLinearSearchGUI();
                } else if (lbBinSearchBtn.contains(p)) {
                    promptBinarySearchGUI();
                } else if (lbBackBtn.contains(p)) {
                    currentState = ScreenState.MAIN_MENU;
                    repaint();
                }
                break;

            case SEARCH_DISPLAY:
                if (searchCloseBtn.contains(p)) {
                    currentState = ScreenState.LEADERBOARD;
                    repaint();
                }
                break;

            default:
                break;
        }
    }

    private void promptCreateProfile() {
        String name = JOptionPane.showInputDialog(
            this,
            "Enter Your Name to Create Profile:",
            "Create New Profile",
            JOptionPane.PLAIN_MESSAGE
        );

        if (name != null && !name.trim().isEmpty()) {
            name = name.trim();
            for (Profile p : profiles) {
                if (p.getName().equalsIgnoreCase(name)) {
                    JOptionPane.showMessageDialog(this,
                        "Profile for '" + name + "' already exists! Selecting your profile.",
                        "Existing Profile Found",
                        JOptionPane.INFORMATION_MESSAGE);
                    selectProfileAndPlay(p);
                    return;
                }
            }

            Profile newProfile = new Profile(name, 0);
            profiles.add(newProfile);
            System.out.println(">> Created new Profile: " + name);
            selectProfileAndPlay(newProfile);
        }
    }

    private void handleKeyPress(int keyCode) {
        if (currentState == ScreenState.MAIN_MENU) {
            if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
                currentState = ScreenState.SELECT_PROFILE;
                repaint();
            } else if (keyCode == KeyEvent.VK_L) {
                currentState = ScreenState.LEADERBOARD;
                repaint();
            } else if (keyCode == KeyEvent.VK_H) {
                currentState = ScreenState.HOW_TO_PLAY;
                repaint();
            }
            return;
        }

        if (currentState == ScreenState.SELECT_PROFILE) {
            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_M) {
                currentState = ScreenState.MAIN_MENU;
                repaint();
            } else if (keyCode == KeyEvent.VK_C || keyCode == KeyEvent.VK_N) {
                promptCreateProfile();
            } else if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_9) {
                int idx = keyCode - KeyEvent.VK_1;
                if (idx < profiles.size()) {
                    selectProfileAndPlay(profiles.get(idx));
                }
            }
            return;
        }

        if (currentState == ScreenState.HOW_TO_PLAY) {
            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_M) {
                currentState = ScreenState.MAIN_MENU;
                repaint();
            }
            return;
        }

        if (currentState == ScreenState.LEADERBOARD) {
            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_M) {
                currentState = ScreenState.MAIN_MENU;
                repaint();
            } else if (keyCode == KeyEvent.VK_F) {
                promptLinearSearchGUI();
            } else if (keyCode == KeyEvent.VK_B) {
                promptBinarySearchGUI();
            }
            return;
        }

        if (currentState == ScreenState.SEARCH_DISPLAY) {
            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
                currentState = ScreenState.LEADERBOARD;
                repaint();
            }
            return;
        }

        if (currentState == ScreenState.GAME_OVER) {
            if (keyCode == KeyEvent.VK_R || keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
                startWithRecursiveCountdown();
            } else if (keyCode == KeyEvent.VK_P) {
                currentState = ScreenState.SELECT_PROFILE;
                repaint();
            } else if (keyCode == KeyEvent.VK_L) {
                currentState = ScreenState.LEADERBOARD;
                repaint();
            } else if (keyCode == KeyEvent.VK_M || keyCode == KeyEvent.VK_ESCAPE) {
                currentState = ScreenState.MAIN_MENU;
                repaint();
            }
            return;
        }

        if (currentState == ScreenState.PLAYING) {
            if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_P) {
                isPaused = !isPaused;
                repaint();
                return;
            }

            if (keyCode == KeyEvent.VK_R) {
                startWithRecursiveCountdown();
                return;
            }

            if (keyCode == KeyEvent.VK_M || keyCode == KeyEvent.VK_ESCAPE) {
                gameTimer.stop();
                currentState = ScreenState.MAIN_MENU;
                repaint();
                return;
            }

            // Enqueue inputs into CustomQueue<Direction>
            // Prevents direction reversals and buffers fast turns!
            if (inputQueue.size() < 3) {
                if ((keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP)) {
                    inputQueue.enqueue(Direction.UP);
                } else if ((keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN)) {
                    inputQueue.enqueue(Direction.DOWN);
                } else if ((keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT)) {
                    inputQueue.enqueue(Direction.LEFT);
                } else if ((keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT)) {
                    inputQueue.enqueue(Direction.RIGHT);
                }
            }
        }
    }

    /**
     * Prompts for player name and displays the step trace inside the GUI window.
     */
    private void promptLinearSearchGUI() {
        String query = JOptionPane.showInputDialog(
            this,
            "Enter Player Name to Search (Linear Search DSA):",
            "Linear Search Player",
            JOptionPane.QUESTION_MESSAGE
        );

        if (query != null && !query.trim().isEmpty()) {
            activeLinearResult = leaderboard.searchPlayer(query.trim());
            activeBinaryResult = null;
            currentState = ScreenState.SEARCH_DISPLAY;
            repaint();
        }
    }

    /**
     * Prompts for score and displays Recursive Binary Search step trace in the GUI window.
     */
    private void promptBinarySearchGUI() {
        String scoreStr = JOptionPane.showInputDialog(
            this,
            "Enter Target Score to Search (Recursive Binary Search DSA):",
            "Recursive Binary Search",
            JOptionPane.QUESTION_MESSAGE
        );

        if (scoreStr != null && !scoreStr.trim().isEmpty()) {
            try {
                int target = Integer.parseInt(scoreStr.trim());
                activeBinaryResult = leaderboard.searchByScore(target);
                activeLinearResult = null;
                currentState = ScreenState.SEARCH_DISPLAY;
                repaint();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric score.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState != ScreenState.PLAYING || isPaused) {
            return;
        }

        // Process buffered direction from CustomQueue
        while (!inputQueue.isEmpty()) {
            Direction nextDir = inputQueue.dequeue();
            if (nextDir == Direction.UP && currentDirection != Direction.DOWN) {
                dx = 0; dy = -1; currentDirection = Direction.UP; break;
            } else if (nextDir == Direction.DOWN && currentDirection != Direction.UP) {
                dx = 0; dy = 1; currentDirection = Direction.DOWN; break;
            } else if (nextDir == Direction.LEFT && currentDirection != Direction.RIGHT) {
                dx = -1; dy = 0; currentDirection = Direction.LEFT; break;
            } else if (nextDir == Direction.RIGHT && currentDirection != Direction.LEFT) {
                dx = 1; dy = 0; currentDirection = Direction.RIGHT; break;
            }
        }

        SnakeNode head = snake.getHead();
        int nextX = head.x + dx;
        int nextY = head.y + dy;

        // 1. Wall Collision Check
        if (nextX < 0 || nextX >= GRID_WIDTH || nextY < 0 || nextY >= GRID_HEIGHT) {
            triggerCollision("WALL COLLISION", "Out of bounds at (" + nextX + ", " + nextY + ")");
            return;
        }

        boolean willGrow = (nextX == food.getX() && nextY == food.getY());

        // 2. Self Collision Check via 2D Array
        SnakeNode tail = snake.getTail();
        boolean isSteppingOnVacatingTail = (!willGrow && tail != null && tail.x == nextX && tail.y == nextY);

        if (board[nextY][nextX] == SNAKE_CELL && !isSteppingOnVacatingTail) {
            triggerCollision("SELF COLLISION", "Array lookup board[" + nextY + "][" + nextX + "] == SNAKE");
            return;
        }

        // Move Doubly Linked List Snake with O(1) tail removal
        snake.move(dx, dy, willGrow);

        // 3. Self Collision Check via Doubly Linked List
        if (snake.checkSelfCollision()) {
            triggerCollision("SELF COLLISION", "Linked List traversal detected overlap");
            return;
        }

        // Food eaten
        if (willGrow) {
            // Second recursive function: RecursionMath calculates points recursively
            int earnedPoints = RecursionMath.calculateScoreBonus(snake.getLength());
            score += earnedPoints;

            eventStack.push("Ate Food: +" + earnedPoints + " pts (Length: " + snake.getLength() + ")");

            if (currentProfile != null && score > currentProfile.getHighScore()) {
                isNewHighScore = true;
                highScore = score;
            }
            foodsEaten++;

            // Increase difficulty
            if (foodsEaten % 2 == 0 && currentSpeed > MIN_SPEED_MS) {
                currentSpeed = Math.max(MIN_SPEED_MS, currentSpeed - SPEED_DECREMENT_MS);
                gameTimer.setDelay(currentSpeed);
                level++;
                eventStack.push("Level Up -> Level " + level + " (" + currentSpeed + "ms)");
            }

            // Safe deterministic food placement
            boolean placed = food.respawn(GRID_WIDTH, GRID_HEIGHT, snake);
            if (!placed) {
                // 100% board filled -> Game Won!
                triggerGameWon();
                return;
            }

            snake.printLinkedList();
        }

        syncBoard();
        repaint();
    }

    private void triggerGameWon() {
        currentState = ScreenState.GAME_OVER;
        collisionType = "VICTORY! BOARD 100% FILLED";
        collisionDetails = "Maximum possible snake length achieved (" + (GRID_WIDTH * GRID_HEIGHT) + " tiles)";
        gameTimer.stop();

        if (currentProfile != null) {
            currentProfile.updateHighScore(score);
            leaderboard.recordScore(currentProfile.getName(), score);
        }
        repaint();
    }

    private void triggerCollision(String type, String details) {
        currentState = ScreenState.GAME_OVER;
        collisionType = type;
        collisionDetails = details;

        if (gameTimer != null) {
            gameTimer.stop();
        }

        eventStack.push("Crash: " + type);

        if (currentProfile != null) {
            currentProfile.incrementGamesPlayed();
            if (currentProfile.updateHighScore(score)) {
                isNewHighScore = true;
                highScore = currentProfile.getHighScore();
            }

            if (score > 0) {
                leaderboard.recordScore(currentProfile.getName(), score);
                System.out.println(">> Leaderboard updated with player '" + currentProfile.getName()
                        + "' and score " + score + " (Bubble Sorted Plain Array)");
            }
        }

        System.out.println("\n💥 " + type + " DETECTED! " + details);
        System.out.println("Final Score: " + score + " | Length: " + snake.getLength());

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (currentState) {
            case MAIN_MENU:
                drawMainMenuScreen(g);
                break;
            case SELECT_PROFILE:
                drawProfileSelectionScreen(g);
                break;
            case COUNTDOWN:
                drawGameBoard(g);
                drawCountdownOverlay(g);
                break;
            case PLAYING:
                drawGameBoard(g);
                if (isPaused) {
                    drawPauseOverlay(g);
                }
                break;
            case GAME_OVER:
                drawGameBoard(g);
                drawGameOverScreen(g);
                break;
            case HOW_TO_PLAY:
                drawHowToPlayScreen(g);
                break;
            case LEADERBOARD:
                drawLeaderboardScreen(g);
                break;
            case SEARCH_DISPLAY:
                drawSearchDisplayScreen(g);
                break;
        }
    }

    private void drawGameBoard(Graphics g) {
        // --- 1. Top HUD Header Bar ---
        g.setColor(new Color(16, 18, 23));
        g.fillRect(0, 0, TOTAL_WIDTH, HEADER_HEIGHT);
        g.setColor(new Color(36, 42, 53));
        g.drawLine(0, HEADER_HEIGHT - 1, TOTAL_WIDTH, HEADER_HEIGHT - 1);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));

        g.setColor(new Color(155, 89, 182));
        String pName = (currentProfile != null) ? currentProfile.getName() : "Player";
        g.drawString("👤 " + pName, 20, 27);

        g.setColor(new Color(240, 240, 240));
        g.drawString("SCORE: " + score, 180, 27);

        g.setColor(new Color(52, 152, 219));
        g.drawString("LEVEL: " + level, 330, 27);

        g.setColor(new Color(241, 196, 15));
        g.drawString("BEST: " + highScore, 460, 27);

        g.setColor(new Color(46, 204, 113));
        g.drawString("LENGTH: " + (snake != null ? snake.getLength() : 3), 580, 27);

        g.setColor(new Color(160, 170, 185));
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("SPEED: " + currentSpeed + "ms", 695, 27);

        // --- 2. Board Grid Area ---
        int boardOffsetY = HEADER_HEIGHT;

        g.setColor(new Color(32, 36, 45));
        for (int r = 0; r <= GRID_HEIGHT; r++) {
            g.drawLine(0, boardOffsetY + r * CELL_SIZE, TOTAL_WIDTH, boardOffsetY + r * CELL_SIZE);
        }
        for (int c = 0; c <= GRID_WIDTH; c++) {
            g.drawLine(c * CELL_SIZE, boardOffsetY, c * CELL_SIZE, boardOffsetY + GRID_HEIGHT * CELL_SIZE);
        }

        if (food != null) {
            food.draw(g, CELL_SIZE, boardOffsetY);
        }

        if (snake != null) {
            SnakeNode current = snake.getHead();
            boolean isHead = true;

            while (current != null) {
                int px = current.x * CELL_SIZE;
                int py = boardOffsetY + current.y * CELL_SIZE;

                if (isHead) {
                    g.setColor(currentState == ScreenState.GAME_OVER ? new Color(231, 76, 60) : new Color(46, 204, 113));
                    g.fillRoundRect(px + 3, py + 3, CELL_SIZE - 6, CELL_SIZE - 6, 12, 12);

                    g.setColor(Color.WHITE);
                    int eyeSize = 6;
                    int eyeOffset1 = 10;
                    int eyeOffset2 = 24;

                    if (dx == 1) {
                        g.fillOval(px + 26, py + eyeOffset1, eyeSize, eyeSize);
                        g.fillOval(px + 26, py + eyeOffset2, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillOval(px + 29, py + eyeOffset1 + 1, 3, 3);
                        g.fillOval(px + 29, py + eyeOffset2 + 1, 3, 3);
                    } else if (dx == -1) {
                        g.fillOval(px + 8, py + eyeOffset1, eyeSize, eyeSize);
                        g.fillOval(px + 8, py + eyeOffset2, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillOval(px + 8, py + eyeOffset1 + 1, 3, 3);
                        g.fillOval(px + 8, py + eyeOffset2 + 1, 3, 3);
                    } else if (dy == -1) {
                        g.fillOval(px + eyeOffset1, py + 8, eyeSize, eyeSize);
                        g.fillOval(px + eyeOffset2, py + 8, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillOval(px + eyeOffset1 + 1, py + 8, 3, 3);
                        g.fillOval(px + eyeOffset2 + 1, py + 8, 3, 3);
                    } else {
                        g.fillOval(px + eyeOffset1, py + 26, eyeSize, eyeSize);
                        g.fillOval(px + eyeOffset2, py + 26, eyeSize, eyeSize);
                        g.setColor(Color.BLACK);
                        g.fillOval(px + eyeOffset1 + 1, py + 29, 3, 3);
                        g.fillOval(px + eyeOffset2 + 1, py + 29, 3, 3);
                    }
                    isHead = false;
                } else {
                    g.setColor(new Color(39, 174, 96));
                    g.fillRoundRect(px + 4, py + 4, CELL_SIZE - 8, CELL_SIZE - 8, 8, 8);
                }
                current = current.next;
            }
        }

        // --- 3. Bottom Control Bar ---
        int footerY = TOTAL_HEIGHT - FOOTER_HEIGHT;
        g.setColor(new Color(16, 18, 23));
        g.fillRect(0, footerY, TOTAL_WIDTH, FOOTER_HEIGHT);
        g.setColor(new Color(36, 42, 53));
        g.drawLine(0, footerY, TOTAL_WIDTH, footerY);

        g.setColor(new Color(140, 150, 165));
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Queue Buffer: " + inputQueue.size() + " inputs", 25, footerY + 30);
        g.drawString("[M] Menu", TOTAL_WIDTH - 90, footerY + 30);

        drawButton(g, gamePauseBtn, isPaused ? "▶ RESUME" : "❚❚ PAUSE",
                new Color(52, 73, 94), new Color(70, 95, 120), Color.WHITE, 13);
        drawButton(g, gameRestartBtn, "⟳ RESTART",
                new Color(192, 57, 43), new Color(231, 76, 60), Color.WHITE, 13);
    }

    private void drawMainMenuScreen(Graphics g) {
        g.setColor(new Color(15, 17, 22));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        g.setColor(new Color(25, 28, 36));
        for (int i = 0; i < TOTAL_WIDTH; i += 40) g.drawLine(i, 0, i, TOTAL_HEIGHT);
        for (int j = 0; j < TOTAL_HEIGHT; j += 40) g.drawLine(0, j, TOTAL_WIDTH, j);

        g.setColor(new Color(22, 26, 35, 240));
        g.fillRoundRect(160, 40, 480, 135, 20, 20);
        g.setColor(new Color(45, 55, 72));
        g.drawRoundRect(160, 40, 480, 135, 20, 20);

        g.setColor(new Color(46, 204, 113));
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        String title = "🐍 SNAKE DSA";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (TOTAL_WIDTH - tw) / 2, 105);

        g.setColor(new Color(180, 190, 205));
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        String sub = "Interactive Data Structures & Algorithms Mini Game";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, (TOTAL_WIDTH - sw) / 2, 145);

        drawButton(g, menuPlayBtn, "▶ PLAY GAME",
                new Color(39, 174, 96), new Color(46, 204, 113), Color.WHITE, 17);

        drawButton(g, menuLeaderboardBtn, "🏆 LEADERBOARD",
                new Color(41, 128, 185), new Color(52, 152, 219), Color.WHITE, 17);

        drawButton(g, menuHowToPlayBtn, "📖 HOW TO PLAY",
                new Color(142, 68, 173), new Color(155, 89, 182), Color.WHITE, 17);

        drawButton(g, menuExitBtn, "✕ EXIT",
                new Color(44, 62, 80), new Color(52, 73, 94), Color.WHITE, 17);

        if (currentProfile != null) {
            g.setColor(new Color(155, 89, 182));
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            String ap = "Active Player: " + currentProfile.getName() + " (Personal Best: " + currentProfile.getHighScore() + " pts)";
            int apw = g.getFontMetrics().stringWidth(ap);
            g.drawString(ap, (TOTAL_WIDTH - apw) / 2, 490);
        }

        g.setColor(new Color(130, 140, 155));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String dsaInfo = "Doubly Linked List (O(1) tail) • Queue • Stack • Bubble Sort • Binary & Linear Search • Recursion";
        int dw = g.getFontMetrics().stringWidth(dsaInfo);
        g.drawString(dsaInfo, (TOTAL_WIDTH - dw) / 2, TOTAL_HEIGHT - 35);
    }

    private void drawProfileSelectionScreen(Graphics g) {
        g.setColor(new Color(15, 17, 22));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        int cardW = 560;
        int cardH = 590;
        int cardX = (TOTAL_WIDTH - cardW) / 2;
        int cardY = 40;

        g.setColor(new Color(24, 28, 38));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g.setColor(new Color(50, 60, 80));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        g.setColor(new Color(46, 204, 113));
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        String title = "👤 SELECT YOUR PROFILE";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cardX + (cardW - tw) / 2, cardY + 45);

        g.setColor(new Color(170, 180, 195));
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        String sub = "Click your profile to start, or create a new one to join!";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, cardX + (cardW - sw) / 2, cardY + 74);

        g.setColor(new Color(45, 55, 75));
        g.drawLine(cardX + 30, cardY + 88, cardX + cardW - 30, cardY + 88);

        int startY = 140;
        int pCardH = 50;
        int gap = 10;
        int pCardW = 440;
        int pCardX = (TOTAL_WIDTH - pCardW) / 2;

        for (int i = 0; i < Math.min(profiles.size(), 6); i++) {
            Profile prof = profiles.get(i);
            Rectangle rect = new Rectangle(pCardX, startY + i * (pCardH + gap), pCardW, pCardH);

            boolean isHovered = (mousePoint != null && rect.contains(mousePoint));
            boolean isSelected = (currentProfile != null && currentProfile.getName().equals(prof.getName()));

            if (isSelected) {
                g.setColor(new Color(39, 174, 96, 60));
            } else if (isHovered) {
                g.setColor(new Color(41, 128, 185, 50));
            } else {
                g.setColor(new Color(32, 38, 50));
            }
            g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

            if (isSelected) {
                g.setColor(new Color(46, 204, 113));
            } else if (isHovered) {
                g.setColor(new Color(52, 152, 219));
            } else {
                g.setColor(new Color(55, 65, 82));
            }
            g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);

            g.setFont(new Font("SansSerif", Font.BOLD, 17));
            g.setColor(Color.WHITE);
            g.drawString("👤 " + prof.getName(), rect.x + 20, rect.y + 32);

            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.setColor(new Color(241, 196, 15));
            String hsStr = "Best: " + prof.getHighScore() + " pts";
            int hsw = g.getFontMetrics().stringWidth(hsStr);
            g.drawString(hsStr, rect.x + rect.width - 25 - hsw, rect.y + 32);
        }

        drawButton(g, profileCreateBtn, "+ CREATE NEW PROFILE",
                new Color(39, 174, 96), new Color(46, 204, 113), Color.WHITE, 15);

        drawButton(g, profileBackBtn, "⌂ BACK TO MENU",
                new Color(52, 73, 94), new Color(70, 95, 120), Color.WHITE, 14);
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 215));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        int cardW = 500;
        int cardH = 500;
        int cardX = (TOTAL_WIDTH - cardW) / 2;
        int cardY = 85;

        g.setColor(new Color(24, 28, 38));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g.setColor(new Color(50, 60, 80));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        g.setColor(new Color(231, 76, 60));
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        String title = "GAME OVER";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cardX + (cardW - tw) / 2, cardY + 50);

        g.setColor(new Color(241, 196, 15));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        int cdw = g.getFontMetrics().stringWidth(collisionDetails);
        g.drawString(collisionDetails, cardX + (cardW - cdw) / 2, cardY + 78);

        g.setColor(new Color(155, 89, 182));
        g.setFont(new Font("SansSerif", Font.BOLD, 17));
        String pName = "PLAYER: " + ((currentProfile != null) ? currentProfile.getName() : "Player");
        int pnw = g.getFontMetrics().stringWidth(pName);
        g.drawString(pName, cardX + (cardW - pnw) / 2, cardY + 115);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 32));
        String scoreStr = "SCORE: " + score;
        int scw = g.getFontMetrics().stringWidth(scoreStr);
        g.drawString(scoreStr, cardX + (cardW - scw) / 2, cardY + 155);

        if (isNewHighScore && score > 0) {
            g.setColor(new Color(241, 196, 15));
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            String nhs = "★ NEW HIGH SCORE! ★";
            int nhsw = g.getFontMetrics().stringWidth(nhs);
            g.drawString(nhs, cardX + (cardW - nhsw) / 2, cardY + 190);
        } else {
            g.setColor(new Color(170, 180, 195));
            g.setFont(new Font("SansSerif", Font.PLAIN, 15));
            String hsStr = "Personal Best: " + highScore + " pts  |  Snake Length: " + (snake != null ? snake.getLength() : 0);
            int hsw = g.getFontMetrics().stringWidth(hsStr);
            g.drawString(hsStr, cardX + (cardW - hsw) / 2, cardY + 190);
        }

        drawButton(g, goPlayAgainBtn, "⟳ PLAY AGAIN",
                new Color(39, 174, 96), new Color(46, 204, 113), Color.WHITE, 15);

        drawButton(g, goSwitchProfileBtn, "👤 SWITCH PROFILE",
                new Color(142, 68, 173), new Color(155, 89, 182), Color.WHITE, 15);

        drawButton(g, goLeaderboardBtn, "🏆 LEADERBOARD",
                new Color(41, 128, 185), new Color(52, 152, 219), Color.WHITE, 15);

        drawButton(g, goMainMenuBtn, "⌂ MAIN MENU",
                new Color(52, 73, 94), new Color(70, 95, 120), Color.WHITE, 15);
    }

    private void drawHowToPlayScreen(Graphics g) {
        g.setColor(new Color(15, 17, 22));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        int cardW = 660;
        int cardH = 570;
        int cardX = (TOTAL_WIDTH - cardW) / 2;
        int cardY = 50;

        g.setColor(new Color(24, 28, 38));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g.setColor(new Color(50, 60, 80));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        g.setColor(new Color(155, 89, 182));
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        String title = "HOW TO PLAY & DSA ARCHITECTURE GUIDE";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cardX + (cardW - tw) / 2, cardY + 40);

        g.setColor(new Color(45, 55, 75));
        g.drawLine(cardX + 30, cardY + 58, cardX + cardW - 30, cardY + 58);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(new Color(241, 196, 15));
        g.drawString("🎮 CONTROLS & GAMEPLAY:", cardX + 35, cardY + 84);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(Color.WHITE);
        g.drawString("• W/A/S/D or Arrows: Steer  |  Space: Pause  |  R: Restart  |  F: Search", cardX + 45, cardY + 106);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.setColor(new Color(46, 204, 113));
        g.drawString("📚 COMPREHENSIVE DATA STRUCTURES & ALGORITHMS (6 CORE COMPONENTS):", cardX + 35, cardY + 138);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(new Color(220, 225, 235));

        g.drawString("1. 2D Array Grid (int[][] board):", cardX + 45, cardY + 162);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   20x15 coordinate system with O(1) state verification (0=Empty, 1=Snake, 2=Food).", cardX + 60, cardY + 180);

        g.setColor(new Color(220, 225, 235));
        g.drawString("2. Doubly Linked List with Tail Pointer (Snake.java):", cardX + 45, cardY + 206);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   Upgraded with tail and prev pointers: O(1) head insertion and O(1) tail removal!", cardX + 60, cardY + 224);

        g.setColor(new Color(220, 225, 235));
        g.drawString("3. Hand-Built FIFO Queue (CustomQueue<Direction>):", cardX + 45, cardY + 250);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   Input Buffer Queue prevents dropped keystrokes during rapid turning (enqueue/dequeue O(1)).", cardX + 60, cardY + 268);

        g.setColor(new Color(220, 225, 235));
        g.drawString("4. Hand-Built LIFO Stack (CustomStack<String>):", cardX + 45, cardY + 294);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   Action & Score Event History Stack (push/pop O(1)) tracking all milestones.", cardX + 60, cardY + 312);

        g.setColor(new Color(220, 225, 235));
        g.drawString("5. Bubble Sort on Plain Arrays (BubbleSort.java):", cardX + 45, cardY + 338);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   Operates on LeaderboardEntry[] plain array (O(N^2) descending sort, no java.util.List).", cardX + 60, cardY + 356);

        g.setColor(new Color(220, 225, 235));
        g.drawString("6. In-Window Search & Two Recursive Functions:", cardX + 45, cardY + 382);
        g.setColor(new Color(170, 180, 195));
        g.drawString("   • Linear Search (O(N)) with full visual step trace in window.", cardX + 60, cardY + 400);
        g.drawString("   • Recursive Binary Search (O(log N)) by score on sorted array.", cardX + 60, cardY + 418);
        g.drawString("   • Recursion: Call-stack countdown (3->0) + RecursionMath scoring bonus.", cardX + 60, cardY + 436);

        drawButton(g, howBackBtn, "⌂ BACK TO MENU",
                new Color(52, 73, 94), new Color(70, 95, 120), Color.WHITE, 15);
    }

    private void drawLeaderboardScreen(Graphics g) {
        g.setColor(new Color(15, 17, 22));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        int cardW = 600;
        int cardH = 560;
        int cardX = (TOTAL_WIDTH - cardW) / 2;
        int cardY = 55;

        g.setColor(new Color(24, 28, 38));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g.setColor(new Color(50, 60, 80));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        g.setColor(new Color(241, 196, 15));
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        String title = "🏆 LEADERBOARD";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, cardX + (cardW - tw) / 2, cardY + 45);

        g.setColor(new Color(170, 180, 195));
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        String sub = "Sorted Plain Array using Bubble Sort (Descending)";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, cardX + (cardW - sw) / 2, cardY + 72);

        g.setColor(new Color(45, 55, 75));
        g.drawLine(cardX + 30, cardY + 85, cardX + cardW - 30, cardY + 85);

        LeaderboardEntry[] entries = leaderboard.getEntries();
        int entryY = cardY + 125;
        g.setFont(new Font("SansSerif", Font.BOLD, 17));

        for (int i = 0; i < Math.min(entries.length, 7); i++) {
            LeaderboardEntry entry = entries[i];

            if (i == 0) g.setColor(new Color(241, 196, 15)); // Gold
            else if (i == 1) g.setColor(new Color(189, 195, 199)); // Silver
            else if (i == 2) g.setColor(new Color(205, 127, 50)); // Bronze
            else g.setColor(Color.WHITE);

            String rank = (i + 1) + ".";
            g.drawString(rank, cardX + 60, entryY);
            g.drawString(entry.getName(), cardX + 115, entryY);

            g.setColor(new Color(46, 204, 113));
            String scoreStr = String.valueOf(entry.getScore());
            int scw = g.getFontMetrics().stringWidth(scoreStr);
            g.drawString(scoreStr, cardX + cardW - 80 - scw, entryY);

            entryY += 44;
        }

        // Action Buttons: Linear Search, Recursive Binary Search, Back
        drawButton(g, lbSearchBtn, "🔍 LINEAR SEARCH",
                new Color(41, 128, 185), new Color(52, 152, 219), Color.WHITE, 13);

        drawButton(g, lbBinSearchBtn, "⚡ BINARY SEARCH (RECURSIVE)",
                new Color(142, 68, 173), new Color(155, 89, 182), Color.WHITE, 12);

        drawButton(g, lbBackBtn, "⌂ BACK",
                new Color(52, 73, 94), new Color(70, 95, 120), Color.WHITE, 14);
    }

    /**
     * Screen: In-Window Search Result & Step Trace Display
     * Solves examiner requirement: renders step trace directly on screen for demo video!
     */
    private void drawSearchDisplayScreen(Graphics g) {
        g.setColor(new Color(15, 17, 22));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        int cardW = 620;
        int cardH = 550;
        int cardX = (TOTAL_WIDTH - cardW) / 2;
        int cardY = 60;

        g.setColor(new Color(24, 28, 38));
        g.fillRoundRect(cardX, cardY, cardW, cardH, 20, 20);
        g.setColor(new Color(50, 60, 80));
        g.drawRoundRect(cardX, cardY, cardW, cardH, 20, 20);

        String[] traceLines;
        boolean found;
        String headerTitle;
        String subTitle;

        if (activeLinearResult != null) {
            headerTitle = "🔍 LINEAR SEARCH RESULTS & STEP TRACE";
            subTitle = "Algorithm: Linear Search O(N) Sequential Traversal";
            traceLines = activeLinearResult.trace;
            found = activeLinearResult.found;
        } else if (activeBinaryResult != null) {
            headerTitle = "⚡ RECURSIVE BINARY SEARCH STEP TRACE";
            subTitle = "Algorithm: Recursive Binary Search O(log N) Divide & Conquer";
            traceLines = activeBinaryResult.trace;
            found = activeBinaryResult.found;
        } else {
            return;
        }

        // Title
        g.setColor(new Color(52, 152, 219));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        int tw = g.getFontMetrics().stringWidth(headerTitle);
        g.drawString(headerTitle, cardX + (cardW - tw) / 2, cardY + 45);

        // Subtitle
        g.setColor(new Color(170, 180, 195));
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        int sw = g.getFontMetrics().stringWidth(subTitle);
        g.drawString(subTitle, cardX + (cardW - sw) / 2, cardY + 70);

        g.setColor(new Color(45, 55, 75));
        g.drawLine(cardX + 30, cardY + 85, cardX + cardW - 30, cardY + 85);

        // Match Status Banner
        g.setFont(new Font("SansSerif", Font.BOLD, 17));
        if (found) {
            g.setColor(new Color(46, 204, 113));
            String matchStr = "✓ MATCH FOUND IN LEADERBOARD";
            g.drawString(matchStr, cardX + 40, cardY + 115);
        } else {
            g.setColor(new Color(231, 76, 60));
            String failStr = "✕ NOT FOUND ON LEADERBOARD";
            g.drawString(failStr, cardX + 40, cardY + 115);
        }

        // Step Trace Card Box
        g.setColor(new Color(16, 18, 24));
        g.fillRoundRect(cardX + 35, cardY + 135, cardW - 70, 310, 12, 12);
        g.setColor(new Color(42, 50, 65));
        g.drawRoundRect(cardX + 35, cardY + 135, cardW - 70, 310, 12, 12);

        // Step lines
        g.setFont(new Font("Monospaced", Font.PLAIN, 13));
        g.setColor(new Color(220, 225, 235));
        int traceY = cardY + 165;

        for (int i = 0; i < Math.min(traceLines.length, 9); i++) {
            String line = traceLines[i];
            if (line.contains("MATCH FOUND")) {
                g.setColor(new Color(46, 204, 113));
            } else if (line.contains("NOT FOUND")) {
                g.setColor(new Color(231, 76, 60));
            } else {
                g.setColor(new Color(200, 210, 225));
            }
            g.drawString(line, cardX + 50, traceY);
            traceY += 28;
        }

        // Footer step count
        g.setFont(new Font("SansSerif", Font.ITALIC, 13));
        g.setColor(new Color(241, 196, 15));
        String stepSummary = "Total Algorithmic Comparisons Executed: " + (traceLines.length - 1);
        g.drawString(stepSummary, cardX + 40, cardY + 475);

        // Close Button
        drawButton(g, searchCloseBtn, "✓ CLOSE SEARCH TRACE",
                new Color(39, 174, 96), new Color(46, 204, 113), Color.WHITE, 14);
    }

    private void drawCountdownOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        boolean isGo = "GO!".equals(countdownText);
        g.setFont(new Font("SansSerif", Font.BOLD, isGo ? 90 : 120));
        g.setColor(isGo ? new Color(46, 204, 113) : new Color(241, 196, 15));

        int cw = g.getFontMetrics().stringWidth(countdownText);
        g.drawString(countdownText, (TOTAL_WIDTH - cw) / 2, TOTAL_HEIGHT / 2 + 10);

        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.setColor(new Color(155, 89, 182));
        String pName = "Player: " + ((currentProfile != null) ? currentProfile.getName() : "Player");
        int pw = g.getFontMetrics().stringWidth(pName);
        g.drawString(pName, (TOTAL_WIDTH - pw) / 2, TOTAL_HEIGHT / 2 - 90);

        g.setFont(new Font("SansSerif", Font.BOLD, 17));
        g.setColor(new Color(220, 225, 235));
        String dsaNote = "Algorithm: Recursive Countdown -> countdown(" + countdownText + ")";
        int dw = g.getFontMetrics().stringWidth(dsaNote);
        g.drawString(dsaNote, (TOTAL_WIDTH - dw) / 2, TOTAL_HEIGHT / 2 + 85);

        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(new Color(160, 170, 185));
        String stackInfo = "Call Stack: countdown(3) ↓ countdown(2) ↓ countdown(1) ↓ countdown(0) [Base Case]";
        int siw = g.getFontMetrics().stringWidth(stackInfo);
        g.drawString(stackInfo, (TOTAL_WIDTH - siw) / 2, TOTAL_HEIGHT / 2 + 115);
    }

    private void drawPauseOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, TOTAL_WIDTH, TOTAL_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 46));
        String pText = "PAUSED";
        int pw = g.getFontMetrics().stringWidth(pText);
        g.drawString(pText, (TOTAL_WIDTH - pw) / 2, TOTAL_HEIGHT / 2 - 20);

        g.setColor(new Color(180, 190, 205));
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        String sub = "Press SPACE or Click RESUME to Continue";
        int sw = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, (TOTAL_WIDTH - sw) / 2, TOTAL_HEIGHT / 2 + 25);
    }

    private void drawButton(Graphics g, Rectangle r, String text, Color baseColor, Color hoverColor, Color textColor, int fontSize) {
        boolean isHovered = (mousePoint != null && r.contains(mousePoint));

        g.setColor(isHovered ? hoverColor : baseColor);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        g.setColor(isHovered ? Color.WHITE : new Color(255, 255, 255, 40));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);

        g.setColor(textColor);
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text);
        int th = fm.getAscent();
        int textX = r.x + (r.width - tw) / 2;
        int textY = r.y + (r.height + th) / 2 - 3;
        g.drawString(text, textX, textY);
    }
}
