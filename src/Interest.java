package src;

/**
 * Esta clase rpresenta un interés con una categoria y un nombre
 */
public class Interest {
    private String category;
    private String name;

    /**
     * Crea un nuevo interés con la categoria y el nombre especificados.
     *
     * @param category La categoria del interés.
     * @param name El nombre del interés.
     */
    public Interest(String category, String name) {
        this.category = category;
        this.name = name;
    }

    /**
     * Obtiene la categoria del interés.
     *
     * @return La categoria del interés.
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

