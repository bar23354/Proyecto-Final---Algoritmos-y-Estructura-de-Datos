/**
 * Universidad del Valle de Guatemala
 * @author Isabella Obando, 23074
 * @author Mia Alejandra Fuentes Merida, 23775
 * @author Roberto Barreda, 23354
 * @description Clase principal de la aplicación que interactúa con una base de datos Neo4j para
 * @date creación 23/05/2024 última modificación 28/05/2024
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Clase que representa un usuario en el sistema de recomendaciones.
 */
public class User {
    private String username; // Nombre de usuario
    private String password; // Contraseña del usuario
    private Map<String, String> dealBreakers; // Mapa de deal breakers del usuario
    private Set<String> likes; // Conjunto de gustos del usuario
    private Set<String> dislikes; // Conjunto de disgustos del usuario

    /**
     * Constructor para crear un nuevo usuario.
     *
     * @param username El nombre de usuario.
     * @param password La contraseña del usuario.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.dealBreakers = new HashMap<>();
        this.likes = new HashSet<>();
        this.dislikes = new HashSet<>();
    }

    /**
     * Obtiene el nombre de usuario.
     *
     * @return El nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return La contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Obtiene los deal breakers del usuario.
     *
     * @return Un mapa con los deal breakers del usuario.
     */
    public Map<String, String> getDealBreakers() {
        return dealBreakers;
    }

    /**
     * Establece los deal breakers del usuario.
     *
     * @param dealBreakers Un mapa con los nuevos deal breakers del usuario.
     */
    public void setDealBreakers(Map<String, String> dealBreakers) {
        this.dealBreakers = dealBreakers;
    }

    /**
     * Obtiene los gustos del usuario.
     *
     * @return Un conjunto con los gustos del usuario.
     */
    public Set<String> getLikes() {
        return likes;
    }

    /**
     * Obtiene los disgustos del usuario.
     *
     * @return Un conjunto con los disgustos del usuario.
     */
    public Set<String> getDislikes() {
        return dislikes;
    }

    /**
     * Agrega un nuevo deal breaker al usuario.
     *
     * @param category La categoría del deal breaker.
     * @param value El valor del deal breaker.
     */
    public void addDealBreaker(String category, String value) {
        dealBreakers.put(category, value);
    }

    /**
     * Agrega un nuevo gusto al usuario.
     *
     * @param like El gusto a agregar.
     */
    public void addLike(String like) {
        likes.add(like);
    }

    /**
     * Agrega un nuevo disgusto al usuario.
     *
     * @param dislike El disgusto a agregar.
     */
    public void addDislike(String dislike) {
        dislikes.add(dislike);
    }
}