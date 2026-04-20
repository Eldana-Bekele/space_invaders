import javax.swing.*;

public class GameController {
    // Controller wires GameModel and GameView, handles input and the game loop.
    // Responsibilities:
    // - Add input listeners to the view
    // - Drive game updates (Timers / game loop)
    // - Coordinate model updates and view repaints

    private final GameModel model;
    private final GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
        // TODO: Attach listeners and start game loop / timers
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
        });
    }
}
