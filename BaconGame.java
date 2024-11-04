import java.io.IOException;
import java.util.*;

public class BaconGame {
        private Scanner in = new Scanner(System.in);
        private Graph<String, Set<String>> baconGraph;
        private static Graph<String, Set<String>> treePath;
        private static String center;

        public BaconGame(String movieFilePath, String actorFilePath, String movieActorFilePath, String cen){
            try {
                baconGraph = BaconGraphBuilder.createGraph(movieFilePath, actorFilePath, movieActorFilePath);
                treePath = BaconGraph.bfs(baconGraph, cen);
                center = cen;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void changeCenter(String newCen){
            treePath = BaconGraph.bfs(baconGraph, newCen);
            center = newCen;
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
            System.out.println(separations);
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

        public List<String> withinDegree(int low, int high){
            List<String> actors = new ArrayList<>();
            for (String actor : baconGraph.vertices()){
                int degree = baconGraph.inDegree(actor);
                if(degree <= high && degree >= low){
                    actors.add(actor);
                }
            }
            return actors;
        }

        public Set<String> infinteSep(){
            return BaconGraph.missingVertices(baconGraph, treePath);
        }

        public List<String> sortActors(int low, int high){
            PriorityQueue<String> sorted = new PriorityQueue<>((String actor1, String actor2) -> sepFromCen(actor1) - sepFromCen(actor2));
            for(String actor : treePath.vertices()){
                if(sepFromCen(actor) <= high && sepFromCen(actor) >= low){
                    sorted.add(actor);
                }
            }
            List<String> ordered = new ArrayList<>(); //using priority queue toString messes up order
            while(!sorted.isEmpty()) {
                ordered.add(sorted.poll());
            }
            return ordered;
        }

        public int sepFromCen(String actor){
            int sep = 0;
            while(treePath.outDegree(actor) > 0){
                sep++;
                actor = treePath.outNeighbors(actor).iterator().next();
            }
            return sep;
        }

        public void play() throws Exception{

            System.out.println("Commands:\n" +
                    "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" +
                    "d <low> <high>: list actors sorted by degree, with degree between low and high\n" +
                    "i: list actors with infinite separation from the current center\n" +
                    "p <name>: find path from <name> to current center of the universe\n" +
                    "s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" +
                    "u <name>: make <name> the center of the universe\n" +
                    "q: quit game");

            String input = in.nextLine();
            while (!input.equals("q")){

                if(input.equals("c")) {
                    System.out.println("How many centers to display?");
                    int i = in.nextInt();
                    if(Math.abs(i) > baconGraph.numVertices()){
                        throw new Exception("invalid number of actors");
                    }
                    List<String> centers = new ArrayList<>();
                    if(i>0){
                        PriorityQueue<String> top = separations("top");
                         for(int j = 0; j < i; j++){
                             centers.add(top.remove());
                         }
                    }else{
                        PriorityQueue<String> bottom = separations("bottom");
                        for(int j = 0; j < i*(-1); j++){
                            centers.add(bottom.remove());
                        }
                    }
                    System.out.println(centers);
                    input = in.nextLine();
                }

                if(input.equals("d")){
                    System.out.println("lower bound degree:");
                    int l = in.nextInt();
                    System.out.println("upper bound degree");
                    int u = in.nextInt();
                    List<String> actors = withinDegree(l, u);
                    if(actors.isEmpty()){
                        System.out.println("no actors with specified degrees");
                    } else {
                        System.out.println(actors);
                    }
                    input = in.nextLine();
                }

                if (input.equals("i")){
                    System.out.println(infinteSep());
                }

                if (input.equals("p")){
                    //String[] split = input.split(" ");
                    System.out.println("Whose " + center + " number would you like to calculate?");
                    input = in.nextLine();
                    if (treePath.hasVertex(input)){ //?
                        System.out.println(findPath(input));
                        input = in.nextLine();
                     }
                     else {
                        System.out.println(input + " does not exist.");
                        input = in.nextLine();
                    }
                }

                if (input.equals("s")){
                    System.out.println("lowest separation:");
                    int low = in.nextInt();
                    System.out.println("highest separation");
                    int high = in.nextInt();
                    List<String> actors = sortActors(low, high);
                    if(actors.isEmpty()){
                        System.out.println("no actors with specified degrees");
                    } else {
                        System.out.println(actors);
                    }
                    input = in.nextLine();
                }

                if (input.equals("u")){
                    System.out.println("new center of universe:");
                    String newCenter = in.nextLine();
                    if (treePath.hasVertex(newCenter)){
                        changeCenter(newCenter);
                    }
                    else {
                        System.out.println(newCenter + " does not exist.");
                    }
                    input = in.nextLine();
                }

                else{
                    //System.out.println("invalid command");
                    input = in.nextLine();
                }

            }
            in.close();
        }

    public static void main(String[] args) {
        BaconGame test = new BaconGame("moviesTest.txt", "actorsTest.txt", "movie-actorsTest.txt", "Kevin Bacon");
        System.out.println(test.separations("top"));
        System.out.println(test.separations("bottom"));
        try {
            test.play();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
