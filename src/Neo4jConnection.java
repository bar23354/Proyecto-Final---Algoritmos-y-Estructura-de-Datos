package src;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import java.util.LinkedList;

public class Neo4jConnection implements AutoCloseable {
    private final Driver driver;

    public Neo4jConnection(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public void close() {
        driver.close();
    }

    public String createUser(String name, String password, String databaseName) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE (n:User {name: $name, password: $password})", 
                       org.neo4j.driver.Values.parameters("name", name, "password", password));
                return null;
            });
            return "Usuario creado exitosamente.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al crear el usuario.";
        }
    }

    public String getUserPassword(String name, String databaseName) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                var result = tx.run("MATCH (n:User {name: $name}) RETURN n.password AS password", 
                                    org.neo4j.driver.Values.parameters("name", name));
                if (result.hasNext()) {
                    return result.single().get("password").asString();
                } else {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String addLike(String userName, String category, String like, String databaseName) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User {name: $userName}) " +
                       "MERGE (l:Interest {name: $like}) " +
                       "MERGE (u)-[:LIKES]->(l)", 
                       org.neo4j.driver.Values.parameters("userName", userName, "category", category, "like", like));
                return null;
            });
            return "Gusto añadido exitosamente.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al añadir el gusto.";
        }
    }

    public String addDislike(String userName, String category, String dislike, String databaseName) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User {name: $userName}) " +
                       "MERGE (d:Interest {name: $dislike}) " +
                       "MERGE (u)-[:DISLIKES]->(d)", 
                       org.neo4j.driver.Values.parameters("userName", userName, "category", category, "dislike", dislike));
                return null;
            });
            return "Disgusto añadido exitosamente.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al añadir el disgusto.";
        }
    }

    public LinkedList<String> connectUsersBasedOnLikes(String databaseName) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                var result = tx.run("MATCH (u:User)-[:LIKES]->(l:Interest) " +
                                    "RETURN u.name AS user, collect(l.name) AS likes " +
                                    "ORDER BY size(collect(l.name)) DESC");
                LinkedList<String> connections = new LinkedList<>();
                while (result.hasNext()) {
                    var record = result.next();
                    connections.add(record.get("user").asString() + ": " + record.get("likes").asList());
                }
                return connections;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public LinkedList<String> disconnectUsersBasedOnDislikes(String databaseName) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                var result = tx.run("MATCH (u:User)-[:DISLIKES]->(d:Interest) " +
                                    "RETURN u.name AS user, collect(d.name) AS dislikes " +
                                    "ORDER BY size(collect(d.name)) DESC");
                LinkedList<String> disconnections = new LinkedList<>();
                while (result.hasNext()) {
                    var record = result.next();
                    disconnections.add(record.get("user").asString() + ": " + record.get("dislikes").asList());
                }
                return disconnections;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public String deleteUser(String userName, String databaseName) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MATCH (u:User {name: $userName}) DETACH DELETE u", 
                       org.neo4j.driver.Values.parameters("userName", userName));
                return null;
            });
            return "Usuario eliminado exitosamente.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al eliminar el usuario.";
        }
    }
}
