package src;

import java.util.HashSet;
import java.util.Set;

/**
 * esta clase representa una categoría con un conjunto de opciones válidas
 */
public class Category {
    private Set<String> options;

    /**
     * crea una nueva categoria con las opciones especificadas
     *
     * @param options Las opciones válidas para esta categoria.
     */
    public Category(String... options) {
        this.options = new HashSet<>();
        for (String option : options) {
            this.options.add(option.toLowerCase());
        }
    }

    /**
     * verifica si una opción es válida para esta categoria
     *
     * @param option La opción a verificar.
     * @return {@code true} si la opción es válida, {@code false} en caso contrario.
     */
    public boolean isValidOption(String option) {
        return options.contains(option.toLowerCase());
    }

    /**
     * obtiene el conjunto de opciones válidas para esta categoria
     *
     * @return Un conjunto de opciones válidas.
     */
    public Set<String> getOptions() {
        return options;
    }
}
