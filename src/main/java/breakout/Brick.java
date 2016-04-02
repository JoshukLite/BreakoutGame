package breakout;

import java.net.URL;
import javax.swing.ImageIcon;
 
public class Brick extends Sprite {
	// State of the brick
	private boolean destroyed;
	public Brick(int x, int y, String type) {
		// Initialize position of brick
		this.x = x;
		this.y = y;
		// Load and set image for brick
		loadImage(type);
		i_width = image.getWidth(null);
		i_height = image.getHeight(null);
		destroyed = false;
	}
	public boolean isDestroyed() {
		return destroyed;
	}
	public void setDestroyed(boolean val) {
		destroyed = val;
	}
	private void loadImage(String typeNumber) {
		// url for brick image
		URL url = getClass().getResource(
				"/resources/brick" + typeNumber + ".png");
		ImageIcon ii;
		if(url != null) {
			// If url is valid, load this image for brick
			ii = new ImageIcon(url);
		}	else {
			// If url is invalid, laod default image for brick
			ii = new ImageIcon(getClass()
					.getResource("/resources/brick00.png"));
		}
		image = ii.getImage();
	}
}