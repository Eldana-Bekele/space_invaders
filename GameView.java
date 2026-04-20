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
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            // Smooth text and shapes
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw HUD: score (top-left) and lives (top-right)
            g2.setColor(Color.WHITE);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
            String scoreText = "Score: " + model.getScore();
            g2.drawString(scoreText, 12, 20);

            String livesText = "Lives: " + model.getLives();
            int lw = g2.getFontMetrics().stringWidth(livesText);
            g2.drawString(livesText, getWidth() - lw - 12, 20);

            // Draw player
            g2.setColor(Color.GREEN);
            int px = model.getPlayerX();
            int pw = model.getPlayerWidth();
            int py = model.getPlayerY();
            g2.fillRect(px - pw/2, py - 8, pw, 16);

            // Draw aliens
            g2.setColor(Color.CYAN);
            for (int r = 0; r < GameModel.ALIEN_ROWS; r++) {
                for (int c = 0; c < GameModel.ALIEN_COLS; c++) {
                    if (model.isAlienAlive(r, c)) {
                        int ax = model.getAlienX(c);
                        int ay = model.getAlienY(r);
                        g2.fillRect(ax, ay, model.getAlienWidth(), model.getAlienHeight());
                    }
                }
            }

            // Draw player bullet
            GameModel.Bullet pb = model.getPlayerBullet();
            if (pb != null) {
                g2.setColor(Color.YELLOW);
                g2.fillRect(pb.x - 2, pb.y - 6, 4, 12);
            }

            // Draw alien bullets
            g2.setColor(Color.RED);
            for (GameModel.Bullet b : model.getAlienBullets()) {
                g2.fillRect(b.x - 3, b.y - 6, 6, 12);
            }

            // Game end messages
            boolean anyAlien = false;
            for (int r = 0; r < GameModel.ALIEN_ROWS && !anyAlien; r++) {
                for (int c = 0; c < GameModel.ALIEN_COLS; c++) {
                    if (model.isAlienAlive(r, c)) { anyAlien = true; break; }
                }
            }

            if (model.getLives() <= 0 || !anyAlien) {
                String msg = model.getLives() <= 0 ? "GAME OVER" : "YOU WIN";
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48f));
                FontMetrics fm = g2.getFontMetrics();
                int mw = fm.stringWidth(msg);
                int mx = (getWidth() - mw) / 2;
                int my = getHeight() / 2;
                g2.setColor(new Color(0,0,0,160));
                g2.fillRect(mx - 20, my - fm.getAscent(), mw + 40, fm.getAscent() + fm.getDescent() + 20);
                g2.setColor(Color.WHITE);
                g2.drawString(msg, mx, my + fm.getAscent()/2 - 8);
            }
        } finally {
            g2.dispose();
        }
    }
}
