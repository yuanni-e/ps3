import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * builds kevin bacon graph to be used in BaconGame
 * ps-4 cs 10
 * @author claire chang
 * @author annie yuan
 */
public class BaconGraphBuilder {

    /**
     * creates a graph representing the relationships between actors/costars, parsing from three txt files
     * @param movieFilePath string filepath for file w movie ids attached to movie names
     * @param actorFilePath string filepath for file w actor ids attached to actor names
     * @param movieActorFilePath string filepath for file w actor ids attached to the ids of the movie(s) they've appeared in
     * @return a graph w relationships between all actors listed in the files + movie names as edge labels
     * @throws IOException if error while reading files
     */
    public static AdjMapGraph<String, Set<String>> createGraph(String movieFilePath, String actorFilePath, String movieActorFilePath) throws IOException{
        AdjMapGraph<String, Set<String>> baconGraph = new AdjMapGraph<>();

        BufferedReader movieInput = new BufferedReader(new FileReader(movieFilePath)); //set u buffered readers for each file path
        BufferedReader actorInput = new BufferedReader(new FileReader(actorFilePath));
        BufferedReader movieActorInput = new BufferedReader(new FileReader(movieActorFilePath));

        try {
            Map<Integer, String> actorMap = new HashMap<>(); //maps integer id to String actor
            Map<Integer, String> movieMap = new HashMap<>(); //maps integer id to String movie
            Map<Integer, List<Integer>> movieActorMap = new HashMap<>(); //maps movie id to a list of actor ids that were featured in movie

            String movieLine = movieInput.readLine();
            while (movieLine != null){ //while there are still lines in the file left to read
                String[] s = movieLine.split("\\|"); //ignore pipe symbol
                movieMap.put(Integer.parseInt(s[0]), s[1]); //put movie id as key, movie title as value
                movieLine = movieInput.readLine(); //get next line of file
            }

            String actorLine = actorInput.readLine();
            while (actorLine != null){
                String[] s = actorLine.split("\\|");
                actorMap.put(Integer.parseInt(s[0]), s[1]); //put actor id as key, actor name as value
                actorLine = actorInput.readLine();
            }

            String movieActorLine = movieActorInput.readLine();
            while (movieActorLine != null){
                String[] s = movieActorLine.split("\\|");
                if (!movieActorMap.containsKey(Integer.parseInt(s[0]))){ //if map does not already contain movie id as a key
                    movieActorMap.put(Integer.parseInt(s[0]), new ArrayList<>()); //add the key as a new entry to map; instantiate an empty actor list as its value
                }
                movieActorMap.get(Integer.parseInt(s[0])).add(Integer.parseInt(s[1])); //add to actor list each time movie id appears in file
                movieActorLine = movieActorInput.readLine();
            }

            for (Integer key : actorMap.keySet()){
                baconGraph.insertVertex(actorMap.get(key)); //insert all actors into graph
            }

            for (int i : movieActorMap.keySet()){ //for each movie id
                for (int j = 0; j < movieActorMap.get(i).size() - 1; j++){ //
                    for (int k = j + 1; k < movieActorMap.get(i).size(); k++){ //set of actors in movie i
                        //if graph does not already have edge between actor 1 and actor 2 that are both in movie i
                        if (!baconGraph.hasEdge(actorMap.get(movieActorMap.get(i).get(j)), actorMap.get(movieActorMap.get(i).get(k)))){
                            //insert undirected edge between the two, instantiate empty set as its label
                            baconGraph.insertUndirected(actorMap.get(movieActorMap.get(i).get(j)), actorMap.get(movieActorMap.get(i).get(k)), new HashSet<>());
                        }
                        //add movie corresponding to movie id to the edge label set
                        baconGraph.getLabel(actorMap.get(movieActorMap.get(i).get(j)), actorMap.get(movieActorMap.get(i).get(k))).add(movieMap.get(i));
                    }
                }
            }

        }
        catch (IOException e){
            System.out.println(e);
        }
        finally {
            try{
                movieInput.close(); //close all BufferedReaders
                actorInput.close();
                movieActorInput.close();
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
        return baconGraph; //completed graph returned
    }
}
