import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController {
    // Controller wires GameModel and GameView, handles input and the game loop.
    private final GameModel model;
    private final GameView view;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private final Timer gameTimer;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;

        // Key handling: arrow keys and spacebar
        view.setFocusable(true);
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_LEFT) leftPressed = true;
                else if (code == KeyEvent.VK_RIGHT) rightPressed = true;
                else if (code == KeyEvent.VK_SPACE) model.firePlayerBullet();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_LEFT) leftPressed = false;
                else if (code == KeyEvent.VK_RIGHT) rightPressed = false;
            }
        });

        // Game loop timer (~30 FPS)
        gameTimer = new Timer(33, evt -> {
            if (leftPressed) model.movePlayerLeft();
            if (rightPressed) model.movePlayerRight();

            model.update();
            view.repaint();

            if (isGameOver()) {
                gameTimer.stop();
            }
        });
        gameTimer.start();
    }

    private boolean isGameOver() {
        if (model.getLives() <= 0) return true;
        for (int r = 0; r < GameModel.ALIEN_ROWS; r++) {
            for (int c = 0; c < GameModel.ALIEN_COLS; c++) {
                if (model.isAlienAlive(r, c)) return false;
            }
        }
        return true; // no aliens left -> win
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameModel model = new GameModel();
            GameView view = new GameView(model);
            new GameController(model, view);

            JFrame frame = new JFrame("Space Invaders");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ensure the view has keyboard focus
            view.requestFocusInWindow();
        });
    }
}
