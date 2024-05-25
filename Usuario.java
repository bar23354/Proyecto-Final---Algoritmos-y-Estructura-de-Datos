import java.util.List;

public class Usuario {
    private String id;
    private String nombre;
    private String correo;
    private String contrasena;
    private List<String> gustos;

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public List<String> getGustos() {
        return gustos;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setGustos(List<String> gustos) {
        this.gustos = gustos;
    }

}
