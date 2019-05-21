package actorSearch;

import actorSearch.StorageGraph.ActorGraph;
import actorSearch.StorageGraph.MovieGraph;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.Pseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.graph.SimpleGraph;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class GraphController {

    private static final Logger logger = LoggerFactory.getLogger(LookupService.class);
    private LookupService lookupService;
    private final RestTemplate restTemplate;

    public static final String ANSI_RESET = "\033[0m";;
    public static final String ANSI_YELLOW = "\033[0;33m";
    public static final String ANSI_PURPLE = "\033[0;35m";
    public static final String ANSI_RED = "\033[0;31m";


    public GraphController(RestTemplateBuilder restTemplateBuilder,LookupService lookupService) {
        this.restTemplate = restTemplateBuilder.build();
        this.lookupService = lookupService;
    }

    @Async("LookupServiceExecutor")
    public CompletableFuture<JSONArray> getPath(String id1,String id2) throws InterruptedException {

       try{
           Graph<ActorGraph, MovieGraph> g = new SimpleGraph<>(MovieGraph.class);
           CompletableFuture<ActorGraph> completableFutureActor1 = lookupService.findActor(id1);
           CompletableFuture<ActorGraph> completableFutureActor2 = lookupService.findActor(id2);

           ActorGraph startActor = completableFutureActor1.get();
           ActorGraph targetActor = completableFutureActor2.get();
           logger.info(startActor.getName()+" " + startActor.getId());
           logger.info(targetActor.getName()+" " + targetActor.getId());

           Queue<ActorGraph> actorGraphQueue  = new ArrayDeque<>();

           actorGraphQueue.add(startActor);
           g.addVertex(startActor);
           List<CompletableFuture<MovieGraph>> futureListOfMovie;
           boolean targetFound = false;
               while (!actorGraphQueue.isEmpty()) {
                   futureListOfMovie = new ArrayList<>();
                   ActorGraph sourceActorGraph = actorGraphQueue.remove();

                   CompletableFuture<List<MovieGraph>> movieListPage = lookupService.findMovieList(sourceActorGraph.getId());
                   sourceActorGraph.setMovies(movieListPage.get());
                   List<MovieGraph> movieList = sourceActorGraph.getMovies();
                   System.out.println(ANSI_YELLOW + "Searching for " + sourceActorGraph.getName() + " movies' actors" + ANSI_RESET);

                   for (int i = 0; i < movieList.size(); i++) {
                       futureListOfMovie.add(lookupService.findMovie(movieList.get(i).getId()));
                   }


                   for (int i = 0; i < movieList.size(); i++) {
                       movieList.set(i, futureListOfMovie.get(i).get());
                   }

                   for (int i = 0; i < sourceActorGraph.getMovies().size(); i++) {
                       List<ActorGraph> actorList = sourceActorGraph.getMovies().get(i).getActors();
                       if (actorList.contains(targetActor)) {
                           targetFound = true;
                           g.addVertex(targetActor);
                           g.addEdge(sourceActorGraph, targetActor, (MovieGraph) sourceActorGraph.getMovies().get(i).clone());
                           break;
                       }

                       for (int j = 0; j < actorList.size(); j++) {
                           ActorGraph actor = actorList.get(j);
                           //System.out.println(ANSI_PURPLE +/* "FILM: " + sourceActorGraph.getMovies().get(i).getTitle() + " " + sourceActorGraph.getMovies().get(i).getId() + */" Actors: " + actor.getName()+ ANSI_RESET);
                           if (actor.equals(sourceActorGraph)) continue;
                           if (!g.containsVertex(actor)) {
                               //System.out.println(ANSI_PURPLE +/* "FILM: " + sourceActorGraph.getMovies().get(i).getTitle() + " " + sourceActorGraph.getMovies().get(i).getId() + */" Actors: " + actor.getName() + " film ID "+ sourceActorGraph.getMovies().get(i) + ANSI_RESET);
                               actorGraphQueue.add(actor);
                               g.addVertex(actor);
                               g.addEdge(sourceActorGraph, actor, (MovieGraph) sourceActorGraph.getMovies().get(i).clone());
                           } else if (!g.containsEdge(sourceActorGraph, actor)) {
                               g.addEdge(sourceActorGraph, actor, (MovieGraph) sourceActorGraph.getMovies().get(i).clone());
                           }
                       }
                   }
                   //System.out.println(ANSI_RED +" edge: " + g + ANSI_RESET);
                   if (targetFound == true) break;
               }

           Set<ActorGraph> vertices = g.vertexSet();
           BellmanFordShortestPath<ActorGraph, MovieGraph> bfsp = new BellmanFordShortestPath<>(g);
           GraphPath<ActorGraph, MovieGraph> shortestPath = bfsp.getPath(startActor,targetActor);
           List<MovieGraph> edges = shortestPath.getEdgeList();
           List<ActorGraph> actorsPath = shortestPath.getVertexList();

           for(int i = 0; i < actorsPath.size(); ++i){
               if(i == actorsPath.size()-1)
                    System.out.println(ANSI_RED +actorsPath.get(i).getName() + ANSI_RESET);
               else
                   System.out.println(ANSI_RED +actorsPath.get(i).getName() +" id:"+ actorsPath.get(i).getId() + " -> " + edges.get(i).getTitle()+ " id: " + edges.get(i).getId() + " -> "+ ANSI_RESET);
           }
           JSONArray jsonArray = new JSONArray();
           for(int i = 0; i < actorsPath.size(); ++i){
               if(i == actorsPath.size()-1) {
                   JSONObject jsonObjectVertex = actorsPath.get(i).toJson();
                   jsonArray.put(jsonObjectVertex);
               }

               else{
                   JSONObject jsonObjectVertex = actorsPath.get(i).toJson();
                   JSONObject jsonObjectEdge = edges.get(i).toJson();
                   jsonArray.put(jsonObjectVertex);
                   jsonArray.put(jsonObjectEdge);
               }
           }
           return CompletableFuture.completedFuture(jsonArray);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



}
