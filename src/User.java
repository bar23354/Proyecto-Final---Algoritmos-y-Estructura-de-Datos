package src;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User {
    private String username;
    private String password;
    private Map<String, Set<String>> likes;
    private Map<String, Set<String>> dislikes;

    public User(String username) {
        this.username = username;
        this.likes = new HashMap<>();
        this.dislikes = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public void setSignIn(String password) {
        this.password = password;
    }

    public Map<String, Set<String>> getLikes() {
        return likes;
    }

    public Map<String, Set<String>> getDislikes() {
        return dislikes;
    }

    public void addLike(String category, String like) {
        likes.computeIfAbsent(category, k -> new HashSet<>()).add(like);
    }

    public void addDislike(String category, String dislike) {
        dislikes.computeIfAbsent(category, k -> new HashSet<>()).add(dislike);
    }
}
