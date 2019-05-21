package actorSearch;

import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Component
@RestController
public class WebController {

    private List<CompletableFuture<JSONArray>> completableFutureList = new ArrayList<>();
    private static int indexOfPath=0;
    private final GraphController graphController;
    public WebController(GraphController graphController) {
        this.graphController = graphController;
    }


    @RequestMapping(value = "/actors/{id1}/{id2}", method = GET)
    public ResponseEntity<String> getFindPathFromId1toId2(@PathVariable String id1, @PathVariable String id2) throws Exception {
        int tmpIndex = indexOfPath;
        indexOfPath++;
        completableFutureList.add(graphController.getPath(id1,id2));
        return ResponseEntity.status(HttpStatus.OK).body("Index: " + tmpIndex);
    }

    @RequestMapping(value = "/actors/{id}/check", method = GET)
    public ResponseEntity<String> checkRoadFromId1toId2(@PathVariable String id) throws Exception {
        String returnString = id + " processing";
        if (completableFutureList.get(Integer.valueOf(id)).isDone()){
            returnString = id + " DONE";
        }
        return ResponseEntity.status(HttpStatus.OK).body("Searching path id: " + returnString);
    }

    @RequestMapping(value = "/actors/{id}/result", method = GET, produces = "application/json")
    public ResponseEntity<String> getRoadFromId1toId2(@PathVariable String id) throws Exception {
        if (!completableFutureList.get(Integer.valueOf(id)).isDone()){
            return ResponseEntity.status(HttpStatus.PROCESSING).body("Searching path is not done !");
        }
        String jsonString = completableFutureList.get(Integer.parseInt(id)).get().toString();
        return ResponseEntity.status(HttpStatus.OK).body(jsonString);
    }

}