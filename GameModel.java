import java.util.*;

public class GameModel {
    // This class manages the game state, including positions, bullets, score, and lives.
    // It provides methods to update the game logic each tick, handle player input, and detect collisions.

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 50;
    private static final int PLAYER_SPEED = 6;
    private static final int BULLET_SPEED = 30;
    private static final int ALIEN_ROWS = 5;
    private static final int ALIEN_COLS = 11;
    private static final int ALIEN_WIDTH = 40;
    private static final int ALIEN_HEIGHT = 30;
    private static final int ALIEN_SPACING_X = 50;
    private static final int ALIEN_SPACING_Y = 40;
    private int formationSpeed = 3;
    private static final int FORMATION_DROP = 10;

    private int playerX = SCREEN_WIDTH / 2;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean[][] aliensAlive = new boolean[ALIEN_ROWS][ALIEN_COLS];
    private int alienFormationX = 50;
    private int alienFormationY = 50;
    private boolean movingRight = true;
    private Bullet playerBullet = null;
    private List<Bullet> alienBullets = new ArrayList<>();
    private int score = 0;
    private int lives = 3;
    private int level = 1;
    private int alienBulletSpeed = 20;
    private int alienShootChance = 5;
    private int explosionTimer = 0;
    private List<Star> stars = new ArrayList<>();
    private Random random = new Random();

