import java.util.List;

public class Perfil {
    private String id;
    private String nombre;
    private List<String> intereses;
    private List<String> desintereses;

    // Constructor
    public Perfil(String id, String nombre, List<String> intereses, List<String> desintereses) {
        this.id = id;
        this.nombre = nombre;
        this.intereses = intereses;
        this.desintereses = desintereses;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getIntereses() {
        return intereses;
    }

    public void setIntereses(List<String> intereses) {
        this.intereses = intereses;
    }

    public List<String> getDesintereses() {
        return desintereses;
    }

    public void setDesintereses(List<String> desintereses) {
        this.desintereses = desintereses;
    }
}
