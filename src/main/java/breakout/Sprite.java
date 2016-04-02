package breakout;

import java.awt.Image;
import java.awt.Rectangle;

/*
Class Sprite provides base for all objects
that will be drawn on the GameBoard
*/
public class Sprite {
	// X position of the Sprite
	protected double x;
	// Y position of the Sprite
	protected double y;
	// Width of Sprite image
	protected int i_width;
	// Height of Sprite image
	protected int i_height;
	// Image that represents Sprite
	protected Image image;
	public void setX(int x) {
		this.x = x;
	}
	public int getX() {
		// Downcast double to properly draw it 
		// on board
		return (int)x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getY() {
		// Downcast double to properly draw it 
		// on board
		return (int)y;
	}
	public int getWidth() {
		return i_width;
	}
	public int getHeight() {
		return i_height;
	}
	Image getImage() {
		return image;
	}
	Rectangle getRect() {
		return new Rectangle((int)x, (int)y, 
			image.getWidth(null), image.getHeight(null));
	}
}