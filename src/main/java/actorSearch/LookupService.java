package actorSearch;

import actorSearch.StorageGraph.ActorGraph;
import actorSearch.StorageGraph.MovieGraph;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class LookupService {

    private static final Logger logger = LoggerFactory.getLogger(LookupService.class);
    private final RestTemplate restTemplate;

    public LookupService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async("LookupServiceExecutor")
    public CompletableFuture<ActorGraph> findActor(String id) throws InterruptedException {
        String url = String.format("https://java.kisim.eu.org/actors/%s", id);
        ActorGraph results = restTemplate.getForObject(url, ActorGraph.class);
        return CompletableFuture.completedFuture(results);
    }

    @Async("LookupServiceExecutor")
    public CompletableFuture<MovieGraph> findMovie(String id) throws InterruptedException {
        String url = String.format("https://java.kisim.eu.org/movies/%s", id);
        MovieGraph results = restTemplate.getForObject(url, MovieGraph.class);
        return CompletableFuture.completedFuture(results);
    }


        @Async("LookupServiceExecutor")
        public CompletableFuture<List<MovieGraph>> findMovieList(String id) throws InterruptedException {
            //logger.info("Looking up " + id);
            String url = String.format("https://java.kisim.eu.org/actors/%s/movies", id);
            MovieGraph[] tmpResult = restTemplate.getForObject(url, MovieGraph[].class);
            List<MovieGraph> results = new ArrayList<>(Arrays.asList(tmpResult));
            return CompletableFuture.completedFuture(results);
        }



}
