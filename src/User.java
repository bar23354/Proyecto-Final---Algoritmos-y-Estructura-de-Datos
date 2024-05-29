package src;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Esta clase representa a un usuario con su nombre de usuario, contraseña, gustos y disgustos.
 */
public class User {
    private String username;
    private String password;
    private Map<String, Set<String>> likes;
    private Map<String, Set<String>> dislikes;

    /**
     * crea un nuevo usuario con el nombre de usuario y contraseña especificados
     *
     * @param username El nombre de usuario.
     * @param password La contraseña del usuario.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.likes = new HashMap<>();
        this.dislikes = new HashMap<>();
    }

    /**
     * obtiene el nombre de usuario
     *
     * @return El nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * obtiene la contraseña del usuario
     *
     * @return La contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * obtiene los gustos del usuario
     *
     * @return Un mapa de categorías a conjuntos de gustos.
     */
    public Map<String, Set<String>> getLikes() {
        return likes;
    }

    /**
     * obtiene los disgustos del usuario
     *
     * @return Un mapa de categorías a conjuntos de disgustos.
     */
    public Map<String, Set<String>> getDislikes() {
        return dislikes;
    }

    /**
     * añade un gusto a la categoría especificada para el usuario
     *
     * @param category La categoría del gusto.
     * @param like     El gusto específico.
     */
    public void addLike(String category, String like) {
        likes.computeIfAbsent(category, k -> new HashSet<>()).add(like);
    }

    /**
     * añade un disgusto a la categoría especificada para el usuario
     *
     * @param category La categoría del disgusto.
     * @param dislike  El disgusto específico.
     */
    public void addDislike(String category, String dislike) {
        dislikes.computeIfAbsent(category, k -> new HashSet<>()).add(dislike);
    }
}
