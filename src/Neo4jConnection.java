package src;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Result;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Neo4jConnection implements AutoCloseable {
    private final Driver driver;

    public Neo4jConnection(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public String createUser(String name, String password, String databaseName) {
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    String result;
                    Result res = tx.run("MERGE (u:User {name: $name}) RETURN u.name",
                            org.neo4j.driver.Values.parameters("name", name));
                    if (res.hasNext()) {
                        result = "User " + name + " created successfully.";
                    } else {
                        result = "Error creating user " + name;
                    }
                    return result;
                }
            });
        }
    }

    public String addLike(String username, String category, String interest, String databaseName) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User {name: $username}) " +
                        "MERGE (i:Interest {name: $interest}) " +
                        "MERGE (u)-[:LIKES {category: $category}]->(i)",
                        org.neo4j.driver.Values.parameters("username", username, "interest", interest, "category", category));
                return null;
            });
            return "Like added successfully.";
        }
    }
    
    public String addDislike(String username, String category, String interest, String databaseName) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User {name: $username}) " +
                        "MERGE (i:Interest {name: $interest}) " +
                        "MERGE (u)-[:DISLIKES {category: $category}]->(i)",
                        org.neo4j.driver.Values.parameters("username", username, "interest", interest, "category", category));
                return null;
            });
            return "Dislike added successfully.";
        }
    }

    public LinkedList<String> connectUsersBasedOnLikes(String databaseName) {
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<LinkedList<String>>() {
                @Override
                public LinkedList<String> execute(Transaction tx) {
                    LinkedList<String> connections = new LinkedList<>();
                    Result result = tx.run("MATCH (u1:User)-[:LIKES]->(i:Interest)<-[:LIKES]-(u2:User) " +
                            "WHERE u1.name <> u2.name " +
                            "RETURN u1.name, u2.name, COUNT(i) as sharedLikes " +
                            "ORDER BY sharedLikes DESC");
                    while (result.hasNext()) {
                        var record = result.next();
                        connections.add(record.get("u1.name").asString() + " y " + record.get("u2.name").asString() + " comparten " + record.get("sharedLikes").asInt() + " gustos.");
                    }
                    return connections;
                }
            });
        }
    }

    public LinkedList<String> disconnectUsersBasedOnDislikes(String databaseName) {
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<LinkedList<String>>() {
                @Override
                public LinkedList<String> execute(Transaction tx) {
                    LinkedList<String> disconnections = new LinkedList<>();
                    Result result = tx.run("MATCH (u1:User)-[:DISLIKES]->(i:Dislike)<-[:DISLIKES]-(u2:User) " +
                            "WHERE u1.name <> u2.name " +
                            "RETURN u1.name, u2.name, COUNT(i) as sharedDislikes " +
                            "ORDER BY sharedDislikes DESC");
                    while (result.hasNext()) {
                        var record = result.next();
                        disconnections.add(record.get("u1.name").asString() + " y " + record.get("u2.name").asString() + " comparten " + record.get("sharedDislikes").asInt() + " disgustos.");
                    }
                    return disconnections;
                }
            });
        }
    }

    public LinkedList<String> getRandomUsers(int limit, String databaseName) {
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<LinkedList<String>>() {
                @Override
                public LinkedList<String> execute(Transaction tx) {
                    LinkedList<String> users = new LinkedList<>();
                    Result result = tx.run("MATCH (u:User) RETURN u.name ORDER BY rand() LIMIT $limit",
                            org.neo4j.driver.Values.parameters("limit", limit));
                    while (result.hasNext()) {
                        var record = result.next();
                        users.add(record.get("u.name").asString());
                    }
                    return users;
                }
            });
        }
    }

    public String deleteUser(String name, String databaseName) {
        try (Session session = driver.session()) {
            return session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    tx.run("MATCH (u:User {name: $name}) DETACH DELETE u",
                            org.neo4j.driver.Values.parameters("name", name));
                    return "User " + name + " deleted successfully.";
                }
            });
        }
    }

    public String getUserPassword(String name, String databaseName) {
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    Result result = tx.run("MATCH (u:User {name: $name}) RETURN u.password",
                            org.neo4j.driver.Values.parameters("name", name));
                    if (result.hasNext()) {
                        return result.next().get("u.password").asString();
                    }
                    return null;
                }
            });
        }
    }

    public boolean userExists(String username, String databaseName) {
        try (Session session = driver.session()) {
            return session.readTransaction(new TransactionWork<Boolean>() {
                @Override
                public Boolean execute(Transaction tx) {
                    Result result = tx.run("MATCH (u:User {name: $username}) RETURN u",
                            org.neo4j.driver.Values.parameters("username", username));
                    return result.hasNext();
                }
            });
        }
    }
    
}
