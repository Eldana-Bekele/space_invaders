import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController {
    // This class coordinates between the model and the view.
    // It handles user input (key presses), updates the model via a timer, and refreshes the view.
    // It contains the main method to start the application.

    public static void main(String[] args) {
        // Create instances of the model, view, and controller
        GameModel model = new GameModel();
        GameView view = new GameView();
        view.setModel(model);

        // Create the main window
        JFrame frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(view);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setFocusable(true);

        // Declare timer for access in key listener
        final Timer[] timerRef = new Timer[1];

        // Add key listener for player input
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        model.movePlayerLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        model.movePlayerRight();
                        break;
                    case KeyEvent.VK_SPACE:
                        model.firePlayerBullet();
                        break;
                    case KeyEvent.VK_R:
                        if (model.isGameOver() && timerRef[0] != null) {
                            model.reset();
                            timerRef[0].start();
                        }
                        break;
                }
            }
        });

        frame.setVisible(true);

        // Start game loop timer (~60 FPS)
        timerRef[0] = new Timer(16, e -> {
            model.update();
            view.repaint();
            if (model.isGameOver()) {
                ((Timer) e.getSource()).stop();
            }
        });
        timerRef[0].start();
    }
}