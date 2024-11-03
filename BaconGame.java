import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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

        public void play(){

        }

    public static void main(String[] args) {
        BaconGame test = new BaconGame("moviesTest.txt", "actorsTest.txt", "movie-actorsTest.txt");
        System.out.println(findPath("Dartmouth (Earl thereof)"));
    }
}
