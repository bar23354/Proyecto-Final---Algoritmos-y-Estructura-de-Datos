import java.util.List;

public class Perfil {
    private String id;
    private List<String> intereses;

    public String getId() {
        return id;
    }

    public List<String> getIntereses() {
        return intereses;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIntereses(List<String> intereses) {
        this.intereses = intereses;
    }
}
