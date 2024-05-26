package src;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String name;
    private Map<String, Interest> likes;
    private Map<String, Interest> dislikes;
    private Map<String, Category> categories;
    private String signIn; // Variable para almacenar el inicio de sesión

    public User(String name) {
        this.name = name;
        this.likes = new HashMap<>();
        this.dislikes = new HashMap<>();
        this.categories = new HashMap<>();
        // Definir las opciones válidas para cada categoría
        categories.put("pasatiempo", new Category("fútbol", "lectura", "pintura", "música", "viajes"));
        categories.put("principios y valores", new Category("honestidad", "respeto", "tolerancia", "solidaridad"));
        categories.put("educación", new Category("matemáticas", "historia", "literatura", "ciencias"));
        categories.put("religión", new Category("cristianismo", "islam", "budismo", "hinduismo"));
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return name;
    }

    public void setSignIn(String signIn) {
        this.signIn = signIn;
    }

    public Map<String, Interest> getLikes() {
        return likes;
    }

    public Map<String, Interest> getDislikes() {
        return dislikes;
    }

    public void addLike(String uri, String user, String password, String databaseName, String userName, String interest, String like) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            List<String> availableCategories = db.getAvailableCategories(userName, databaseName);
            System.out.println("Categorías disponibles:");
            for (String category : availableCategories) {
                System.out.println(category);
            }
            if (availableCategories.contains(interest)) {
                String result = db.addLike(userName, interest, databaseName);
                System.out.println(result);
            } else {
                System.out.println("Categoría inválida: " + interest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDislike(String uri, String user, String password, String databaseName, String userName, String interest, String dislike) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            List<String> availableCategories = db.getAvailableCategories(userName, databaseName);
            System.out.println("Categorías disponibles:");
            for (String category : availableCategories) {
                System.out.println(category);
            }
            if (availableCategories.contains(interest)) {
                String result = db.addDislike(userName, interest, databaseName);
                System.out.println(result);
            } else {
                System.out.println("Categoría inválida: " + interest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    

}
