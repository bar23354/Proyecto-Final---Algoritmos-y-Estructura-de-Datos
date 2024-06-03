import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User {
    private String username;
    private String password;
    private Map<String, String> dealBreakers;
    private Map<String, Set<String>> likes;
    private Map<String, Set<String>> dislikes;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.dealBreakers = new HashMap<>();
        this.likes = new HashMap<>();
        this.dislikes = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, String> getDealBreakers() {
        return dealBreakers;
    }

    public Map<String, Set<String>> getLikes() {
        return likes;
    }

    public Map<String, Set<String>> getDislikes() {
        return dislikes;
    }

    public void addDealBreaker(String category, String value) {
        dealBreakers.put(category, value);
    }

    public void addLike(String category, String like) {
        likes.computeIfAbsent(category, k -> new HashSet<>()).add(like);
    }

    public void addDislike(String category, String dislike) {
        dislikes.computeIfAbsent(category, k -> new HashSet<>()).add(dislike);
    }
}
