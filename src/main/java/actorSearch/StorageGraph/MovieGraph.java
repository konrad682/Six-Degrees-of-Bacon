package actorSearch.StorageGraph;
import org.jgrapht.graph.DefaultEdge;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MovieGraph  implements Cloneable {
    private String title;
    private String id;
    private List<ActorGraph> actors = new ArrayList<>();

    public MovieGraph(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public Object clone()throws CloneNotSupportedException{
        return super.clone();
    }

    public MovieGraph() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ActorGraph> getActors() {
        return actors;
    }

    public void setActors(List<ActorGraph> actors) {
        this.actors = actors;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "MovieGraph{" +
                "id='" + id + '\'' +
                '}';
    }


    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID: ",id);
        jsonObject.put("Ttile: ",title);
        return jsonObject;
    }
}
