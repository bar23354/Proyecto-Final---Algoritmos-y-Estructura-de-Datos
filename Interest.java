/**
 * Universidad del Valle de Guatemala
 * @author Isabella Obando, 23074
 * @author Mia Alejandra Fuentes Merida, 23775
 * @author Roberto Barreda, 23354
 * @description Clase principal de la aplicación que interactúa con una base de datos Neo4j para
 * @date creación 23/05/2024 última modificación 28/05/2024
 */

/**
 * Clase que representa un interés con una categoría y un nombre.
 */
public class Interest {
    private String category; // Categoría del interés
    private String name; // Nombre del interés

    /**
     * Constructor para crear un nuevo interés.
     *
     * @param category La categoría del interés.
     * @param name El nombre del interés.
     */
    public Interest(String category, String name) {
        this.category = category;
        this.name = name;
    }

    /**
     * Obtiene la categoría del interés.
     *
     * @return La categoría del interés.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Obtiene el nombre del interés.
     *
     * @return El nombre del interés.
     */
    public String getName() {
        return name;
    }
}
