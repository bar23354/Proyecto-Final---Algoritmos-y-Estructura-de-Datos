package src;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import static org.neo4j.driver.Values.parameters;
import java.util.LinkedList;
import java.util.List;

public class Neo4jConnection implements AutoCloseable {

    private final Driver driver;

    public Neo4jConnection(String url, String user, String password) {
        driver = GraphDatabase.driver(url, AuthTokens.basic(user, password));
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public String addLike(String userName, String interestName, String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.writeTransaction(tx -> {
                tx.run("MERGE (u:User {name: $userName}) " +
                        "MERGE (i:Interest {name: $interestName}) " +
                        "MERGE (u)-[:LIKES]->(i)",
                        parameters("userName", userName, "interestName", interestName));
                return "Like added";
            });
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String addDislike(String userName, String interestName, String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.writeTransaction(tx -> {
                tx.run("MERGE (u:User {name: $userName}) " +
                        "MERGE (i:Interest {name: $interestName}) " +
                        "MERGE (u)-[:DISLIKES]->(i)",
                        parameters("userName", userName, "interestName", interestName));
                return "Dislike added";
            });
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String removeLike(String userName, String interestName, String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.writeTransaction(tx -> {
                tx.run("MATCH (u:User {name: $userName})-[r:LIKES]->(i:Interest {name: $interestName}) " +
                        "DELETE r",
                        parameters("userName", userName, "interestName", interestName));
                return "Like removed";
            });
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String removeDislike(String userName, String interestName, String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.writeTransaction(tx -> {
                tx.run("MATCH (u:User {name: $userName})-[r:DISLIKES]->(i:Interest {name: $interestName}) " +
                        "DELETE r",
                        parameters("userName", userName, "interestName", interestName));
                return "Dislike removed";
            });
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public LinkedList<String> connectUsersBasedOnLikes(String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (u1:User)-[:LIKES]->(i:Interest)<-[:LIKES]-(u2:User) " +
                        "WHERE u1 <> u2 " +
                        "MERGE (u1)-[:CONNECTED]->(u2) " +
                        "RETURN DISTINCT u1.name, u2.name");
                LinkedList<String> connections = new LinkedList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    connections.add(
                            record.get("u1.name").asString() + " is connected to " + record.get("u2.name").asString());
                }
                return connections;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LinkedList<String> disconnectUsersBasedOnDislikes(String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.writeTransaction(tx -> {
                Result result = tx.run("MATCH (u1:User)-[:DISLIKES]->(i:Interest)<-[:DISLIKES]-(u2:User) " +
                        "WHERE u1 <> u2 " +
                        "MERGE (u1)-[:DISCONNECTED]->(u2) " +
                        "RETURN DISTINCT u1.name, u2.name");
                LinkedList<String> disconnections = new LinkedList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    disconnections.add(record.get("u1.name").asString() + " is disconnected from "
                            + record.get("u2.name").asString());
                }
                return disconnections;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String createUser(String name, String password, String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.writeTransaction(tx -> {
                tx.run("MERGE (u:User {name: $name, password: $password})",
                        parameters("name", name, "password", password));
                return "User created";
            });
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // paswrod
    public String getUserPassword(String name, String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u:User {name: $name}) RETURN u.password",
                        parameters("name", name));
                if (result.hasNext()) {
                    return result.single().get("u.password").asString();
                } else {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
