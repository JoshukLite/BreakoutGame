package breakout;

import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.JFrame;

public class Breakout extends JFrame {
	// Loads levels for "GameBoard" object
	private LevelLoader levelLoader = new LevelLoader();
	// Main menu board
	MenuBoard mainMenu;
	// Game board
	GameBoard gameBoard;
	public Breakout() {
		initUI();
	}
	private void initUI() {
		mainMenu = new MenuBoard();
		add(BorderLayout.CENTER, mainMenu);

		setResizable(false);
		pack();
		setTitle("Breakout");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	/*
	Removes main menu board and adds game board
	which takes LevelLoader object as argument
	to initialize game board
	*/
	void startGame() {
		remove(mainMenu);
		gameBoard = new GameBoard(levelLoader);
		add(gameBoard);
		// Make objects on board controllable
		gameBoard.requestFocus();
		validate();		
	}
	/*
	Removes current game board and adds new one
	with updated preferences for new level
	*/
	synchronized void nextLevel() {
		remove(gameBoard);
		gameBoard = new GameBoard(levelLoader);
		add(gameBoard);
		gameBoard.requestFocus();
		validate();
	}
	/*
	Removes current game board and adds new 
	main menu board
	*/
	synchronized void toMainMenu() {
		remove(gameBoard);
		mainMenu = new MenuBoard();
		add(mainMenu);
		validate();
	}
}