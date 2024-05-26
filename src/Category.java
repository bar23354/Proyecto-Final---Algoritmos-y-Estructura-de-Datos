package src;

import java.util.HashSet;
import java.util.Set;

public class Category {
    private Set<String> options;

    public Category(String... options) {
        this.options = new HashSet<>();
        for (String option : options) {
            this.options.add(option.toLowerCase());
        }
    }

    public boolean isValidOption(String option) {
        return options.contains(option.toLowerCase());
    }

    public Set<String> getOptions() {
        return options;
    }
}
