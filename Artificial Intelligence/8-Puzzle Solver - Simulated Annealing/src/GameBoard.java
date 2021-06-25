import java.util.*;

public class GameBoard implements Comparable<GameBoard>,Cloneable {
	public Graph<Integer> board; 	// This eight puzzle is implemented using a graph
	private final Integer dim = 3;	// Dimensions of the eight puzzle
	public Integer heuristic;		// Heuristic is how many numbers are out of place from their placement in the goal state	
	
	// Constructor
	GameBoard(int[][] values) {
		Integer num = 0;
		
		board = new Graph<Integer>();
		heuristic = 0;
		
		for(Integer i = 0; i < dim; i++) {
			for (Integer j = 0; j < dim; j++) {
				board.addVertex(values[i][j]);
			}
		}
		
		for (Integer i = 0; i < dim; i++) {
			for (Integer j = 0; j < dim; j++) {
				num = i * dim + j;
				
				//North
				if (i > 0) {
					board.addEdge(num, num - dim);
				}
				
				// South
				if (i < dim - 1) {
					board.addEdge(num, num + dim);
				}
				
				// East
				if (j < dim - 1) {
					board.addEdge(num, num + 1);
				}
				
				// West
				if (j > 0) {
					board.addEdge(num, num - 1);
				}
			}
		}
	}
	
	// Returns a set of available places the blank spot can move.
		Set<Integer> neighbors() {
			return board.neighbors(board.vertexNumber(0));
		}
	
	// Copy constructor
	GameBoard(GameBoard gb) {
		this.board = new Graph<Integer>(gb.board);
		this.heuristic = new Integer(gb.heuristic);
	}
	
	public String toString() {
		Integer num = 0;
		String str = new String();
		
		for (Integer i = 0; i < dim; i++) {
			for(Integer j = 0; j < dim; j++) {
				num = i * dim +j;
				
				str = str + this.board.getLabel(num) + " ";
			}
			
			str = str + "\n";
		}
		
		str = str + "h(n) = " + heuristic + "\n";
		
		return str;
	}

	public int compareTo(GameBoard gb) {
		return this.heuristic - gb.heuristic;
	}
}
