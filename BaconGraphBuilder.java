import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BaconGraphBuilder {
    public AdjMapGraph<String, Set<String>> baconGraph;

    public BaconGraphBuilder() throws IOException {

    }

    public AdjMapGraph<String, Set<String>> createGraph(String movieFilePath, String actorFilePath, String movieActorFilePath) throws IOException{
        baconGraph = new AdjMapGraph<>();

        BufferedReader movieInput = new BufferedReader(new FileReader(movieFilePath));
        BufferedReader actorInput = new BufferedReader(new FileReader(actorFilePath));
        BufferedReader movieActorInput = new BufferedReader(new FileReader(movieActorFilePath));

        try {
            //vertices are the actor names (not IDs), and whose edges are labeled with sets of the movie names (again, not IDs) in which actors appeared together
            Map<Integer, String> actorMap = new HashMap<>();
            Map<Integer, String> movieMap = new HashMap<>();
            Map<Integer, List<Integer>> movieActorMap = new HashMap<>();

            String movieLine = movieInput.readLine();
            while (movieLine != null){
                String[] s = movieLine.split("\\|");
                movieMap.put(Integer.parseInt(s[0]), s[1]);
                movieLine = movieInput.readLine();
            }

            String actorLine = actorInput.readLine();
            while (actorLine != null){
                String[] s = actorLine.split("\\|");
                actorMap.put(Integer.parseInt(s[0]), s[1]);
                actorLine = actorInput.readLine();
            }

            String movieActorLine = movieActorInput.readLine();
            while (movieActorLine != null){
                String[] s = movieActorLine.split("\\|");
                if (!movieActorMap.containsKey(Integer.parseInt(s[0]))){
                    movieActorMap.put(Integer.parseInt(s[0]), new ArrayList<>());
                } //maps movie to actors in movie
                movieActorMap.get(Integer.parseInt(s[0])).add(Integer.parseInt(s[1]));

                movieActorLine = movieActorInput.readLine();
            }

            for (Integer key : actorMap.keySet()){
                baconGraph.insertVertex(actorMap.get(key)); //insert all vertices
            }

            for (int i : movieActorMap.keySet()){ //for movie
                for (int j = 0; j < movieActorMap.get(i).size() - 1; j++){
                    for (int k = j + 1; k < movieActorMap.get(i).size(); k++){ //set of actors in movie
                        if (!baconGraph.hasEdge(actorMap.get(movieActorMap.get(i).get(j)), actorMap.get(movieActorMap.get(i).get(k)))){
                            baconGraph.insertUndirected(actorMap.get(movieActorMap.get(i).get(j)), actorMap.get(movieActorMap.get(i).get(k)), new HashSet<>());
                        }
                        baconGraph.getLabel(actorMap.get(movieActorMap.get(i).get(j)), actorMap.get(movieActorMap.get(i).get(k))).add(movieMap.get(i));
                    }
                }
            }

        }
        catch (IOException e){
            System.out.println("error");
        }
        finally {
            try{
                movieInput.close();
                actorInput.close();
            }
            catch (Exception e){
                System.out.println("error");
            }
        }
        return baconGraph;
    }



}
