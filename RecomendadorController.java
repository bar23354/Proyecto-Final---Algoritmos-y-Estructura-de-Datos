import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
class RecomendadorController {
    private final Recomendador recomendador;

    public RecomendadorController() {
        this.recomendador = new Recomendador("bolt://localhost:7687", "neo4j", "password");
    }

    @PostMapping("/registrar")
    public void registrarUsuario(@RequestBody Usuario usuario) {
        recomendador.registrarUsuario(usuario.getNombre(), usuario.getCorreo(), usuario.getContrasena(), usuario.getGustos(), usuario.getDisgustos());
    }

    @PostMapping("/iniciar-sesion")
    public boolean iniciarSesion(@RequestBody Usuario usuario) {
        return recomendador.iniciarSesion(usuario.getCorreo(), usuario.getContrasena());
    }

    @GetMapping("/recomendaciones/{id}")
    public List<Perfil> obtenerRecomendaciones(@PathVariable String id) {
        return recomendador.generarRecomendacionesHibridas(id);
    }
}