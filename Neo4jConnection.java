import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4jConnection implements AutoCloseable {
    private final Driver driver;

    public Neo4jConnection(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() {
        driver.close();
    }

public void addUser(User user) {
    try (Session session = driver.session()) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", user.getUsername());
        parameters.put("password", user.getPassword());
        parameters.put("dealBreakers", user.getDealBreakers());
        
        session.writeTransaction(tx -> tx.run("CREATE (u:User {username: $username, password: $password, dealBreakers: $dealBreakers})",
                parameters));
    }
}


    public User getUser(String username) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Record record = tx.run("MATCH (u:User {username: $username}) RETURN u.username AS username, u.password AS password, u.dealBreakers AS dealBreakers",
                        Values.parameters("username", username)).single();
                User user = new User(record.get("username").asString(), record.get("password").asString());
                Map<String, String> dealBreakers = record.get("dealBreakers").asMap(Value::asString);
                for (Map.Entry<String, String> entry : dealBreakers.entrySet()) {
                    user.addDealBreaker(entry.getKey(), entry.getValue());
                }
                return user;
            });
        }
    }

    public void removeUser(String username) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run("MATCH (u:User {username: $username}) DETACH DELETE u",
                    Values.parameters("username", username)));
        }
    }

    public List<User> getRecommendations(User user) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                List<User> recommendations = new ArrayList<>();
                String query = "MATCH (u:User) WHERE u.username <> $username RETURN u.username AS username";
                List<Record> result = tx.run(query, Values.parameters("username", user.getUsername())).list();
                for (Record record : result) {
                    String username = record.get("username").asString();
                    User candidate = getUser(username);
                    if (matchesDealBreakers(user, candidate)) {
                        recommendations.add(candidate);
                    }
                }
                return recommendations;
            });
        }
    }

    private boolean matchesDealBreakers(User user, User candidate) {
        Map<String, String> dealBreakers = user.getDealBreakers();
        for (Map.Entry<String, String> entry : dealBreakers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!candidate.getDealBreakers().getOrDefault(key, "").equalsIgnoreCase(value)) {
                return false;
            }
        }
        return true;
    }
}
