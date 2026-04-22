import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.FontMetrics;

public class GameView extends JPanel {
    // This class handles the graphical representation of the game.
    // It extends JPanel and overrides paintComponent to draw the player, aliens, bullets, score, lives, and game-over message.
    // It only reads from the model and never modifies game state.

    private GameModel model;
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 50;
    private static final int ALIEN_WIDTH = 40;
    private static final int ALIEN_HEIGHT = 30;
    private static final int ALIEN_SPACING_X = 50;
    private static final int ALIEN_SPACING_Y = 40;
    private static final int ALIEN_ROWS = 5;
    private static final int ALIEN_COLS = 11;

    public GameView() {
        setPreferredSize(new java.awt.Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    }

    public void setModel(GameModel model) {
        this.model = model;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (model == null) return;

        // Draw player
        g.setColor(Color.BLUE);
        g.fillRect(model.getPlayerX(), SCREEN_HEIGHT - PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);

        // Draw aliens
        g.setColor(Color.GREEN);
        boolean[][] alive = model.getAliensAlive();
        int fx = model.getAlienFormationX();
        int fy = model.getAlienFormationY();
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                if (alive[r][c]) {
                    g.fillRect(fx + c * ALIEN_SPACING_X, fy + r * ALIEN_SPACING_Y, ALIEN_WIDTH, ALIEN_HEIGHT);
                }
            }
        }

        // Draw player bullet
        g.setColor(Color.RED);
        GameModel.Bullet pb = model.getPlayerBullet();
        if (pb != null) {
            g.fillRect(pb.x - 2, pb.y - 5, 4, 10);
        }

        // Draw alien bullets
        g.setColor(Color.YELLOW);
        for (GameModel.Bullet b : model.getAlienBullets()) {
            g.fillRect(b.x - 2, b.y, 4, 10);
        }

        // Draw score and lives
        g.setColor(Color.BLACK);
        g.drawString("Score: " + model.getScore(), 10, 20);
        g.drawString("Lives: " + model.getLives(), 10, 40);

        // Draw instructions
        g.drawString("Arrow keys: move, Space: fire", 10, SCREEN_HEIGHT - 40);
        if (model.isGameOver()) {
            g.drawString("Press R to restart", 10, SCREEN_HEIGHT - 20);
        }

        // Draw game over message if game is over
        if (model.isGameOver()) {
            g.setColor(Color.RED);
            String msg = "Game Over";
            FontMetrics fm = g.getFontMetrics();
            int x = (SCREEN_WIDTH - fm.stringWidth(msg)) / 2;
            int y = SCREEN_HEIGHT / 2;
            g.drawString(msg, x, y);
        }
    }
}