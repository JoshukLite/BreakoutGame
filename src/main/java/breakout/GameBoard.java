package breakout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
This class is responsible for representing game graphics
and animation. Each "GameBoard" loads one level
*/
public class GameBoard extends JPanel
implements Commons {
	// Util Timer for creating animation
	private Timer timer;
	// Message to display when current level ends
	private String message = "Game Over";
	// Objects to draw
	private Ball ball;
	private Brick[] bricks;
	private Paddle paddle;
	// Number of bricks to destroy
	private int N_OF_BRICKS;
	// Game state
	private boolean ingame = true;
	// Pause state
	private boolean paused = false;
	private Random rand = new Random();
	/* 
	level loader object to load current level 
	and check if there anouther levels left
	*/
	private LevelLoader levelLoader;
	// Container to store level data
	private HashMap<String, String> levelData;
	private ExecutorService exec = Executors.newSingleThreadExecutor();
	// Initialize "LevelLoader" object and load current level data:
	public GameBoard(LevelLoader ll) {
		levelLoader = ll;
		levelData = ll.nextLevel();
		initBoard();
	}
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(Commons.WIDTH, Commons.HEIGHT);
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Commons.WIDTH, Commons.HEIGHT);
	}
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Commons.WIDTH, Commons.HEIGHT);
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		// Set rendering for quality
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
		// Turn on anti-aliasing 
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		if(ingame) {
			drawObjects(g2d);		/* Continue to draw objects */
		}	else {
			levelEnd(g2d);			/* Draw level ending title */
		}
		Toolkit.getDefaultToolkit().sync();
	}
	private void initBoard() {
		// Initializes number of bricks to destroy from "levelData"
		N_OF_BRICKS = Integer.parseInt(levelData.get("nofbricks"));
		// Add "KeyAdapter" to controll "paddle"
		addKeyListener(new Adapter());
		// Allows to focus this panel
		setFocusable(true);

		bricks = new Brick[N_OF_BRICKS];
		setDoubleBuffered(true);
		timer = new Timer();
		// Schedules the specified task for repeated fixed-rate execution
		timer.scheduleAtFixedRate(new ScheduleTask(), DELAY, PERIOD);
		setBackground(Color.WHITE);
		gameInit();
	}
	private void gameInit() {
		ball = new Ball();
		paddle = new Paddle();
		/*
		Pattern to get valid x and y position of each brick
		from xml document
		*/
		Pattern pattern = Pattern.compile("\\s*?(\\d+)\\s*?(\\d+)\\s*?(\\d+)");
		Matcher matcher = pattern.matcher(levelData.get("brickspos"));
		int k = 0;
		while(matcher.find()) {
			/*
			Initialize each "Brick" object with coordinates and type from xml document
			group(1) == x position, group(2) == y position, group(3) == type of brick
			*/
			bricks[k++] = new Brick(Integer.parseInt(matcher.group(1)), 
					Integer.parseInt(matcher.group(2)),
					matcher.group(3));
		}
	}
	// Draws all objects on the panel:
	private void drawObjects(Graphics2D g2d) {
		// Draw "Ball" object on the board:
		g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(),
			ball.getWidth(), ball.getHeight(), this);
		// Draw "Paddle" object on the board:
		g2d.drawImage(paddle.getImage(), paddle.getX(), paddle.getY(),
			paddle.getWidth(), paddle.getHeight(), this);
		// Draw every brick on the board if it not destroyed:
		for(int i = 0; i < N_OF_BRICKS; i++) {
			if(!bricks[i].isDestroyed()) {
				Brick b = bricks[i];
				g2d.drawImage(b.getImage(), b.getX(), b.getY(),
					b.getWidth(), b.getHeight(), this);
			}
		}
	}
	// Draw level ending title:
	private void levelEnd(Graphics2D g2d) {
		// Font for text
		Font font = new Font("Tv Cen Mt Condensed", Font.BOLD, 24);
		// Determines font metrics of string to be draw
		FontMetrics metr = this.getFontMetrics(font);
		g2d.setColor(Color.BLACK);
		g2d.setFont(font);
		// Draw message in the middle of the panel:
		g2d.drawString(message, 
			(Commons.WIDTH - metr.stringWidth(message)) / 2,
			Commons.WIDTH / 2);
	}
	// Method to pause and contunie game:
	private void pauseGame() {
		if(!paused) {
			/* 
			Changes "pause" state and cancel 
			"timer" to stop drawing objects on board
			*/
			paused = true;
			timer.cancel();
		}	else {
			/*
			Changes "pause" state and creates new "Timer" object
			with new "ScheduleTask" object with zero DELAY to
			continue drawing objects on panel
			*/
			paused = false;
			timer = new Timer();
			timer.scheduleAtFixedRate(new ScheduleTask(), 0, PERIOD);
		}
	}
	// Method to call next level:
	private void onNextLevel(final int delay) {
		ingame = false;
		// Get reference of upcasted JFrame on whitch it's located:
		Breakout topFrame = (Breakout)SwingUtilities.getWindowAncestor(this);
		// Stops drawing:
		timer.cancel();
		/*
		Start new thread that will call method from "Breakout" object
		to load next level with some delay. Shows end game title
		in the delay time
		*/
		exec.execute(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(delay);
					}	catch(InterruptedException e) {
						throw new RuntimeException(e);
					}
					// Load next level
					topFrame.nextLevel();
				}
		});
	}
	// Method to call main menu:
	private void mainBoard(final int delay) {
		ingame = false;
		// Get reference of upcasted JFrame on whitch it's located:
		Breakout topFrame = (Breakout)SwingUtilities.getWindowAncestor(this);
		// Stops drawing:
		timer.cancel();
		// Reset level number to zero
		levelLoader.resetLevels();
		/*
		Start new thread that will call method from "Breakout" object
		to load mani board with some delay. Shows end game title
		in the delay time
		*/
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(delay);
				}	catch(InterruptedException e) {

				}
				// Load main board
				topFrame.toMainMenu();
			}
		});
	}
	// Method to check ball collision with paddle and bricks:
	private void checkCollision() {
		if(ball.getRect().getMaxY() > Commons.BOTTOM_EDGE) {
			// Call "mainBoard()" of ball gets bottom of the panel
			mainBoard(2);
		}
		// Checks if all bricks are destroyed:
		for(int i = 0, j = 0; i < N_OF_BRICKS; i++) {
			// j == number of destroyed bricks
			if(bricks[i].isDestroyed()) {
				j++;
			}
			if((j == N_OF_BRICKS) && (levelLoader.hasNextLevel())) {
				// If there is one more level, loads next level
				message = "VICTORY";
				onNextLevel(2);
			}	else if((j == N_OF_BRICKS) && !(levelLoader.hasNextLevel())) {
				// If there is't one more level, loads main board
				message = "YOU WIN";
				mainBoard(2);
			}
		}
		// Checks collision between ball and paddle
		if((ball.getRect()).intersects(paddle.getRect())) {
			int paddlePos = (int)paddle.getRect().getMinX();
			int ballPos = (int)ball.getRect().getMinX();

			// Splits paddle on seven sections, each of this sections
			// repell ball in different sides(with different angles)
			int[] sections = new int[7];
			for(int x = 1; x <= sections.length; x++) {
				sections[x - 1] = paddle.getWidth() / 7 * x;
			}
			for(int x = 0; x < sections.length; x++) {
				if((ballPos < paddlePos + sections[3]) 
						&& (ballPos > paddlePos + sections[2])) {
				   	// Repell ball directly to north
					ball.setRad(1.571);
					break;
				}	else if(ballPos < paddlePos + sections[x]) {
					// Calculate angle degree depending on section number
					ball.setRad(Math.PI / 8 * (8 - (x + 1)));
					break;
				}	else if(ballPos >= paddlePos + sections[x]) {
					// If ball hits paddle in right angle it will be
					// repelled with minimal degree
					ball.setRad(Math.PI / 8);
				}
			}
		}
		// Checks collision between ball and survived bricks
		for(int i = 0; i < N_OF_BRICKS; i++) {
			if((!bricks[i].isDestroyed()) 
					&& (ball.getRect()).intersects(bricks[i].getRect())) {
				// Left ball position on board
				int ballLeft = (int)ball.getRect().getMinX();
				// Right ball position on board
				int ballRight = (int)ball.getRect().getMaxX();
				// Top ball position on board
				int ballTop = (int)ball.getRect().getMinY();
				// Bottom ball position on board
				int ballBottom = (int)ball.getRect().getMaxY();
				// Points of ball to properly repel it from brick:
				Point 
					upLeft = new Point(ballLeft + 1, ballTop),
					upRight = new Point(ballRight - 2, ballTop),
					downLeft = new Point(ballLeft + 1, ballBottom),
					downRight = new Point(ballRight - 2, ballBottom),
					leftUp = new Point(ballLeft, ballTop + 1),
					leftDown = new Point(ballLeft, ballBottom - 2),
					rightUp = new Point(ballRight, ballTop + 1),
					rightDown = new Point(ballRight, ballBottom - 2),
					leftUpAngle = new Point(ballLeft, ballTop),
					leftDownAngle = new Point(ballLeft, ballBottom),
					rightUpAngle = new Point(ballRight, ballTop),
					rightDownAngle = new Point(ballRight, ballBottom);
				Rectangle brick = bricks[i].getRect();
				if(brick.contains(upLeft) || brick.contains(upRight)) {
					// Ball hits bottom side of the brick: 
					if(ball.getRad() == 1.571) {
						/*
						Ball hits brick under 90 degree repel 
						ball in random direction under 45 degree
						*/
						switch(rand.nextInt(2)) {
							case 0 : ball.setRad(Math.PI + Math.PI * 0.25);		/* West direction */
										break;
							case 1 : ball.setRad(Math.PI + Math.PI * 0.75);		/* East direction */
										break;
						}
					}	else {
						ball.changeDirectionFrom(Ball.Direction.UP);
					}
				}	else if(brick.contains(downLeft) || brick.contains(downRight)) {
					// Ball hits top side of the brick
					ball.changeDirectionFrom(Ball.Direction.DOWN);
				}	else if(brick.contains(leftUp) || brick.contains(leftDown)) {
					// Ball hits right side of the brick
					ball.changeDirectionFrom(Ball.Direction.LEFT);
				}	else if(brick.contains(rightUp) || brick.contains(rightDown)) {
					// Ball hits left side of the brick
					ball.changeDirectionFrom(Ball.Direction.RIGHT);
				}	else if(brick.contains(leftUpAngle)) {
					// Ball hits right down angle of the brick
					ball.setRad(Math.PI / 4);
				}	else if(brick.contains(leftDownAngle)) {
					// Ball hits right up angle of the brick
					ball.setRad(Math.PI * 7 / 4);
				}	else if(brick.contains(rightUpAngle)) {
					// Ball hits left down angle of the brick
					ball.setRad(Math.PI * 5 / 4);
				}	else if(brick.contains(rightDownAngle)) {
					// Ball hits left up angle of the brick
					ball.setRad(Math.PI * 3 / 4);
				}
				bricks[i].setDestroyed(true);
			}
		}
	}
	private class Adapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				pauseGame();
			}
			paddle.keyPressed(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			paddle.keyReleased(e);
		}
	}
	/*
	Class to move ball and paddle objects on the panel,
	check collision between them and repaint it all
	*/
	private class ScheduleTask extends TimerTask {
		@Override
		public void run() {
			ball.move();
			paddle.move();
			checkCollision();
			repaint();
		}
	}
}