import java.util.*;

/**
 * Adjancency Map implementation of the Graph interface
 * Edge labels are stored in nested maps: { v1 -> { v2 -> edge } }
 * Inspired by and loosely based on Goodrich & Tamassia
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author Tim Pierson, Dartmouth CS10, provided for Fall 2024
 */

public class BaconGraph<V,E>  {

	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source){
		System.out.println("\nBreadth First Search from " + source);
		Graph<V, E> tree = new AdjMapGraph<>(); //initialize backTrack
		tree.insertVertex(source); //load start vertex with null parent
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
					tree.insertVertex(v); //save that this vertex was discovered from prior vertex
					tree.insertDirected(v, u, g.getLabel(v, u));
				}
			}
		}
		return tree;
	}

	//given a shortest path tree and a vertex, construct a path from the vertex back to the center of the universe
	public static <V,E> List<V> getPath(Graph<V,E> tree, V v){
		List<V> path = new ArrayList<>();
		if(tree.hasVertex(v)){
			while(tree.outDegree(v) > 0){
				path.add(v);
				v = tree.outNeighbors(v).iterator().next();
			}
			path.add(v);
			return path;
		}
		return null;
	}

	//given a graph and a subgraph (here shortest path tree),
	//determine which vertices are in the graph but not the subgraph (here, not reached by BFS)
	public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph){
		Set<V> missing = new HashSet<>();
		for(V v1: graph.vertices()){
			boolean found = false;
			for(V v2: subgraph.vertices()){
                if (v1.equals(v2)) {
                    found = true;
                    break;
                }
			}
			if (!found){
				missing.add(v1);
			}
		}
		return missing;
	}

	//find the average distance-from-root in a shortest path tree.
	//note: do this without enumerating all the paths! Hint: think tree recursion...
	public static <V,E> double averageSeparation(Graph<V,E> tree, V root){
		return ((double)totalDistance(tree, root)/ (double)tree.numVertices());
	}

	public static <V,E> int totalDistance (Graph<V,E> tree, V root){
		int totalDist = 0;
		while(tree.inDegree(root) != 0){
			for (V child : tree.inNeighbors(root)) {
				totalDist += 1 + totalDistance(tree, tree.inNeighbors(child).iterator().next());
			}
		}
		totalDist++;
		return totalDist;

//		int totalDist = 0;
//		if(tree.inDegree(root) == 0){
//			totalDist++;
//		}
//		else {
//			for (V child : tree.inNeighbors(root)) {
//				totalDist += 1 + totalDistance(tree, tree.inNeighbors(child).iterator().next());
//			}
//		}
//		return totalDist;
	}

	public static void main(String[] args) {
		try{
			BaconGraphBuilder test = new BaconGraphBuilder();
			AdjMapGraph<String, Set<String>> g = test.createGraph("moviesTest.txt", "actorsText.txt", "movie-actorsTest.txt");
			System.out.println(g);
			Graph<String, Set<String>> bfs = bfs(g, "Kevin Bacon");
			System.out.println(bfs);
			System.out.println(getPath(bfs, "Charlie"));
			System.out.println(missingVertices(g, bfs));
			System.out.println(averageSeparation(g, "Kevin Bacon"));
		}
		catch (Exception e){
			System.out.println("error");
		}
	}


	/** 
	 * Returns a string representation of the vertex and edge lists.
	 */
//	public String toString() {
//		return "Vertices: " + out.keySet().toString() + "\nOut edges: " + out.toString();
//	}
}
