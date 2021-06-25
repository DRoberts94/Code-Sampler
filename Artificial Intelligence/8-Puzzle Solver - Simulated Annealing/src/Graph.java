import java.util.*;

public class Graph<T> {
	
	// MEMBER CONSTANTS
		static final int MAXIMUM = 9;
		public boolean edges[][];
		public T labels[];
		int manyVertices;
	
	// CONSTRUCTOR
	Graph() {
		manyVertices = 0;
		edges = new boolean[MAXIMUM][MAXIMUM];
		labels = (T[])new Object[MAXIMUM];
	}
	
	Graph(Graph<T> g) {
		manyVertices = new Integer(g.manyVertices);
		edges = new boolean[MAXIMUM][MAXIMUM];
		for (int i = 0; i < g.edges.length; i++) {
			edges[i] = g.edges[i].clone();
		}
		labels = (T[]) g.labels.clone();
	}
	
	// MODIFICAITON MEMBER FUNCTIONS
	void addVertex(final T label) {
		int newTemp;
		int temp;
		
		assert(size() < MAXIMUM);
		newTemp = manyVertices;
		manyVertices++;
		
		for(temp = 0; temp < newTemp; temp++) {
			edges[temp][newTemp] = false;
			edges[newTemp][temp] = false;
		}
		
		labels[newTemp] = label;
	}
	
	void addEdge(int source, int target) {
		assert(source < size());
		assert(target < size());
		edges[source][target] = true;
		edges[target][source] = true;
	}
	
	void removeEdge(int source, int target) {
		assert(source < size());
		assert(target < size());
		edges[source][target] = false;
		edges[target][source] = false;
	}
	
	// CONSTNAT MEMBER FUNCTIONS
	int size() {
		return manyVertices;
	}
	
	boolean isEdge(int source, int target) {
		assert(source < size());
		assert(target < size());
		
		return edges[source][target];
	}
	
	boolean isVertex(T label) {
		for( int i = 0; i < size(); i++) {
			if(label == labels[i]) {
				return true;
			}
		}
		
		return false;
	} 
	
	T getLabel(int vertex) {
		assert(vertex < size());
		return labels[vertex];
	}
	
	void setLabel(T label, int vertex) {
		labels[vertex] = label;
	}
	
	Set<Integer> neighbors(int vertex) {
		Set<Integer> answer = new HashSet<Integer>();
		
		assert(vertex < size());
		
		for (Integer i = 0; i < size(); i++) {
			if(edges[vertex][i]) {
				answer.add(i);
			}
		}
		
		return answer;
	}
	
	String toString(Set<T> s) {
		String str = new String();
		
		for(T item : s) {
			str = str + item + "";
		}
		
		return str;
	}
	
	int vertexNumber(T label) {
		boolean contained = false;
		
		for (Integer i =0; i < size(); i++) {
			if (labels[i] == label) {
				contained = true;
			}
		}
		
		assert(contained);
		
		for (Integer i = 0; i < size(); i++) {
			if (labels[i] == label) {
				return i;
			}
		}
		
		return 0;
	}
	
	boolean equals(Graph<T> g) {
		if (this.size() != g.size()) {
			return false;
		}
		
		for (Integer i = 0; i < this.size(); i++) {
			if(!g.isVertex(this.labels[i]) || !this.isVertex(g.labels[i])) {
				return false;
			}
			
			if(!this.neighbors(i).equals(g.neighbors(g.vertexNumber(this.labels[i])))) {
				return false;
			}
		}
		
		return true;
	}
	
	
	
	
}