import java.util.*;
import javax.swing.*;
import java.awt.*;

public class GUI {
	// Valid game states
	private Vector<GameBoard> playedStates;
	
	// Components
	JFrame frame;
	JPanel gameBoard;
	JPanel[][] box;
	JLabel[][] labels;
	Font font;
	
	int gameRate = 500;
	
	GUI(EightPuzzle e) {
		playedStates = e.intermediaryStates;
		
		frame = new JFrame("Assingment 3 - 8-Puzzle w/ Simulated Annealing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		gameBoard = new JPanel(new GridLayout(3,3));
		box = new JPanel[3][3];
		labels = new JLabel[3][3];
		font = new Font("Arial", Font.PLAIN, 36);
		
		for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            	box[i][j] = new JPanel(new GridBagLayout());
                box[i][j].setBorder(BorderFactory.createLineBorder(Color.white));
                labels[i][j] = new JLabel();
                labels[i][j].setFont(font);
                
                box[i][j].add(labels[i][j]);
                gameBoard.add(box[i][j]);
            }
        }
		
		frame.add(gameBoard);
		frame.setSize(500,500);
		
		display();
		play();
	}
	
	public void display() {
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);
	}
	
	public void play() {
		for(GameBoard state : playedStates) {
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 3; j++) {
					
					labels[i][j].setText(state.board.getLabel(i * 3 + j).toString());
					if(state.board.getLabel(i * 3 +j) == 0) {
						labels[i][j].setText("");
					}
				}
			}
			
			try {
				Thread.sleep(gameRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		
	}
}
