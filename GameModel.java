import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameModel {
    // Game field size (matches view preferred size)
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    // Player
    private final int playerWidth = 48;
    private final int playerHeight = 16;
    private final int playerY = HEIGHT - 80;
    private int playerX; // center X
    private final int playerSpeed = 8;

    // Aliens formation
    public static final int ALIEN_ROWS = 5;
    public static final int ALIEN_COLS = 11;
    private final boolean[][] aliens = new boolean[ALIEN_ROWS][ALIEN_COLS];
    private int formationX; // top-left of formation
    private int formationY;
    private int alienSpacingX = 48;
    private int alienSpacingY = 40;
    private int alienWidth = 32;
    private int alienHeight = 24;
    private int alienDir = 1; // 1 = right, -1 = left
    private int alienSpeed = 4; // pixels per tick
    private int dropDistance = 20;

    // Player bullet (single)
    public static class Bullet {
        public int x, y;
        public int dy;
        public boolean active;

        public Bullet(int x, int y, int dy) {
            this.x = x;
            this.y = y;
            this.dy = dy;
            this.active = true;
        }
    }

    private Bullet playerBullet = null;

    // Alien bullets (multiple)
    private final List<Bullet> alienBullets = new ArrayList<>();

    // Score & lives
    private int score = 0;
    private int lives = 3;

    // Alien firing cooldown (ticks)
    private final Random rand = new Random();
    private int alienFireCooldown = 60; // initial

    public GameModel() {
        resetFormation();
        playerX = WIDTH / 2;
    }

    private void resetFormation() {
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                aliens[r][c] = true;
            }
        }
        formationX = 40;
        formationY = 40;
        alienDir = 1;
    }

    // --- Player control ---
    public void movePlayerLeft() {
        playerX -= playerSpeed;
        if (playerX - playerWidth/2 < 0) playerX = playerWidth/2;
    }

    public void movePlayerRight() {
        playerX += playerSpeed;
        if (playerX + playerWidth/2 > WIDTH) playerX = WIDTH - playerWidth/2;
    }

    public void firePlayerBullet() {
        if (playerBullet == null || !playerBullet.active) {
            playerBullet = new Bullet(playerX, playerY, -12);
        }
    }

    // --- Update per tick ---
    public void update() {
        advancePlayerBullet();
        advanceAlienBullets();
        moveAliens();
        tryAlienFire();
        detectCollisions();
    }

    private void advancePlayerBullet() {
        if (playerBullet != null && playerBullet.active) {
            playerBullet.y += playerBullet.dy;
            if (playerBullet.y < 0) playerBullet.active = false;
        }
    }

    private void advanceAlienBullets() {
        Iterator<Bullet> it = alienBullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.y += b.dy;
            if (b.y > HEIGHT) it.remove();
        }
    }

    private void moveAliens() {
        if (alienSpeed == 0) return;
        formationX += alienDir * alienSpeed;

        // compute formation width
        int formationWidth = (ALIEN_COLS - 1) * alienSpacingX + alienWidth;

        // check edges
        if (formationX <= 0 && alienDir < 0) {
            formationX = 0;
            formationY += dropDistance;
            alienDir = 1;
        } else if (formationX + formationWidth >= WIDTH && alienDir > 0) {
            formationX = WIDTH - formationWidth;
            formationY += dropDistance;
            alienDir = -1;
        }
    }

    private void tryAlienFire() {
        alienFireCooldown--;
        if (alienFireCooldown <= 0) {
            // pick a random alive alien (prefer bottom-most in a random column)
            int attempts = 0;
            boolean fired = false;
            while (attempts < 20 && !fired) {
                int c = rand.nextInt(ALIEN_COLS);
                int shooterRow = -1;
                for (int r = ALIEN_ROWS - 1; r >= 0; r--) {
                    if (aliens[r][c]) { shooterRow = r; break; }
                }
                if (shooterRow >= 0) {
                    int ax = getAlienX(c) + alienWidth/2;
                    int ay = getAlienY(shooterRow) + alienHeight;
                    alienBullets.add(new Bullet(ax, ay, 6));
                    fired = true;
                }
                attempts++;
            }
            alienFireCooldown = 30 + rand.nextInt(90); // next cooldown
        }
    }

    private void detectCollisions() {
        // Player bullet vs aliens
        if (playerBullet != null && playerBullet.active) {
            outer:
            for (int r = 0; r < ALIEN_ROWS; r++) {
                for (int c = 0; c < ALIEN_COLS; c++) {
                    if (!aliens[r][c]) continue;
                    int ax = getAlienX(c);
                    int ay = getAlienY(r);
                    if (pointInRect(playerBullet.x, playerBullet.y, ax, ay, alienWidth, alienHeight)) {
                        aliens[r][c] = false;
                        playerBullet.active = false;
                        score += 100;
                        break outer;
                    }
                }
            }
        }

        // Alien bullets vs player
        Iterator<Bullet> it = alienBullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            int px = playerX - playerWidth/2;
            int py = playerY - playerHeight/2;
            if (pointInRect(b.x, b.y, px, py, playerWidth, playerHeight)) {
                it.remove();
                lives--;
                if (lives < 0) lives = 0;
            }
        }
    }

    private boolean pointInRect(int px, int py, int rx, int ry, int rw, int rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }

    // --- Accessors for the view/controller ---
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public int getPlayerWidth() { return playerWidth; }

    public boolean isAlienAlive(int row, int col) {
        return aliens[row][col];
    }

    public int getAlienX(int col) { return formationX + col * alienSpacingX; }
    public int getAlienY(int row) { return formationY + row * alienSpacingY; }
    public int getAlienWidth() { return alienWidth; }
    public int getAlienHeight() { return alienHeight; }

    public Bullet getPlayerBullet() { return (playerBullet != null && playerBullet.active) ? playerBullet : null; }
    public List<Bullet> getAlienBullets() { return new ArrayList<>(alienBullets); }

    public int getScore() { return score; }
    public int getLives() { return lives; }
}

