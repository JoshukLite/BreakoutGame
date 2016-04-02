package breakout;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.GridLayout;

class MenuBoard extends JPanel {
	private JButton startGame = new JButton("Start Game");
	private JButton exitGame = new JButton("Exit");
	private Image image;
	public MenuBoard() {
		initMenu();
	}
	private void initMenu() {
		// Load image for background
		loadImage();
		// Add listeners to buttons
		startGame.addActionListener(new StartGameListener());
		exitGame.addActionListener(new ExitGameListener());
		// Setting buttons in the middle of the screen
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(startGame, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		// Resize "exitGame" button to "startGame" button size
		exitGame.setPreferredSize(startGame.getPreferredSize());
		// Set insets between two buttons in 100 px
		gbc.insets = new Insets(100, 0, 0, 0);
		add(exitGame, gbc);
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
		g.drawImage(image, 0, 0, null);

	}
	private void loadImage() {
		ImageIcon ii = new ImageIcon(
				getClass().getResource("/resources/main_menu.png"));
		image = ii.getImage();
	}
	private class StartGameListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Call "startGame()" from main JFrame to load GameBoard object
			((Breakout)SwingUtilities.getWindowAncestor(MenuBoard.this)).startGame();
		}
	}
	private class ExitGameListener implements ActionListener {
		@Override
		// Exit game
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
}