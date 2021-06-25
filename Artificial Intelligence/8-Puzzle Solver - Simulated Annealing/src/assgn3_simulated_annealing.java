// 8-Star Puzzle Solver using Simulated Annealing


class assgn3_simulated_annealing {
	public static void main(String[] args) {
		int initialBoard[][] = 	{ {2,8,3},
				 				  {1,6,4},
				 				  {7,0,5}
		};
		
		int goalBoard[][] = 	{ {1,2,3},
					  		  	  {8,0,4},
					  		  	  {7,6,5}
		};
		
		GUI gui = new GUI(new EightPuzzle(initialBoard, goalBoard));
	}
}