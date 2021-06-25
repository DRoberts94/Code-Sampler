import java.awt.*;
import java.util.*;
import java.util.List;

public class GameBoard {
	
	// Gameplay variables
	Player p;
	boolean endGame = false;
	Point startLocation;
	Point endLocation;
	final int gameRate = 100;
	 
	// Board variables
	final int gameBoardSize = 10;
	int[][] board;		// Array that holds integer variables based on status.
	final int START = -4;
	final int END = 0;
	final int OBSTACLE = -2;
	final int VISITED = -1;
	final int EMPTY = 0;
	final int CURRENT = 1;
	
	// Constructor
	GameBoard() {
		
		// Create the board
		board = new int[gameBoardSize][gameBoardSize];
		
		// Determine random start and end points
		startLocation = new Point(new Random().nextInt(gameBoardSize - 1) + 1, 0);
		endLocation = new Point(new Random().nextInt(gameBoardSize), gameBoardSize - 1);
		
		// Randomly place obstacles
		double attritionRate = 0.15;
		int numObstacles = (int) (attritionRate * gameBoardSize * gameBoardSize);
		Point obLocation = new Point();
		for( int i = 0; i < numObstacles; i++) {
			do {
    			obLocation.x = new Random().nextInt(gameBoardSize - 1) + 1;
    			obLocation.y = new Random().nextInt(gameBoardSize - 1) + 1;
			} while ((obLocation == startLocation) || (obLocation == endLocation));
			
			board[obLocation.x][obLocation.y] = OBSTACLE;
		}
		
		// Set start and end points
		board[startLocation.x][startLocation.y] = START;
		board[endLocation.x][endLocation.y] = END;
		
		// Enter player
		p = new Player();
	}
	
	void play() {
		p.analyze(p.path.get(0));
		setPath();
	}
	
	// Returns the board status at a point on the board.
	int getStatus(Point point) {
		return board[point.x][point.y];
	}
	
	// Sets the board status at a point on the board.
	void setStatus(Point point, int status) {
		board[point.x][point.y]= status; 
	}
	
	// Sets the path the player has taken.
	void setPath() {
		for(Point ln : p.path) {
			board[ln.x][ln.y] = VISITED;
		}
	}
	
	public class Player {
		ArrayList<Point> path;		// Keeps track of the player's chosen moves
		
		// Random variables
		int rInt;
		double rDouble;
		
		// Annealing variables
		double delta;
		double temperature;
		int counter;
		
		Player() {
			setupNewGame();
			path.add(startLocation);
		}
		
		void setupNewGame() {
			path = null;
			temperature = 100;
			counter = 0;
			path = new ArrayList<Point>();
		}
		
		void analyze(Point currentLocation) {
			
			// Add this point to the vector of visited points.
			path.add(currentLocation);
			setStatus(currentLocation, VISITED);
			
			// Return if the player has reached the end goal.			
			if(currentLocation.equals(endLocation)) {
				endGame = true;
				return;
			}
			
			counter++;
			temperature = calculateTemp(counter);
			
			// If the annealing has maxed out, end analysis.
			if ((int) temperature == 0) {
	 			return;
	 		}
			
			// Create neighbors to path's last location
			List<Location> neighbors  = new ArrayList<Location>();
			
			Location north = new Location(new Point(currentLocation.x-1,currentLocation.y));
			Location south = new Location(new Point(currentLocation.x+1,currentLocation.y));
			Location east = new Location(new Point(currentLocation.x,currentLocation.y+1));
			Location west = new Location(new Point(currentLocation.x,currentLocation.y-1));
			Location[] l = new Location[] {north, east, south, west};
			
			// Add all valid neighbors (no out-of-bound point values)
			for(int i = 0; i < 4; i++) {
				if(l[i].point.x >= 0 && l[i].point.x < gameBoardSize) {
					if(l[i].point.y >=0 && l[i].point.y < gameBoardSize) {
						if(!isPlayedState(l[i])) {
							neighbors.add(l[i]);
						}
					}
				}
			}
			
			// If there are neighbors, continue analysis. Otherwise, end the sequence.
			if(!neighbors.isEmpty()) {
				
				// Randomly select a neighbor.
				Location nextLocation = null;
								
				do {
					rInt = new Random().nextInt(neighbors.size());
					nextLocation = neighbors.get(rInt);
				} while(isPlayedState(nextLocation));
				
				// If the next location is better than the current location, select that location.
				// Otherwise, probabilistically accept or reject the location.
				Location currentState = new Location(currentLocation);
				
				if (nextLocation.h < currentState.h) {
					analyze(nextLocation.point);
				} else {
					delta = nextLocation.h - currentState.h;
					rDouble = new Random().nextDouble();
					if (Math.exp(-delta/temperature) > rDouble) {
						analyze(nextLocation.point);
					} else {
						analyze(currentState.point);
					}
				}
			}
		}
		
		private boolean isPlayedState(Location nextLocation) {			
			for(Point state : path) {
				if(state.equals(nextLocation.point)) {
					return true;
				}
			}
			
			return false;
		}
		
		double calculateTemp(int k) {
			if (k == 1) {
				return 1000;
			} else if (k <= 0) return 0;
			
			return 0.9 * calculateTemp(k-1);
		}

		class Location implements Comparable<Location>{
			Point point;
			double h;		// heuristic
			
			Location(Point p) {
				point = new Point();
				point.x = p.x;
				point.y = p.y;
				h = Math.sqrt(Math.pow(endLocation.x-point.x, 2) + Math.pow(endLocation.y-point.y,  2));
			}
			
			public int compareTo(Location l) {
				if (this.h < l.h) return -1;
				else if (this.h == l.h) return 0;
				else if (this.h > l.h) return 1;
				return 0;
			}
			
			public String toString() {
				return point.toString() + "; h: " + h;
			}
		};
	};
}
