import org.neo4j.driver.Session;
import org.neo4j.driver.Result;

import java.util.ArrayList;
import java.util.List;

public class Recomendador {
    private final Neo4jConnection conexion;

    public Recomendador(Neo4jConnection conexion) {
        this.conexion = conexion;
    }

    public List<Perfil> generarRecomendaciones(String usuarioId) {
        List<Perfil> recomendaciones = new ArrayList<>();
        try (Session session = conexion.getSession()) {
            Result result = session.run(
                "MATCH (u:Usuario)-[:INTERESADO_EN]->(g:Gusto)<-[:INTERESADO_EN]-(re:Usuario) " +
                "WHERE u.id = $id " +
                "RETURN re.id AS id, re.nombre AS nombre", 
                parameters("id", usuarioId));
            while (result.hasNext()) {
                Record record = result.next();
                Perfil perfil = new Perfil();
                perfil.setId(record.get("id").asString());
                perfil.setIntereses(getIntereses(record.get("id").asString()));
                recomendaciones.add(perfil);
            }
        }
        return recomendaciones;
    }

    private List<String> getIntereses(String perfilId) {
        List<String> intereses = new ArrayList<>();
        try (Session session = conexion.getSession()) {
            Result result = session.run(
                "MATCH (p:Perfil {id: $id})-[:INTERESADO_EN]->(g:Gusto) " +
                "RETURN g.nombre AS gusto",
                parameters("id", perfilId));
            while (result.hasNext()) {
                intereses.add(result.next().get("gusto").asString());
            }
        }
        return intereses;
    }
}
