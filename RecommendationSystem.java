/**
 * Universidad del Valle de Guatemala
 * @author Isabella Obando, 23074
 * @author Mia Alejandra Fuentes Merida, 23775
 * @author Roberto Barreda, 23354
 * @description Clase principal de la aplicación que interactúa con una base de datos Neo4j para
 * @date creación 23/05/2024 última modificación 28/05/2024
 */

import java.util.List;
import java.util.Map;

/**
 * Clase que maneja el sistema de recomendaciones utilizando una conexión a Neo4j.
 */
public class RecommendationSystem {
    private Neo4jConnection neo4jConnection;

    /**
     * Constructor para crear una nueva instancia del sistema de recomendaciones.
     *
     * @param uri La URI de conexión a la base de datos Neo4j.
     * @param user El nombre de usuario para la autenticación.
     * @param password La contraseña para la autenticación.
     */
    public RecommendationSystem(String uri, String user, String password) {
        this.neo4jConnection = new Neo4jConnection(uri, user, password);
    }

    /**
     * Agrega un nuevo usuario al sistema de recomendaciones.
     *
     * @param user El usuario a agregar.
     */
    public void addUser(User user) {
        neo4jConnection.addUser(user);
    }

    /**
     * Elimina un usuario del sistema de recomendaciones.
     *
     * @param username El nombre de usuario a eliminar.
     */
    public void removeUser(String username) {
        neo4jConnection.removeUser(username);
    }

    /**
     * Obtiene un usuario del sistema de recomendaciones por su nombre de usuario.
     *
     * @param username El nombre de usuario.
     * @return El usuario correspondiente o null si no existe.
     */
    public User getUser(String username) {
        return neo4jConnection.getUser(username);
    }

    /**
     * Obtiene recomendaciones de usuarios basadas en los gustos y deal breakers del usuario actual.
     *
     * @param user El usuario para el cual se obtienen recomendaciones.
     * @return Una lista de mapas con las recomendaciones de usuarios.
     */
    public List<Map<String, Object>> getRecommendations(User user) {
        return neo4jConnection.getRecommendations(user);
    }

    /**
     * Agrega un interés a un usuario en el sistema de recomendaciones.
     *
     * @param username El nombre de usuario.
     * @param interest El interés a agregar.
     */
    public void addInterest(String username, String interest) {
        neo4jConnection.addInterest(username, interest);
    }
}