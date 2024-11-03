import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class BaconGame {
        private Scanner in;
        private Graph<String, Set<String>> baconGraph;
        private Graph<String, Set<String>> treePath;
        private String center;

        public BaconGame(String movieFilePath, String actorFilePath, String movieActorFilePath){
            try {
                BaconGraphBuilder builder = new BaconGraphBuilder();
                baconGraph = builder.createGraph(movieFilePath, actorFilePath, movieActorFilePath);
                treePath = BaconGraph.bfs(baconGraph, "center");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void changeCenter(String newCen){
            center = newCen;
            treePath = BaconGraph.bfs(baconGraph, "center");
        }

        public String findPath(String actor){
            List<String> path = BaconGraph.getPath(treePath, actor);
            if(path.isEmpty()){
                return "no path between " + center + " and " + actor;
            }
            String out = "";
            out += actor + "'s number is" + path.size() + "\n";
            for(int i = 0; i < path.size()-1; i++){
                String curr = path.get(i);
                String next = path.get(i+1);
                String movie = treePath.getLabel(curr, next).toString();
                out += curr + " appeared in " + movie + " with " + next;
            }
            return out;
        }

        public void play(){

        }

    public static void main(String[] args) {
        BaconGame test = new BaconGame("moviesTest.txt", "actorsTest.txt", "movie-actorsTest.txt");
        System.out.println(findPath("Charlie"));
    }
}
