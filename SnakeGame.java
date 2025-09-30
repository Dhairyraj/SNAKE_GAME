import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class SnakeGame extends Frame {
    String str = "S"; 
    Random rand = new Random();
    int ax = ThreadLocalRandom.current().nextInt(100, 400);
    int ay = ThreadLocalRandom.current().nextInt(100, 400);
    
    List<Point> snakeBody = new ArrayList<>();
    int initialX = 300; 
    int initialY = 300;

    boolean GameOver = false;
    int dx = 10, dy = 0;
    int score = 0;

    final int INITIAL_SPEED_DELAY = 150;
    int currentSpeedDelay = INITIAL_SPEED_DELAY;
    Timer gameTimer;
    
    // Flag to control growth in the next movement cycle
    boolean shouldGrow = false;

    SnakeGame() {
        setSize(700, 700);
        setVisible(true);

        snakeBody.add(new Point(initialX, initialY));
        snakeBody.add(new Point(initialX - 10, initialY));
        snakeBody.add(new Point(initialX - 20, initialY));

        startTimer();

        addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (GameOver) return;
                int key = e.getKeyCode();
                
                if (key == KeyEvent.VK_UP && dy != 10) {
                    dx = 0; dy = -10;
                } else if (key == KeyEvent.VK_DOWN && dy != -10) {
                    dx = 0; dy = 10;
                } else if (key == KeyEvent.VK_LEFT && dx != 10) {
                    dx = -10; dy = 0;
                } else if (key == KeyEvent.VK_RIGHT && dx != -10) {
                    dx = 10; dy = 0;
                }
            }
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (gameTimer != null) gameTimer.cancel();
                dispose();
            }
        });
    }

    private void startTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask() {
            public void run() {
                if (!GameOver) {
                    touch(); 
                    moveSnake();

                    Point head = snakeBody.get(0);
                    if (head.x < 0 || head.x > getWidth() - 10 || head.y < 40 || head.y > getHeight() - 40) {
                        GameOver = true;
                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                restart();
                            }
                        }, 2000);
                    }
                    if (checkSelfCollision()) {
                        GameOver = true;
                        new Timer().schedule(new TimerTask() {
                            public void run() {
                                restart();
                            }
                        }, 2000);
                    }

                    repaint();
                }
            }
        }, 0, currentSpeedDelay);
    }

    private void moveSnake() {
        Point head = snakeBody.get(0);
        Point newHead = new Point(head.x + dx, head.y + dy);
        
        snakeBody.add(0, newHead);
        
        // --- START: GROWTH LOGIC ---
        if (!shouldGrow) {
            // Remove the tail only if growth is NOT required
            snakeBody.remove(snakeBody.size() - 1);
        } else {
            // Reset the flag for the next cycle
            shouldGrow = false;
        }
        // --- END: GROWTH LOGIC ---
    }
    
    private boolean checkSelfCollision() {
        Point head = snakeBody.get(0);
        for (int i = 1; i < snakeBody.size(); i++) {
            if (head.equals(snakeBody.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void paint(Graphics g) {
        if (!GameOver) {
            g.setColor(Color.RED);
            g.fillOval(ax, ay, 10, 10);

            for (int i = 0; i < snakeBody.size(); i++) {
                Point p = snakeBody.get(i);
                if (i == 0) {
                    g.setColor(Color.BLUE);
                } else {
                    g.setColor(Color.GREEN);
                }
                g.fillRect(p.x, p.y, 10, 10);
            }

            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(Color.BLACK);
            g.drawString("Score: " + score, 20, 60);

        } else {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", getWidth() / 2 - 80, getHeight() / 2 - 20);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.BLACK);
            g.drawString("Final Score: " + score, getWidth() / 2 - 80, getHeight() / 2 + 20);
            System.out.print("Game Over");
        }
    }

    public void restart() {
        snakeBody.clear();
        snakeBody.add(new Point(initialX, initialY));
        snakeBody.add(new Point(initialX - 10, initialY));
        snakeBody.add(new Point(initialX - 20, initialY));
        
        dx = 10;
        dy = 0;
        str = "S"; 
        
        ax = ThreadLocalRandom.current().nextInt(100, 400);
        ay = ThreadLocalRandom.current().nextInt(100, 400);
        score = 0;
        GameOver = false;
        shouldGrow = false;

        currentSpeedDelay = INITIAL_SPEED_DELAY;
        startTimer();
        repaint();
    }

    public void touch() {
        Point head = snakeBody.get(0);
        
        if (Math.abs(head.x - ax) < 10 && Math.abs(head.y - ay) < 10) {
            score++;
            shouldGrow = true; // Set the flag to true to prevent tail removal in moveSnake()

            if (score % 5 == 0 && currentSpeedDelay > 50) {
                currentSpeedDelay -= 10;
                startTimer();
            }

            ax = ThreadLocalRandom.current().nextInt(20, getWidth() - 30);
            ay = ThreadLocalRandom.current().nextInt(50, getHeight() - 30);

            for (Point p : snakeBody) {
                if (Math.abs(p.x - ax) < 10 && Math.abs(p.y - ay) < 10) {
                    touch(); 
                    return; 
                }
            }
        }
    }

    public static void main(String[] args) {
        new SnakeGame();
    }
}
// List stores all segments (Points), replacing single (sx, sy) for a true body structure.

// Variables for Speed/Difficulty Increase: Timer delay decreases (speeds up) as score increases.

// True Snake Movement: Adds new head, removes tail. If shouldGrow is true (food eaten), tail removal is skipped.

// Collision Checks: Includes check for boundary hit and self-collision (head vs body segments).