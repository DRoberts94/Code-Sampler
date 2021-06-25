import java.util.*;

class EightPuzzle {
	
	GameBoard playBoard;		// A GameBoard for the initial playboard
	GameBoard goalBoard;		// A GameBoard for the goal state
	Vector<GameBoard> intermediaryStates; // A vector to keep track of all selected states
	double temperature;					// The temperature for simulated annealing
	int counter;						// A counter to keep track of how many times annealing has occured
	
	EightPuzzle(int[][] initialBoard, int[][]goalBoard) {
		this.playBoard = new GameBoard(initialBoard);
		this.goalBoard = new GameBoard(goalBoard);
		
		play();
	}
	
	// 
	void play() {
		temperature = 1000;
		counter = 0;
		intermediaryStates = new Vector<GameBoard>();
		analyze(playBoard);
	}

	void analyze(GameBoard currentState) {
		
		// Add this state to the vector of valid played states.
		intermediaryStates.add(currentState);
		
		// Return if the goal state has been reached.
		if(currentState.board.equals(goalBoard.board)) {
			return;
		}
		
		// Increase the counter an calculate the temperature.
		counter++;
		temperature = calculateTemp(counter);
		
		// If it has has annealed the maximum amount, start over.
		if((int)temperature == 0) {
			intermediaryStates = null;
			play();
			return;
		}
		
		
		// Create a list of all possible next moves.
		List<GameBoard> l = new ArrayList<GameBoard>();
		for (Integer it : currentState.neighbors()) {
			l.add(new GameBoard(createState(currentState, it)));
		}
				
		// Randomly select a new move.
		GameBoard nextState = l.get(new Random().nextInt(l.size()));
		
		// If the next move is a better than the current move, select that move.
		// Otherwise, probabilistically accept or reject the move.
		if (nextState.heuristic < currentState.heuristic) {
			analyze(nextState);
		} else {
			double delta = nextState.heuristic - currentState.heuristic;
			if (Math.exp(-delta/temperature) > new Random().nextDouble()) {
				analyze(nextState);
			} else analyze(currentState);
		}
	}
	
	// Intermediary function to create a new GameBoard given the next location of the blank spot.
	GameBoard createState(GameBoard intermediary, Integer location) {
		GameBoard newGB = new GameBoard(intermediary);
		
		newGB.board.setLabel(newGB.board.getLabel(location),newGB.board.vertexNumber(0));
		newGB.board.setLabel(0, location);
		calculateHeuristic(newGB);
		
		return newGB;
	}
	
	void calculateHeuristic(GameBoard gb) {	
		Integer h = 0;
		
		for (Integer i = 0; i < goalBoard.board.size(); i++) {
			if(goalBoard.board.getLabel(i) != gb.board.getLabel(i)) {
				h++;
			}
		}
		
		gb.heuristic = h;
	}
	
	double calculateTemp(int k) {
		if (k == 1) {
			return 1000;
		}
		
		return 0.9 * calculateTemp(k-1);
	}
	
	boolean isPlayedState(GameBoard gb) {
		for(GameBoard state : intermediaryStates) {
			if (state.equals(gb)) {
				return true;
			}
		}
		
		return false;
	}
	
}
