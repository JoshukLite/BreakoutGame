package breakout;

import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Paddle extends Sprite
implements Commons {
	// Paddle moving direction
	private int dx;
	// State for buttons
	private boolean leftPressed = false;
	private boolean rightPressed = false;
	public Paddle() {
		// Load and set image for paddle
		ImageIcon ii = new ImageIcon(
				getClass().getResource(("/resources/paddle.png")));
		image = ii.getImage();
		i_width = image.getWidth(null);
		i_height = image.getHeight(null);
		resetState();
	}
	public void move() {
		if(leftPressed) {
			dx = -1;			/* Change moving direction to left */
		}
		if(rightPressed) {
			dx = 1;				/* Change moving direction to right */
		}
		// Change x position of paddle
		x += dx;
		if(x <= 0) {
			x = 0;						/* Stop paddle when it get left edge */
		}
		if(x >= WIDTH - i_width) {
			x = WIDTH - i_width;		/* Stop paddle when it get right edge */
		}
		/*
		Reset paddle direction, if no 
		button is pressed, than paddle stop
		*/
		dx = 0;
	}
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_LEFT) {
			leftPressed = true;
		}
		if(key == KeyEvent.VK_RIGHT) {
			rightPressed = true;
		}
	}
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_LEFT) {
			leftPressed = false;
		}
		if(key == KeyEvent.VK_RIGHT) {
			rightPressed = false;
		}
	}
	// Set initial position of paddle
	private void resetState() {
		x = INIT_PADDLE_X;
		y = INIT_PADDLE_Y;
	}
}