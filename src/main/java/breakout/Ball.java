package breakout;

import javax.swing.ImageIcon;

public class Ball extends Sprite 
implements Commons {
	// X direction of ball
	private double xdir;
	// Y direction of ball
	private double ydir;
	// Initial movement set to 57 degrees
	private double rad = 0.995;
	private double halfPI = Math.PI / 2;
	public static enum Direction {
		RIGHT, LEFT, UP, DOWN;
	}
	public Ball() {
		// Set ball direction to 57 degrees
		xdir = Math.cos(rad);
		ydir = Math.sin(rad);
		// Load and set image for ball
		ImageIcon ii = new ImageIcon(
				getClass().getResource(("/resources/ball.png")));
		image = ii.getImage();
		i_width = image.getWidth(null);
		i_height = image.getHeight(null);
		resetState();
	}
	public void move() {
		// Change position of ball
		x += xdir;
		y -= ydir;
		if(x > WIDTH - i_width - 1) {
			// Ball hits right edge of panel
			changeDirectionFrom(Direction.RIGHT);
		}
		if(y < 1) {
			// Ball hits upper edge of panel
			changeDirectionFrom(Direction.UP);
		}
		if(x < 1) {
			// Ball hits left edge of panel
			changeDirectionFrom(Direction.LEFT);
		}
		if(y > HEIGHT - i_height - 1) {
			// Ball hits bottom edge of panel
			changeDirectionFrom(Direction.DOWN);
		}
	}
	/*
	This method is made to repel ball from objects
	or edges of panel in right angle (like 'mirror angle'),
	'direction' argument is ball moving direction (e.g. ball moves
	up then 'direction' argument will be 'UP')
	*/
	public void changeDirectionFrom(Direction direction) {
		switch(direction) {
			case RIGHT : 
				if((rad > 0) && (rad < halfPI)) {
					// If ball moves to north-east it will be repelled to north-west
					rad += (halfPI - rad) * 2;
				}	else if((rad > Math.PI + halfPI) && (rad < Math.PI * 2)) {
					// If ball moves to south east it will be repelled to south west
					rad -= (rad - Math.PI - halfPI) * 2;
				}	else if((rad == 0) || (rad == Math.PI * 2)) {
					// If ball moves directly to east it will be repelled to west
					rad = Math.PI;
				}
				break;
			case UP :
				if((rad > halfPI) && (rad < Math.PI)) { 
					// If ball moves to north west it will be repelled to south west
					rad += (Math.PI - rad) * 2;
				}	else if((rad > 0) && (rad < halfPI)) {
					// If ball moves to north east it will be repelled to south east
					rad = Math.PI * 2 - rad;
				}	else if(rad == halfPI) {
					// If ball moves directly to north it will be repelled to south
					rad = Math.PI + halfPI;
				}
				break;
			case LEFT :
				if((rad > Math.PI) && (rad < Math.PI + halfPI)) { 
					// If ball moves to south west it will be repelled south east
					rad += (Math.PI + halfPI - rad) * 2;
				}	else if((rad > halfPI) && (rad < Math.PI)) {
					// If ball moves to north west it will be repelled north east
					rad -= (rad - halfPI) * 2;
				}	else if(rad == Math.PI) {
					// If ball moves directly to west it will be repelled to east
					rad = 0;
				}
				break;
			case DOWN :
				if((rad > Math.PI + halfPI) && (rad < Math.PI * 2)) {
					// If ball moves to south east it will be repelled to north east
					rad = Math.PI * 2 - rad;
				}	else if((rad > Math.PI) && rad < (Math.PI + halfPI)) {
					// If ball moves to south west it will be repelled to north west
					rad -= (rad - Math.PI) * 2;
				}	else if(rad == Math.PI + halfPI) {
					// If ball moves directly to south it will be repelled to north
					rad = halfPI;
				}
				break;
		}
		assert rad < Math.PI * 2 + 1;
		// Set ball direction to calculated degree
		xdir = Math.cos(rad);
		ydir = Math.sin(rad);
	}
	// Set direction for ball to move on in radians
	public void setRad(double rad) {
		this.rad = rad;
		xdir = Math.cos(rad);
		ydir = Math.sin(rad);
	}
	// Get current direction in radians
	public double getRad() {
		return rad;
	}
	// Set initial position of ball
	private void resetState() {
		x = INIT_BALL_X;
		y = INIT_BALL_Y;
	}
}