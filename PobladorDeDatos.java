import org.neo4j.driver.Session;

public class PobladorDeDatos {
    private final Neo4jConnection conexion;

    public PobladorDeDatos(Neo4jConnection conexion) {
        this.conexion = conexion;
    }

    public void poblar() {
        try (Session session = conexion.getSession()) {
            session.run("CREATE (u:Usuario {id: '1', nombre: 'Juan', correo: 'juan@example.com'})");
            session.run("CREATE (g:Gusto {nombre: 'cine'})");
            session.run("MATCH (u:Usuario {id: '1'}), (g:Gusto {nombre: 'cine'}) " +
                        "CREATE (u)-[:INTERESADO_EN]->(g)");
        }
    }
}
