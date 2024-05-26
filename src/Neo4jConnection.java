package src;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.LinkedList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

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

    public String createUser(String name, String password, String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.writeTransaction(tx -> {
                tx.run("CREATE (u:User {name: $name, password: $password})",
                        parameters("name", name, "password", password));
                return "User created";
            });
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String getUserPassword(String name, String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u:User {name: $name}) RETURN u.password",
                        parameters("name", name));
                if (result.hasNext()) {
                    return result.single().get(0).asString();
                } else {
                    return null;
                }
            });
        } catch (Exception e) {
            return null;
        }
    }

    public LinkedList<String> connectUsersBasedOnLikes(String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u1:User)-[:LIKES]->(i:Interest)<-[:LIKES]-(u2:User) " +
                        "WHERE u1 <> u2 " +
                        "RETURN u1.name, u2.name");
                LinkedList<String> connections = new LinkedList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    connections.add(record.get(0).asString() + " - " + record.get(1).asString());
                }
                return connections;
            });
        } catch (Exception e) {
            return null;
        }
    }

    public LinkedList<String> disconnectUsersBasedOnDislikes(String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u1:User)-[:DISLIKES]->(i:Interest)<-[:DISLIKES]-(u2:User) " +
                        "WHERE u1 <> u2 " +
                        "RETURN u1.name, u2.name");
                LinkedList<String> disconnections = new LinkedList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    disconnections.add(record.get(0).asString() + " - " + record.get(1).asString());
                }
                return disconnections;
            });
        } catch (Exception e) {
            return null;
        }
    }

    public LinkedList<String> getCompatibleUsers(String userName, String databaseName, boolean sortByLikes) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u1:User {name: $userName})-[:LIKES]->(i:Interest)<-[:LIKES]-(u2:User) " +
                                "WHERE u1 <> u2 " +
                                "RETURN u2.name, COUNT(i) AS commonInterests " +
                                "ORDER BY commonInterests " + (sortByLikes ? "DESC" : "ASC"),
                        parameters("userName", userName));
                LinkedList<String> compatibleUsers = new LinkedList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    compatibleUsers.add(record.get(0).asString() + " - " + record.get(1).asString());
                }
                return compatibleUsers;
            });
        } catch (Exception e) {
            return null;
        }
    }
    
    public List<String> getAvailableCategories(String userName, String databaseName) {
        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (u:User {name: $userName})-[:LIKES]->(i:Interest)-[:OF_CATEGORY]->(c:Category) " +
                                "RETURN DISTINCT c.name",
                        parameters("userName", userName));
                List<String> categories = new LinkedList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    categories.add(record.get("c.name").asString());
                }
                return categories;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
