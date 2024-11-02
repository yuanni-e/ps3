import java.util.*;

/**
 * Adjancency Map implementation of the Graph interface
 * Edge labels are stored in nested maps: { v1 -> { v2 -> edge } }
 * Inspired by and loosely based on Goodrich & Tamassia
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author Tim Pierson, Dartmouth CS10, provided for Fall 2024
 */

public class BFSGraph<V,E>  {
	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source){
		System.out.println("\nBreadth First Search from " + source);
		Graph<V, E> tree = new AdjMapGraph<>(); //initialize backTrack
		backTrack.put(source, null); //load start vertex with null parent
		Set<V> visited = new HashSet<V>(); //Set to track which vertices have already been visited
		Queue<V> queue = new LinkedList<V>(); //queue to implement BFS

		queue.add(source); //enqueue start vertex
		visited.add(source); //add start to visited Set
		System.out.println("Visiting " + source);
		while (!queue.isEmpty()) { //loop until no more vertices
			V u = queue.remove(); //dequeue
			for (V v : g.outNeighbors(u)) { //loop over out neighbors
				if (!visited.contains(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
					visited.add(v); //add neighbor to visited Set
					System.out.println("Visiting " + v);
					queue.add(v); //enqueue neighbor
					backTrack.put(v, u); //save that this vertex was discovered from prior vertex
				}
			}
		}
	}
	public static <V,E> List<V> getPath(Graph<V,E> tree, V v){

	}
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){

	}
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root){

	}


	/** 
	 * Returns a string representation of the vertex and edge lists.
	 */
	public String toString() {
		return "Vertices: " + out.keySet().toString() + "\nOut edges: " + out.toString();
	}
}
