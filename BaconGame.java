import java.io.IOException;
import java.util.*;

public class BaconGame {
        private Scanner in;
        private Graph<String, Set<String>> baconGraph;
        private static Graph<String, Set<String>> treePath;
        private static String center;

        public BaconGame(String movieFilePath, String actorFilePath, String movieActorFilePath){
            try {
                baconGraph = BaconGraphBuilder.createGraph(movieFilePath, actorFilePath, movieActorFilePath);
                treePath = BaconGraph.bfs(baconGraph, "Kevin Bacon");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void changeCenter(String newCen){
            center = newCen;
            treePath = BaconGraph.bfs(baconGraph, "center");
        }

        public static String findPath(String actor){
            List<String> path = BaconGraph.getPath(treePath, actor);
            if(path.isEmpty()){
                return "no path between " + center + " and " + actor;
            }
            String out = "";
            out += actor + "'s number is " + (path.size()-1) + "\n";
            for(int i = 0; i < path.size()-1; i++){
                String curr = path.get(i);
                String next = path.get(i+1);
                String movie = treePath.getLabel(curr, next).toString();
                out += curr + " appeared in " + movie + " with " + next + "\n";
            }
            return out;
        }

        public PriorityQueue<String> separations (String order){
            Map<String, Double> separations = new HashMap<>();
            for(String person : baconGraph.vertices()) {
                Graph<String, Set<String>> actorPaths = BaconGraph.bfs(baconGraph, person);
                separations.put(person, BaconGraph.averageSeparation(actorPaths, person));
            }
            PriorityQueue<String> orderedSeparations = null;
            if(order.equals("top")){
                orderedSeparations = new PriorityQueue<String>((String actor1, String actor2) -> Double.compare(separations.get(actor1), separations.get(actor2)));
            } else if (order.equals("bottom")) {
                orderedSeparations = new PriorityQueue<String>((String actor1, String actor2) -> Double.compare(separations.get(actor2), separations.get(actor1)));
            }
            if(orderedSeparations != null){
                for(String person : baconGraph.vertices()){
                    orderedSeparations.add(person);
                }
            }
            return orderedSeparations;
        }

        public void play(){

        }

    public static void main(String[] args) {
        BaconGame test = new BaconGame("moviesTest.txt", "actorsTest.txt", "movie-actorsTest.txt");
        System.out.println(findPath("Dartmouth (Earl thereof)"));
    }
}
