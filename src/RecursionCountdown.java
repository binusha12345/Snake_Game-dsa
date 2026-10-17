import javax.swing.SwingUtilities;

/**
 * Implements a genuine Recursive Countdown for starting the Snake game.
 * 
 * DSA Component: Recursion
 * 
 * Concept:
 *   countdown(3)
 *       ↓
 *   countdown(2)
 *       ↓
 *   countdown(1)
 *       ↓
 *   countdown(0)  [Base Case]
 *       ↓
 *   START GAME
 * 
 * Execution:
 *   Each invocation allocates an independent stack frame on the JVM call stack.
 *   Executes on a dedicated thread to ensure the call stack advances while 
 *   safely updating the Swing UI via SwingUtilities.invokeLater.
 */
public class RecursionCountdown {

    public interface CountdownListener {
        void onTick(String text);
        void onComplete();
    }

    private final CountdownListener listener;

    public RecursionCountdown(CountdownListener listener) {
        this.listener = listener;
    }

    /**
     * Starts the countdown recursively from startNumber down to 0.
     * 
     * @param startNumber initial number (e.g. 3)
     */
    public void start(int startNumber) {
        new Thread(() -> {
            System.out.println("===================================================");
            System.out.println(" RECURSION CALL STACK INITIATED: countdown(" + startNumber + ")");
            System.out.println("===================================================");
            recursiveCountdown(startNumber, 1);
            System.out.println("===================================================\n");
        }, "Recursion-Countdown-Thread").start();
    }

    /**
     * Genuine recursive method.
     * 
     * Base Case: n <= 0
     * Recursive Step: process n, sleep delay, then call recursiveCountdown(n - 1)
     * 
     * @param n current countdown number
     * @param depth recursion call-stack depth
     */
    private void recursiveCountdown(int n, int depth) {
        // Base Case
        if (n <= 0) {
            System.out.println("  [Stack Depth " + depth + "] countdown(" + n + ") -> BASE CASE REACHED! -> 'GO!' -> START GAME");

            SwingUtilities.invokeLater(() -> {
                if (listener != null) {
                    listener.onTick("GO!");
                }
            });

            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            SwingUtilities.invokeLater(() -> {
                if (listener != null) {
                    listener.onComplete();
                }
            });
            return;
        }

        // Recursive Case
        System.out.println("  [Stack Depth " + depth + "] countdown(" + n + ") -> Display: '" + n + "'");

        SwingUtilities.invokeLater(() -> {
            if (listener != null) {
                listener.onTick(String.valueOf(n));
            }
        });

        try {
            Thread.sleep(850);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // --- ACTUAL RECURSIVE CALL ---
        recursiveCountdown(n - 1, depth + 1);
    }
}
