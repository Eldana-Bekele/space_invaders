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

        // Draw space background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Draw stars
        g.setColor(Color.WHITE);
        for (GameModel.Star s : model.getStars()) {
            g.fillRect(s.x, s.y, 1, 1);
        }

        // Draw player spaceship (triangle)
        g.setColor(Color.CYAN);
        int playerY = SCREEN_HEIGHT - PLAYER_HEIGHT;
        int[] xPoints = {model.getPlayerX() + PLAYER_WIDTH / 2, model.getPlayerX(), model.getPlayerX() + PLAYER_WIDTH};
        int[] yPoints = {playerY, playerY + PLAYER_HEIGHT, playerY + PLAYER_HEIGHT};
        g.fillPolygon(xPoints, yPoints, 3);

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

        // Draw player bullet (bright white)
        g.setColor(Color.WHITE);
        GameModel.Bullet pb = model.getPlayerBullet();
        if (pb != null) {
            g.fillRect(pb.x - 1, pb.y - 10, 3, 10);
        }

        // Draw alien bullets (bright red)
        g.setColor(Color.RED);
        for (GameModel.Bullet b : model.getAlienBullets()) {
            g.fillRect(b.x - 1, b.y, 3, 10);
        }

        // Draw score and lives
        g.setColor(Color.WHITE);
        g.drawString("Score: " + model.getScore(), 10, 20);
        g.drawString("Lives: " + model.getLives(), 10, 40);
        g.drawString("Level: " + model.getLevel(), 10, 60);

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