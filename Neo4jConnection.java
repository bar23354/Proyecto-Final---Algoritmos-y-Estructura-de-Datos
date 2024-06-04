/**
 * Universidad del Valle de Guatemala
 * @author Isabella Obando, 23074
 * @author Mia Alejandra Fuentes Merida, 23775
 * @author Roberto Barreda, 23354
 * @description Clase principal de la aplicación que interactúa con una base de datos Neo4j para
 * @date creación 23/05/2024 última modificación 28/05/2024
 */

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.neo4j.driver.exceptions.NoSuchRecordException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Clase que maneja la conexión y las operaciones con la base de datos Neo4j.
 */
public class Neo4jConnection implements AutoCloseable {
    private final Driver driver;

    /**
     * Constructor para crear una nueva conexión con la base de datos Neo4j.
     *
     * @param uri La URI de conexión a la base de datos.
     * @param user El nombre de usuario para la autenticación.
     * @param password La contraseña para la autenticación.
     */
    public Neo4jConnection(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    /**
     * Cierra la conexión con la base de datos.
     */
    @Override
    public void close() {
        driver.close();
    }

    /**
     * Agrega un nuevo usuario a la base de datos.
     *
     * @param user El usuario a agregar.
     */
    public void addUser(User user) {
        if (userExists(user.getUsername())) {
            System.out.println("El usuario ya existe. Por favor, elija un nombre de usuario diferente.");
            return;
        }

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                "CREATE (u:User {username: $username, password: $password, tipoDeRelacion: $tipoDeRelacion, sexualidad: $sexualidad, sexo: $sexo})",
                Values.parameters(
                    "username", user.getUsername(),
                    "password", user.getPassword(),
                    "tipoDeRelacion", user.getDealBreakers().get("tipo de relación"),
                    "sexualidad", user.getDealBreakers().get("sexualidad"),
                    "sexo", user.getDealBreakers().get("sexo")
                )
            ));

            for (String like : user.getLikes()) {
                addInterest(user.getUsername(), like);
            }
            
