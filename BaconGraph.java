import java.util.*;

public class BaconGraph<V,E> {

	/**
	 * modified breadth first search on a bacon graph to create a path tree from the center of the universe to its connected vertices
	 * @param g kevin bacon graph used
	 * @param source the chosen center of the universe or root of the path tree
	 * @return a path tree
	 */
	public static <V, E> Graph<V, E> bfs(Graph<V, E> g, V source) {
		Graph<V, E> tree = new AdjMapGraph<>(); //initialize path tree
		tree.insertVertex(source); //insert source into tree
		Set<V> visited = new HashSet<V>(); //set to track which vertices have already been visited
		Queue<V> queue = new LinkedList<V>(); //queue to implement bfs

		queue.add(source); //enqueue start vertex
		visited.add(source); //add start to visited Set
		while (!queue.isEmpty()) { //loop until no more vertices
			V u = queue.remove(); //dequeue
			for (V v : g.inNeighbors(u)) { //loop over in neighbors
				if (!visited.contains(v)) { //if neighbor not visited, then neighbor is discovered from this vertex
					visited.add(v); //add neighbor to visited set
					queue.add(v); //enqueue neighbor
					tree.insertVertex(v); //insert vertex into tree
					tree.insertDirected(v, u, g.getLabel(v, u)); //insert directed edge from v to u, get edge label from graph
				}
			}
		}
		return tree;
	}

	/**
	 * given a shortest path tree and a vertex, construct a path from the vertex back to the center of the universe
	 * @param tree path tree used to traverse through
	 * @param v the vertex from which the path states
	 * @return list of vertices from v to the center of the universe, null if v is not connected to the center
	 */
	public static <V, E> List<V> getPath(Graph<V, E> tree, V v) {
		List<V> path = new ArrayList<>();
		if (tree.hasVertex(v)) { //if v is a valid vertex in path tree
			while (tree.outDegree(v) > 0) { //while there is a path to follow towards the center
				path.add(v); //add v to the list that is keeping track of path
				v = tree.outNeighbors(v).iterator().next(); //update v to point to its parent
			}
			path.add(v); //add center to path
			return path;
		}
		return null; //v was not a valid vertex; return null
	}

	/**
	 * given a graph and a subgraph (here, shortest path tree),
	 * determine which vertices are in the graph but not the subgraph (here, not reached by BFS)
	 * @param graph larger graph
	 * @param subgraph subgraph (presumably build by calling bfs on graph)
	 * @return set of vertices missing from subgraph but present in graph
	 */
	public static <V, E> Set<V> missingVertices(Graph<V, E> graph, Graph<V, E> subgraph) {
		Set<V> missing = new HashSet<>();
		for (V v1 : graph.vertices()) {
			boolean found = false;
			for (V v2 : subgraph.vertices()) {
				if (v1.equals(v2)) { //if vertex found in graph was found in subgraph
					found = true; //set found to true and break from loop
					break;
				}
			}
			if (!found) { //if found is still false
				missing.add(v1); //vertex from graph was not found in subgraph; add to missing set
			}
		}
		return missing;
	}

	/**
	 * find the average distance-from-root in a shortest path tree
	 * calls recursive method totalDistance to find total distance from root, then divides by total number of vertices in tree
	 * @param tree path tree with a center
	 * @param root center of universe
	 * @return double representation of average distance of each vertex in tree from root
	 */
	public static <V, E> double averageSeparation(Graph<V, E> tree, V root) {
		return ((double) totalDistance(tree, root, 0) / (double) tree.numVertices()); //total distance of all paths/total vertices
	}

	/**
	 * recursive helper for averageSeparation
	 * finds total distance of each vertex from the center of the universe
	 * @param tree path tree being traversed
	 * @param root center of universe
	 * @param dist initial total distance (usually 0)
	 * @return int for total distance from every vertex in tree to root
	 */
	public static <V, E> int totalDistance(Graph<V, E> tree, V root, int dist) {
		int totalDist = dist;
		for (V child : tree.inNeighbors(root)) {
			totalDist += totalDistance(tree, child, dist + 1); //recursively call totalDistance on each in neighbor of root, while incrementing dist
		}
		return totalDist;
	}
}