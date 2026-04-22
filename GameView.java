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
            g.fillRect(s.x, s.y, s.size, s.size);
        }

        // Draw player spaceship or explosion
        int playerY = SCREEN_HEIGHT - PLAYER_HEIGHT;
        if (model.getExplosionTimer() > 0) {
            // Draw explosion
            g.setColor(Color.ORANGE);
            g.fillOval(model.getPlayerX() - 10, playerY - 10, PLAYER_WIDTH + 20, PLAYER_HEIGHT + 20);
            g.setColor(Color.RED);
            g.fillOval(model.getPlayerX(), playerY, PLAYER_WIDTH, PLAYER_HEIGHT);
        } else {
            // Draw player spaceship (triangle)
            g.setColor(Color.CYAN);
            int[] xPoints = {model.getPlayerX() + PLAYER_WIDTH / 2, model.getPlayerX(), model.getPlayerX() + PLAYER_WIDTH};
            int[] yPoints = {playerY, playerY + PLAYER_HEIGHT, playerY + PLAYER_HEIGHT};
            g.fillPolygon(xPoints, yPoints, 3);
        }

        // Draw aliens
        boolean[][] alive = model.getAliensAlive();
        int fx = model.getAlienFormationX();
        int fy = model.getAlienFormationY();
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                if (alive[r][c]) {
                    int ax = fx + c * ALIEN_SPACING_X;
                    int ay = fy + r * ALIEN_SPACING_Y;
                    // Different colors per row
                    switch (r) {
                        case 0: g.setColor(Color.GREEN); break;
                        case 1: g.setColor(Color.BLUE); break;
                        case 2: g.setColor(Color.MAGENTA); break;
                        case 3: g.setColor(Color.ORANGE); break;
                        case 4: g.setColor(Color.PINK); break;
                    }
                    g.fillRect(ax, ay, ALIEN_WIDTH, ALIEN_HEIGHT);
                    // Eyes
                    g.setColor(Color.BLACK);
                    g.fillOval(ax + 5, ay + 5, 5, 5);
                    g.fillOval(ax + ALIEN_WIDTH - 10, ay + 5, 5, 5);
                    // Legs
                    g.setColor(Color.WHITE);
                    g.drawLine(ax + 5, ay + ALIEN_HEIGHT, ax + 5, ay + ALIEN_HEIGHT + 5);
                    g.drawLine(ax + ALIEN_WIDTH - 5, ay + ALIEN_HEIGHT, ax + ALIEN_WIDTH - 5, ay + ALIEN_HEIGHT + 5);
                }
            }
        }

        // Draw player bullet (yellow)
        g.setColor(Color.YELLOW);
        GameModel.Bullet pb = model.getPlayerBullet();
        if (pb != null) {
            g.fillRect(pb.x - 2, pb.y - 10, 4, 10);
        }

        // Draw alien bullets (bright red)
        g.setColor(Color.RED);
        for (GameModel.Bullet b : model.getAlienBullets()) {
            g.fillRect(b.x - 2, b.y, 4, 10);
        }

        // Draw score and lives
        g.setColor(Color.WHITE);
        g.drawString("Score: " + model.getScore(), 10, 20);
        g.drawString("Lives: " + model.getLives(), 10, 40);
        g.drawString("Level: " + model.getLevel(), 10, 60);

        // Draw instructions
        g.drawString("Arrow keys: move, Space: fire", 10, SCREEN_HEIGHT - 40);

        // Draw level up message
        if (model.getLevelUpTimer() > 0) {
            g.setColor(Color.YELLOW);
            String msg = "LEVEL UP!";
            FontMetrics fm = g.getFontMetrics();
            int x = (SCREEN_WIDTH - fm.stringWidth(msg)) / 2;
            int y = SCREEN_HEIGHT / 2;
            // Background
            g.setColor(Color.BLACK);
            g.fillRect(x - 10, y - fm.getHeight() - 5, fm.stringWidth(msg) + 20, fm.getHeight() + 10);
            g.setColor(Color.YELLOW);
            g.drawString(msg, x, y);
        }

        // Draw game over message if game is over
        if (model.isGameOver()) {
            g.setColor(Color.RED);
            String msg = "GAME OVER";
            FontMetrics fm = g.getFontMetrics();
            int x = (SCREEN_WIDTH - fm.stringWidth(msg)) / 2;
            int y = SCREEN_HEIGHT / 2;
            // Background
            g.setColor(Color.BLACK);
            g.fillRect(x - 10, y - fm.getHeight() - 5, fm.stringWidth(msg) + 20, fm.getHeight() + 10);
            g.setColor(Color.RED);
            g.drawString(msg, x, y);
            // Restart instruction
            g.setColor(Color.WHITE);
            String restartMsg = "Press R to restart";
            int rx = (SCREEN_WIDTH - fm.stringWidth(restartMsg)) / 2;
            g.drawString(restartMsg, rx, y + 30);
        }
    }
}