            for (String dislike : user.getDislikes()) {
                addDealBreaker(user.getUsername(), dislike);
            }
        }
    }

    /**
     * Verifica si un usuario ya existe en la base de datos.
     *
     * @param username El nombre de usuario a verificar.
     * @return true si el usuario existe, false en caso contrario.
     */
    public boolean userExists(String username) {
        try (Session session = driver.session()) {
            Record record = session.readTransaction(tx -> tx.run(
                "MATCH (u:User {username: $username}) RETURN u.username AS username",
                Values.parameters("username", username)).single());
            return record != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Agrega un interés a un usuario en la base de datos.
     *
     * @param username El nombre de usuario.
     * @param interest El interés a agregar.
     */
    public void addInterest(String username, String interest) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                "MATCH (u:User {username: $username}) " +
                "MERGE (i:Interest {name: $interest}) " +
                "MERGE (u)-[:LIKES]->(i)",
                Values.parameters("username", username, "interest", interest)));
        }
    }

    /**
     * Agrega un deal breaker a un usuario en la base de datos.
     *
     * @param username El nombre de usuario.
     * @param dealBreaker El deal breaker a agregar.
     */
    public void addDealBreaker(String username, String dealBreaker) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                "MATCH (u:User {username: $username}) " +
                "MERGE (d:DealBreaker {name: $dealBreaker}) " +
                "MERGE (u)-[:DISLIKES]->(d)",
                Values.parameters("username", username, "dealBreaker", dealBreaker)));
        }
    }

    /**
     * Obtiene un usuario de la base de datos por su nombre de usuario.
     *
     * @param username El nombre de usuario.
     * @return El usuario correspondiente o null si no existe.
     */
    public User getUser(String username) {
        try (Session session = driver.session()) {
            Record record = session.readTransaction(tx -> tx.run(
                "MATCH (u:User {username: $username}) " +
                "RETURN u.username AS username, u.password AS password, " +
                "u.tipoDeRelacion AS tipoDeRelacion, u.sexualidad AS sexualidad, u.sexo AS sexo",
                Values.parameters("username", username)).single());

            User user = new User(record.get("username").asString(), record.get("password").asString());
            user.addDealBreaker("tipo de relación", record.get("tipoDeRelacion").asString());
            user.addDealBreaker("sexualidad", record.get("sexualidad").asString());
            user.addDealBreaker("sexo", record.get("sexo").asString());

            List<Record> interests = session.readTransaction(tx -> tx.run(
                "MATCH (u:User {username: $username})-[:LIKES]->(i:Interest) " +
                "RETURN i.name AS name",
                Values.parameters("username", username)).list());

            for (Record interestRecord : interests) {
                user.addLike(interestRecord.get("name").asString());
            }

            List<Record> dealBreakers = session.readTransaction(tx -> tx.run(
                "MATCH (u:User {username: $username})-[:DISLIKES]->(d:DealBreaker) " +
                "RETURN d.name AS name",
                Values.parameters("username", username)).list());

            for (Record dealBreakerRecord : dealBreakers) {
                user.addDislike(dealBreakerRecord.get("name").asString());
            }

            return user;
        } catch (NoSuchRecordException e) {
            return null;
        }
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param username El nombre de usuario.
     */
    public void removeUser(String username) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run("MATCH (u:User {username: $username}) DETACH DELETE u",
                Values.parameters("username", username)));
        }
    }

    /**
     * Obtiene recomendaciones de usuarios basadas en los gustos y deal breakers del usuario actual.
     *
     * @param user El usuario para el cual se obtienen recomendaciones.
     * @return Una lista de mapas con las recomendaciones de usuarios.
     */
    public List<Map<String, Object>> getRecommendations(User user) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                List<Map<String, Object>> recommendations = new ArrayList<>();
                String query = "MATCH (u:User) WHERE u.username <> $username RETURN u.username AS username";
                List<Record> result = tx.run(query, Values.parameters("username", user.getUsername())).list();
                for (Record record : result) {
                    String username = record.get("username").asString();
                    User candidate = getUser(username);
                    if (matchesDealBreakers(user, candidate) && !hasDealBreakerConflict(user, candidate)) {
                        Set<String> commonInterests = getCommonInterests(user, candidate);
                        if (!commonInterests.isEmpty() && user.getDealBreakers().get("tipo de relación").equalsIgnoreCase(candidate.getDealBreakers().get("tipo de relación"))) {
                            Map<String, Object> recommendation = new HashMap<>();
                            recommendation.put("username", candidate.getUsername());
                            recommendation.put("commonInterests", commonInterests);
                            recommendations.add(recommendation);
                        }
                    }
                }
                return recommendations;
            });
        }
    }

    /**
     * Verifica si los deal breakers de un usuario coinciden con los de un candidato.
     *
     * @param user El usuario.
     * @param candidate El candidato a verificar.
     * @return true si los deal breakers coinciden, false en caso contrario.
     */
    private boolean matchesDealBreakers(User user, User candidate) {
        String userSexuality = user.getDealBreakers().get("sexualidad");
        String userGender = user.getDealBreakers().get("sexo");
        String candidateSexuality = candidate.getDealBreakers().get("sexualidad");
        String candidateGender = candidate.getDealBreakers().get("sexo");

        // Verifica compatibilidad heterosexual
        if (userSexuality.equals("heterosexual") && candidateSexuality.equals("heterosexual")) {
            if (userGender.equals("masculino") && candidateGender.equals("femenino")) return true;
            if (userGender.equals("femenino") && candidateGender.equals("masculino")) return true;
        }

        // Verifica compatibilidad homosexual
        if (userSexuality.equals("homosexual") && candidateSexuality.equals("homosexual")) {
            return userGender.equals(candidateGender);
        }

        // Verifica compatibilidad bisexual
        if (userSexuality.equals("bisexual")) {
            if (candidateSexuality.equals("bisexual")) return true;
            if (userGender.equals("masculino") && candidateGender.equals("femenino") && candidateSexuality.equals("heterosexual")) return true;
            if (userGender.equals("femenino") && candidateGender.equals("masculino") && candidateSexuality.equals("heterosexual")) return true;
            if (userGender.equals(candidateGender) && candidateSexuality.equals("homosexual")) return true;
        }

        return false;
    }

    /**
     * Verifica si hay un conflicto de deal breakers entre dos usuarios.
     *
     * @param user El usuario.
     * @param candidate El candidato a verificar.
     * @return true si hay un conflicto, false en caso contrario.
     */
    private boolean hasDealBreakerConflict(User user, User candidate) {
        for (String userDislike : user.getDislikes()) {
            if (candidate.getLikes().contains(userDislike)) {
                return true;
            }
        }
        for (String candidateDislike : candidate.getDislikes()) {
            if (user.getLikes().contains(candidateDislike)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene los intereses comunes entre dos usuarios.
     *
     * @param user El usuario.
     * @param candidate El candidato a verificar.
     * @return Un conjunto de intereses comunes.
     */
    private Set<String> getCommonInterests(User user, User candidate) {
        Set<String> commonInterests = new HashSet<>(user.getLikes());
        commonInterests.retainAll(candidate.getLikes());
        return commonInterests;
    }
}