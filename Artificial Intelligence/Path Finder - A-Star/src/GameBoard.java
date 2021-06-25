import java.awt.*;
import java.util.*;

public class GameBoard {
    	
	// Gameplay variables
	Player p;
	boolean endGame = false;
	Point startLocation;
	Point endLocation;
	final int gameRate = 100;
	
	// Board variables
	final int gameBoardSize = 20;
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
		startLocation = new Point(new Random().nextInt(gameBoardSize), 0);
		endLocation = new Point(new Random().nextInt(gameBoardSize), gameBoardSize - 1);
		
		// Randomly place obstacles
		double attritionRate = 0.15;
		int numObstacles = (int) (attritionRate * Math.pow(gameBoardSize, 2));
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
		
		ArrayList<Point> path; // Keeps track of the player's chosen moves
		
		Player() {
			path = new ArrayList<Point>();
			path.add(startLocation);
		}
		
		void analyze(Point currentLocation) {
	 		
			// Add this point to the vector of visited points, and change its status to visited.
			path.add(currentLocation);
			setStatus(currentLocation, VISITED);
			
			// Return if the player has reached the end goal.
			if(currentLocation.equals(endLocation)) {
				endGame = true;
				return;
	 		}
		
			PriorityQueue<Location> temp  = new PriorityQueue<Location>();
			
			// Create neighbors to path's last location
			Location north = new Location(new Point(currentLocation.x-1,currentLocation.y));
			Location south = new Location(new Point(currentLocation.x+1,currentLocation.y));
			Location east = new Location(new Point(currentLocation.x,currentLocation.y+1));
			Location west = new Location(new Point(currentLocation.x,currentLocation.y-1)); 
			Location[] l = new Location[] {north, east, south, west};
			
			// Add all valid neighbors (no out-of-bound point values)
			for(int i = 0; i < 4; i++) {
				if(l[i].point.x >= 0 && l[i].point.x < gameBoardSize) {
					if(l[i].point.y >=0 && l[i].point.y < gameBoardSize) {
						if(getStatus(l[i].point) == EMPTY) {
							temp.add(l[i]);
						}
					}
				}
			}
			
			// If there are neighbors, the player chooses the closest one and analyzes it.
			if(!temp.isEmpty()) {
				Point nextLocation = temp.element().point;
				analyze(nextLocation);
			}
		}
		
		// A class that keeps track of heuristics of points
		class Location implements Comparable<Location>{
			Point point;
			double h;		// Heuristic is the distance of the point from the end location
			
			Location(Point p) {
				point = new Point();
				point.x = p.x;
				point.y = p.y;
				h = Math.sqrt(Math.pow(endLocation.x-point.x, 2) + Math.pow(endLocation.y-point.y,  2));
			}
			
			public int compareTo(Location l) {

				return (int) (this.h - l.h);
			}
			
			public String toString() {
				return point.toString() + "; h: " + h;
			}
		};
	};
}