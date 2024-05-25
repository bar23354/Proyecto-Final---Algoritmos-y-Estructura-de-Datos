import java.util.List;

public class Usuario {
    private String id;
    private String nombre;
    private String correo;
    private String contrasena;
    private List<String> gustos;
    private List<String> disgustos;

    // Constructor
    public Usuario(String id, String nombre, String correo, String contrasena, List<String> gustos, List<String> disgustos) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.gustos = gustos;
        this.disgustos = disgustos;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public List<String> getGustos() {
        return gustos;
    }

    public void setGustos(List<String> gustos) {
        this.gustos = gustos;
    }

    public List<String> getDisgustos() {
        return disgustos;
    }

    public void setDisgustos(List<String> disgustos) {
        this.disgustos = disgustos;
    }
}
