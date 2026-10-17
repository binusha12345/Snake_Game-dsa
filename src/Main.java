import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {

        JFrame frame = new JFrame("Snake DSA Game");

        // Attach GamePanel containing the 2D Array Grid
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack(); // Pack frame around the 800x600 preferred panel size
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
