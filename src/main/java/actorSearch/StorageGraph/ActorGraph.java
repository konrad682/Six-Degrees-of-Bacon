package actorSearch.StorageGraph;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ActorGraph {
    private String name;
    private String id;
    private List<MovieGraph> movies = new ArrayList<>();

    public ActorGraph(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public ActorGraph() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActorGraph that = (ActorGraph) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ActorGraph{" +
                "id=" + id +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MovieGraph> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieGraph> movies) {
        this.movies = movies;
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID: ",id);
        jsonObject.put("Name: ",name);
        return jsonObject;
    }
}
