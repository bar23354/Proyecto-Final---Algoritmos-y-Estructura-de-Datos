import java.util.List;

public class RecommendationSystem {
    private Neo4jConnection neo4jConnection;

    public RecommendationSystem() {
        this.neo4jConnection = new Neo4jConnection("bolt://localhost:7687", "neo4j", "password");
    }

    public void addUser(User user) {
        neo4jConnection.addUser(user);
    }

    public void removeUser(String username) {
        neo4jConnection.removeUser(username);
    }

    public User getUser(String username) {
        return neo4jConnection.getUser(username);
    }

    public List<User> getRecommendations(User user) {
        return neo4jConnection.getRecommendations(user);
    }
}
