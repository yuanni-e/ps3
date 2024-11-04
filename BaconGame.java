import java.io.IOException;
import java.util.*;

public class BaconGame {
    private Scanner in = new Scanner(System.in); //scanner for keyboard input
    private Graph<String, Set<String>> baconGraph;
    private static Graph<String, Set<String>> treePath;
    private static String center;

    public BaconGame(String movieFilePath, String actorFilePath, String movieActorFilePath, String cen){
        try {
            baconGraph = BaconGraphBuilder.createGraph(movieFilePath, actorFilePath, movieActorFilePath); //build graph from file inputs
            treePath = BaconGraph.bfs(baconGraph, cen); //do a bfs and create shortest path tree
            center = cen; //set center of universe equal to whatever cen is passed in as
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Changes the center of the universe
     *
     * @param newCen the name of the actor that is the new center
     */
    public void changeCenter(String newCen){
        treePath = BaconGraph.bfs(baconGraph, newCen); //new path tree is created by doing a bfs with newCen as the center
        center = newCen; //center is now equal to newCen
        System.out.println(newCen + " is now the center of the acting universe, connected to " + (treePath.numVertices() - 1) + "/" + baconGraph.numVertices() + " actors with average separation " + BaconGraph.averageSeparation(treePath, center));
    }

    /**
     * Finds a path from actor to the center of the universe
     *
     * @param actor the name of the actor that acts as the start of the path
     * @return the path as a String
     */
    public static String findPath(String actor){
        List<String> path = BaconGraph.getPath(treePath, actor); //calls getPath on path tree and passed in actor; stores the returned list
        if(path.isEmpty()){ //if getPath did not return a list
            return "No path between " + center + " and " + actor;
        }
        String out = "";
        out += actor + "'s number is " + (path.size() - 1) + "\n"; //[actor]'s number should be one less than the size of list
        for(int i = 0; i < path.size() - 1; i++){
            String curr = path.get(i);
            String next = path.get(i + 1); //get and store actor at i and actor at i + 1 in the path
            String movie = treePath.getLabel(curr, next).toString(); //gets the movie(s) that features curr and next from their edge label
            if (i == path.size() - 2){
                //last line added, no new line needed
                out += curr + " appeared in " + movie + " with " + next;
            }
            else {
                out += curr + " appeared in " + movie + " with " + next + "\n";
            }
        }
        return out;
    }

    /**
     * Finds and orders top/bottom centers of the universe, sorted by average separation between non-centers and center;
     * "top" center corresponds to lesser average separation, and vice versa
     *
     * @param order either "top" or "bottom"
     * @return a priority queue that puts the separations in order
     */
    public PriorityQueue<String> separations (String order){
        Map<String, Double> separations = new HashMap<>();
        for (String person : treePath.vertices()) { //for each actor in baconGraph
            Graph<String, Set<String>> actorPaths = BaconGraph.bfs(baconGraph, person); //does a bfs and gets a shortest path tree using the actor in baconGraph as a center
            separations.put(person, BaconGraph.averageSeparation(actorPaths, person)); //create a new entry in map, with the actor and the result of averageSeparation using the path tree we created, with the actor as the root
        }
        PriorityQueue<String> orderedSeparations = null;
        if(order.equals("top")){ //if "top" was passed in
            //instantiate orderedSeparations, where the pq is ordered from lesser separation to greater separation
            orderedSeparations = new PriorityQueue<String>((String actor1, String actor2) -> Double.compare(separations.get(actor1), separations.get(actor2))); //anonymous function compares the avg separations of actor1 and actor2
        } else if (order.equals("bottom")) { //if "bottom" was passed in
            //instantiate orderedSeparations, where the pq is ordered from greater separation to lesser separation
            orderedSeparations = new PriorityQueue<String>((String actor1, String actor2) -> Double.compare(separations.get(actor2), separations.get(actor1)));
        }
        if (orderedSeparations != null){ //will return null if something other than "top" or "bottom" was passed in
            for (String person : treePath.vertices()){
                orderedSeparations.add(person); //add actors to pq
            }
        }
        return orderedSeparations;
    }

    /**
     * Finds actors that are within [low, high] degree (# of undirected edges in BaconGraph)
     *
     * @param low lower bound for degree
     * @param high upper bound for degree
     * @return an ordered list of actors within bounds
     */
    public List<String> withinDegree(int low, int high){
        List<String> actors = new ArrayList<>();
        //loop through each actor in the main graph
        for (String actor : baconGraph.vertices()){
            int degree = baconGraph.inDegree(actor); //outDegree would achieve the same result
            if(degree <= high && degree >= low){ //if degree of actor is within bounds of [low, high]
                actors.add(actor); //add to list
            }
        }
        actors.sort((String a1, String a2)-> baconGraph.inDegree(a2) - baconGraph.inDegree(a1)); //sort actors from largest to smallest inDegree
        return actors;
    }

    /**
     * Finds a set of actors that have infinite separation from the center of the universe (no path existing between actor and center)
     *
     * @return a set of actors with infinite separation from current center
     */
    public Set<String> infiniteSep(){
        return BaconGraph.missingVertices(baconGraph, treePath); //call missingVertices to find vertices that could not be reached by bfs
    }

    /**
     * Finds actors that are within [low, high] separation from current center
     *
     * @param low lower bound for separation
     * @param high upper bound for separation
     * @return an ordered list of actors within bounds
     */
    public List<String> sortActors(int low, int high){
        PriorityQueue<String> sorted = new PriorityQueue<>((String actor1, String actor2) -> sepFromCen(actor1) - sepFromCen(actor2)); //anonymous function compares the separations of actor1 and actor2 from current center; calls sepFromCen
        for (String actor : treePath.vertices()){
            if (sepFromCen(actor) <= high && sepFromCen(actor) >= low){ //if separation from center is within [low, high] bounds
                sorted.add(actor); //add actor to pq
            }
        }
        List<String> ordered = new ArrayList<>(); //using priority queue toString messes up order
        while(!sorted.isEmpty()) {
            ordered.add(sorted.poll()); //poll from pq, add to list in order
        }
        return ordered;
    }

    /**
     * Calculates an actor's separation from the center of the universe
     * helper method for sortActors
     *
     * @param actor the name of the target actor
     * @return the actor's separation from center as an int
     */
    public int sepFromCen(String actor){
        int sep = 0;
        while(treePath.outDegree(actor) > 0){ //while path can still be followed to center (actor has out neighbors)
            sep++; //increment separation
            actor = treePath.outNeighbors(actor).iterator().next(); //update actor to point to its parent (a costar that actor has been in a movie with)
        }
        return sep;
    }

    public void play() throws Exception {

        //hi daniel, this isn't redundant, we tested an empty file and it still called play(), we had to
        if (baconGraph == null) {
            throw new Exception("No graph was created.");
        }

        //list of possible commands
        System.out.println("Commands:\n" +
                "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" +
                "d <low> <high>: list actors sorted by degree, with degree between low and high\n" +
                "i: list actors with infinite separation from the current center\n" +
                "p <name>: find path from <name> to current center of the universe\n" +
                "s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" +
                "u <name>: make <name> the center of the universe\n" +
                "q: quit game\n" +
                center + " is now the center of the acting universe, connected to " + (treePath.numVertices() - 1) + "/" + baconGraph.numVertices() + " actors with average separation " + BaconGraph.averageSeparation(treePath, center));

        String input = in.nextLine(); //gets user's keyboard input
        while (!input.equals("q")){ //while user has not quit game
            if (input.equals("c")) {
                System.out.println("How many possible centers to display (positive or negative number)?");
                int i = in.nextInt(); //prompts user to enter an integer
                if(Math.abs(i) > baconGraph.numVertices()){ //if |i| exceeds number of vertices in graph
                    throw new Exception("Invalid number of actors");
                }
                List<String> centers = new ArrayList<>();
                if (i > 0){ //user's input was a positive number
                    PriorityQueue<String> top = separations("top"); //call to separations with "top"
                    for(int j = 0; j < i; j++){
                        centers.add(top.poll()); //add to centers i times
                    }
                }
                else if (i < 0){ //user's input was a negative number
                    PriorityQueue<String> bottom = separations("bottom"); //call to separations with "bottom"
                    for(int j = 0; j < i * (-1); j++){
                        centers.add(bottom.poll()); //add to centers i times
                    }
                }
                else { //user inputted i as 0
                    System.out.println("No centers");
                }
                System.out.println(centers);
                input = in.nextLine(); //call for user to input another character
            }

            else if (input.equals("d")){
                System.out.println("Lower bound degree:");
                int low = in.nextInt(); //prompts user to enter an integer
                System.out.println("Upper bound degree: ");
                int high = in.nextInt(); //prompts user to enter another integer
                List<String> actors = withinDegree(low, high); //call to withinDegree with bounds low and high
                if (actors.isEmpty()){ //no actors were within [low, high] degree
                    System.out.println("No actors within specified degrees");
                } else {
                    System.out.println(actors);
                }
                input = in.nextLine();
            }

            else if (input.equals("i")){
                System.out.println(infiniteSep()); //call to infiniteSep()
                input = in.nextLine();
            }

            else if (input.equals("p")){
                System.out.println("Whose " + center + " number would you like to calculate?");
                String pathFromActor = in.nextLine(); //prompts user to enter an actor name
                if (treePath.hasVertex(pathFromActor)){ //if user input is a valid vertex within path tree
                    System.out.println(findPath(pathFromActor)); //call to findPath with said vertex
                    input = in.nextLine();
                }
                else if (infiniteSep().contains(pathFromActor)){ //if user input is a vertex with infinite separation from center
                    System.out.println("No path to center; " + pathFromActor + "'s " + center + "'s number is infinite");
                    input = in.nextLine();
                } else { //if user input is not a valid vertex in the graph
                    System.out.println(pathFromActor + " does not exist.");
                    input = in.nextLine();
                }

            }
            else if (input.equals("s")){
                System.out.println("Lowest separation:");
                int low = in.nextInt();
                System.out.println("Highest separation");
                int high = in.nextInt();
                List<String> actors = sortActors(low, high); //call to sortActors with bounds low and high
                if (actors.isEmpty()){ //no actors were within [low, high] bounds
                    System.out.println("No actors within specified separations");
                } else {
                    System.out.println(actors);
                }
                input = in.nextLine();
            }

            else if (input.equals("u")){
                System.out.println("New center of universe: ");
                String newCenter = in.nextLine();
                if (baconGraph.hasVertex(newCenter)){ //if user input is a valid vertex within the graph
                    changeCenter(newCenter); //call changeCenter with the new center user passed in
                }
                else {
                    System.out.println(newCenter + " does not exist.");
                }
                input = in.nextLine();
            }

            else{
                System.out.println("Invalid command");
                input = in.nextLine();
            }
        }
        in.close();
    }

    public static void main(String[] args) {
        //create new BaconGame, pass in proper txt files and set Kevin Bacon as default center
        BaconGame test1 = new BaconGame("bacon/moviesTest.txt", "bacon/actorsTest.txt", "bacon/movie-actorsTest.txt", "Kevin Bacon");
        BaconGame test2 = new BaconGame("bacon/movies.txt", "bacon/actors.txt", "bacon/movie-actors.txt", "Kevin Bacon");
        try {
            test1.play(); //play BaconGame (using test data)!
            test2.play(); //play BaconGame (using actual data)!
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}