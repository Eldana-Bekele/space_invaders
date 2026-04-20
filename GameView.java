import javax.swing.*;
import java.awt.*;

public class GameView extends JPanel {
    // Placeholder view responsible for rendering the GameModel
    // Responsibilities:
    // - Draw player, invaders, bullets, and UI (score, lives)
    // - Provide methods to request repaint when model updates

    private final GameModel model;

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);
    }

    // Rendering will be implemented here
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // TODO: Render game objects from model
    }
}