    public GameModel() {
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                aliensAlive[r][c] = true;
            }
        }
        for (int i = 0; i < 50; i++) {
            int size = random.nextInt(2) + 1; // 1 or 2
            stars.add(new Star(random.nextInt(SCREEN_WIDTH), random.nextInt(SCREEN_HEIGHT), 1, size));
        }
    }

    public void reset() {
        playerX = SCREEN_WIDTH / 2;
        leftPressed = false;
        rightPressed = false;
        level = 1;
        alienBulletSpeed = 20;
        formationSpeed = 3;
        alienShootChance = 5;
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                aliensAlive[r][c] = true;
            }
        }
        alienFormationX = 50;
        alienFormationY = 50;
        movingRight = true;
        playerBullet = null;
        alienBullets.clear();
        score = 0;
        explosionTimer = 0;
        lives = 3;
    }

    public void movePlayerLeft() {
        leftPressed = true;
    }

    public void movePlayerRight() {
        rightPressed = true;
    }

    public void stopPlayerLeft() {
        leftPressed = false;
    }

    public void stopPlayerRight() {
        rightPressed = false;
    }

    public void firePlayerBullet() {
        if (playerBullet == null) {
            playerBullet = new Bullet(playerX + PLAYER_WIDTH / 2, SCREEN_HEIGHT - PLAYER_HEIGHT, true);
        }
    }

    public void update() {
        // Move stars
        for (Star s : stars) {
            s.y += s.dy;
            if (s.y > SCREEN_HEIGHT) {
                s.y = 0;
                s.x = random.nextInt(SCREEN_WIDTH);
            }
        }

        // Decrement explosion timer
        if (explosionTimer > 0) {
            explosionTimer--;
        }

        // Move player
        if (leftPressed && explosionTimer == 0) {
            playerX = Math.max(0, playerX - PLAYER_SPEED);
        }
        if (rightPressed && explosionTimer == 0) {
            playerX = Math.min(SCREEN_WIDTH - PLAYER_WIDTH, playerX + PLAYER_SPEED);
        }

        // Advance player bullet
        if (playerBullet != null) {
            playerBullet.y -= BULLET_SPEED;
            if (playerBullet.y < 0) {
                playerBullet = null;
            }
        }

        // Advance alien bullets
        for (Iterator<Bullet> it = alienBullets.iterator(); it.hasNext(); ) {
            Bullet b = it.next();
            b.y += alienBulletSpeed;
            if (b.y > SCREEN_HEIGHT) {
                it.remove();
            }
        }

        // Move alien formation
        if (movingRight) {
            alienFormationX += formationSpeed;
            if (alienFormationX + ALIEN_COLS * ALIEN_SPACING_X > SCREEN_WIDTH) {
                alienFormationX -= formationSpeed;
                alienFormationY += FORMATION_DROP;
                movingRight = false;
            }
        } else {
            alienFormationX -= formationSpeed;
            if (alienFormationX < 0) {
                alienFormationX += formationSpeed;
                alienFormationY += FORMATION_DROP;
                movingRight = true;
            }
        }

        // Random alien fire
        if (random.nextInt(100) < alienShootChance) {
            fireAlienBullet();
        }

        // Check collisions
        checkCollisions();

        // Check for level up
        if (!hasAliensLeft()) {
            levelUp();
        }
    }

    private void fireAlienBullet() {
        List<int[]> aliveAliens = new ArrayList<>();
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                if (aliensAlive[r][c]) {
                    aliveAliens.add(new int[]{r, c});
                }
            }
        }
        if (!aliveAliens.isEmpty()) {
            int[] alien = aliveAliens.get(random.nextInt(aliveAliens.size()));
            int x = alienFormationX + alien[1] * ALIEN_SPACING_X + ALIEN_WIDTH / 2;
            int y = alienFormationY + alien[0] * ALIEN_SPACING_Y + ALIEN_HEIGHT;
            alienBullets.add(new Bullet(x, y, false));
        }
    }

    private void checkCollisions() {
        // Player bullet vs aliens
        if (playerBullet != null) {
            for (int r = 0; r < ALIEN_ROWS; r++) {
                for (int c = 0; c < ALIEN_COLS; c++) {
                    if (aliensAlive[r][c]) {
                        int ax = alienFormationX + c * ALIEN_SPACING_X;
                        int ay = alienFormationY + r * ALIEN_SPACING_Y;
                        if (playerBullet.x >= ax && playerBullet.x <= ax + ALIEN_WIDTH &&
                            playerBullet.y >= ay && playerBullet.y <= ay + ALIEN_HEIGHT) {
                            aliensAlive[r][c] = false;
                            score += 10;
                            playerBullet = null;
                            return;
                        }
                    }
                }
            }
        }

        // Alien bullets vs player
        int playerY = SCREEN_HEIGHT - PLAYER_HEIGHT;
        for (Iterator<Bullet> it = alienBullets.iterator(); it.hasNext(); ) {
            Bullet b = it.next();
            if (b.x >= playerX && b.x <= playerX + PLAYER_WIDTH &&
                b.y >= playerY && b.y <= playerY + PLAYER_HEIGHT) {
                lives--;
                explosionTimer = 20;
                it.remove();
            }
        }
    }

    private boolean hasAliensLeft() {
        for (boolean[] row : aliensAlive) {
            for (boolean alive : row) {
                if (alive) return true;
            }
        }
        return false;
    }

    private void levelUp() {
        level++;
        alienBulletSpeed += 2;
        formationSpeed += 1;
        alienShootChance += 1;
        // Reset aliens
        for (int r = 0; r < ALIEN_ROWS; r++) {
            for (int c = 0; c < ALIEN_COLS; c++) {
                aliensAlive[r][c] = true;
            }
        }
        alienFormationX = 50;
        alienFormationY = 50;
        movingRight = true;
        alienBullets.clear();
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    // Getters
    public int getPlayerX() { return playerX; }
    public boolean[][] getAliensAlive() { return aliensAlive; }
    public int getAlienFormationX() { return alienFormationX; }
    public int getAlienFormationY() { return alienFormationY; }
    public Bullet getPlayerBullet() { return playerBullet; }
    public List<Bullet> getAlienBullets() { return alienBullets; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getLevel() { return level; }
    public List<Star> getStars() { return stars; }
    public int getExplosionTimer() { return explosionTimer; }

    public static class Bullet {
        public int x, y;
        public boolean isPlayer;

        public Bullet(int x, int y, boolean isPlayer) {
            this.x = x;
            this.y = y;
            this.isPlayer = isPlayer;
        }
    }

    public static class Star {
        public int x, y, dy, size;

        public Star(int x, int y, int dy, int size) {
            this.x = x;
            this.y = y;
            this.dy = dy;
            this.size = size;
        }
    }
}