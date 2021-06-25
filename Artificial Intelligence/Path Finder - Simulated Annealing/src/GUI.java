import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI {
	
	// Gameplay variables
	static GameBoard game;
	final int emptyInt;
	final int obstacleInt;
	final int gameBoardSize;

	// GUI Components
	JFrame window = new JFrame("Assignment 3 - Path Finding w/ Simulated Annealing");
	JPanel frame = new JPanel();
	JPanel[][] box;
	JPanel pPanel = new JPanel();
	final int gameRate = 100;
	final int windowSize = 750;
	
	// Colors
	Color emptyClr = Color.black;
	Color obstacleClr = Color.gray;
	Color currentClr = Color.blue;
	Color visitedClr = Color.yellow;
	Color startClr = Color.green;
	Color endClr = Color.red;

	GUI(GameBoard gb) {
		game = gb;
		gameBoardSize = gb.gameBoardSize;
		emptyInt = game.EMPTY;
		obstacleInt = game.OBSTACLE;
		
		setup();
		display();
		run();
	}
	
	void setup() {
		box = new JPanel[game.gameBoardSize][game.gameBoardSize];
		
		frame.setLayout(new GridLayout(game.gameBoardSize,game.gameBoardSize));
		
		for (int x = 0; x < game.gameBoardSize; x++) {
            for (int y = 0; y < game.gameBoardSize; y++) {
            	box[x][y] = new JPanel(new GridBagLayout());
                box[x][y].setBorder(BorderFactory.createLineBorder(Color.white));
                frame.add(box[x][y]);
            }
        }
		
		box[game.startLocation.x][game.startLocation.y].add(pPanel);

		window.add(frame);
		window.setSize(windowSize, windowSize);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void display() {		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		window.setResizable(false);
	}
	
	void run() {
		// Continuously generates new games until close.
		do { 
			game = new GameBoard();
			game.play();
			colorize(true);
			colorize(false);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while(true);
	}
	
	public void colorize(boolean object) {
		// If true, colorize board. If false, colorize path.
		if(object) {
			pPanel.setBackground(currentClr);
			
			for (int x = 0; x < game.gameBoardSize; x++) {
                for (int y = 0; y < game.gameBoardSize; y++) {
                	if(game.board[x][y] == obstacleInt) {
                    		box[x][y].setBackground(obstacleClr);
                	} else box[x][y].setBackground(emptyClr);
                }
            }
    		
    		box[game.startLocation.x][game.startLocation.y].setBackground(startClr);
    		box[game.endLocation.x][game.endLocation.y].setBackground(endClr);
		} else {
			for (Point point : game.p.path) {
				try {
					if((!point.equals(game.startLocation) & !point.equals(game.endLocation))) {
    					box[point.x][point.y].setBackground(visitedClr); 
    				}
    				
    				box[point.x][point.y].add(pPanel);
            		window.repaint();
            		
					Thread.sleep(gameRate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
		}
	}
}
