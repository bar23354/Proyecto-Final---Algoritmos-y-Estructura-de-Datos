import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Value;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Set;
import java.util.UUID;

public class Recomendador {
    private final Driver driver;

    public Recomendador(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void close() {
        driver.close();
    }

    // Método para registrar un nuevo usuario
    public void registrarUsuario(String nombre, String correo, String contrasena, List<String> gustos, List<String> disgustos) {
        String idUsuario = UUID.randomUUID().toString();
        String contrasenaCifrada = cifrarContrasena(contrasena);

        try (Session session = driver.session()) {
            session.run(
                "CREATE (u:Usuario {id: $id, nombre: $nombre, correo: $correo, contrasena: $contrasena})",
                Values.parameters("id", idUsuario, "nombre", nombre, "correo", correo, "contrasena", contrasenaCifrada)
            );

            for (String gusto : gustos) {
                session.run(
                    "MATCH (u:Usuario {id: $id}) " +
                    "MERGE (g:Gusto {nombre: $gusto}) " +
                    "CREATE (u)-[:INTERESADO_EN]->(g)",
                    Values.parameters("id", idUsuario, "gusto", gusto)
                );
            }

            for (String disgusto : disgustos) {
                session.run(
                    "MATCH (u:Usuario {id: $id}) " +
                    "MERGE (d:Disgusto {nombre: $disgusto}) " +
                    "CREATE (u)-[:NO_INTERESADO_EN]->(d)",
                    Values.parameters("id", idUsuario, "disgusto", disgusto)
                );
            }
        }
    }

    // Método para iniciar sesión
    public boolean iniciarSesion(String correo, String contrasena) {
        try (Session session = driver.session()) {
            Result result = session.run(
                "MATCH (u:Usuario {correo: $correo}) RETURN u.contrasena AS contrasena, u.id AS id",
                Values.parameters("correo", correo)
            );

            if (result.hasNext()) {
                Value record = result.next().get("contrasena");
                String contrasenaCifrada = record.asString();
                if (verificarContrasena(contrasena, contrasenaCifrada)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Métodos de cifrado y verificación de contraseña (simulados aquí)
    private String cifrarContrasena(String contrasena) {
        // Implementar cifrado de contraseña
        return contrasena; // Simulación, se debe usar un cifrado real
    }

    private boolean verificarContrasena(String contrasena, String contrasenaCifrada) {
        // Implementar verificación de contraseña
        return contrasena.equals(contrasenaCifrada); // Simulación, se debe usar verificación real
    }

    // Método para actualizar usuario
    public void actualizarUsuario(String idUsuario, String nombre, String correo, List<String> gustos, List<String> disgustos) {
        try (Session session = driver.session()) {
            session.run(
                "MATCH (u:Usuario {id: $id}) " +
                "SET u.nombre = $nombre, u.correo = $correo",
                Values.parameters("id", idUsuario, "nombre", nombre, "correo", correo)
            );

            // Actualizar gustos
            session.run(
                "MATCH (u:Usuario {id: $id})-[r:INTERESADO_EN]->(g:Gusto) DELETE r",
                Values.parameters("id", idUsuario)
            );

            for (String gusto : gustos) {
                session.run(
                    "MATCH (u:Usuario {id: $id}) " +
                    "MERGE (g:Gusto {nombre: $gusto}) " +
                    "CREATE (u)-[:INTERESADO_EN]->(g)",
                    Values.parameters("id", idUsuario, "gusto", gusto)
                );
            }

            // Actualizar disgustos
            session.run(
                "MATCH (u:Usuario {id: $id})-[r:NO_INTERESADO_EN]->(d:Disgusto) DELETE r",
                Values.parameters("id", idUsuario)
            );

            for (String disgusto : disgustos) {
                session.run(
                    "MATCH (u:Usuario {id: $id}) " +
                    "MERGE (d:Disgusto {nombre: $disgusto}) " +
                    "CREATE (u)-[:NO_INTERESADO_EN]->(d)",
                    Values.parameters("id", idUsuario, "disgusto", disgusto)
                );
            }
        }
    }

    // Método para eliminar un usuario
    public void eliminarUsuario(String idUsuario) {
        try (Session session = driver.session()) {
            session.run(
                "MATCH (u:Usuario {id: $id}) DETACH DELETE u",
                Values.parameters("id", idUsuario)
            );
        }
    }

    // Método para generar recomendaciones híbridas
    public List<Perfil> generarRecomendacionesHibridas(String usuarioId) {
        List<Perfil> recomendacionesColaborativas = generarRecomendacionesColaborativas(usuarioId);
        List<Perfil> recomendacionesBasadasEnContenido = generarRecomendacionesBasadasEnContenido(usuarioId);

        Set<Perfil> recomendacionesUnicas = new HashSet<>(recomendacionesColaborativas);
        recomendacionesUnicas.addAll(recomendacionesBasadasEnContenido);

        return new ArrayList<>(recomendacionesUnicas);
    }

    // Implementación básica de recomendaciones colaborativas
    private List<Perfil> generarRecomendacionesColaborativas(String usuarioId) {
    private final Driver driver;

    public Recomendador(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public void close() {
        driver.close();
    }

    // Método para registrar un nuevo usuario
    public void registrarUsuario(String nombre, String correo, String contrasena, List<String> gustos, List<String> disgustos) {
        String idUsuario = UUID.randomUUID().toString();

        try (Session session = driver.session()) {
            session.run(
                "CREATE (u:Usuario {id: $id, nombre: $nombre, correo: $correo, contrasena: $contrasena})",
                Values.parameters("id", idUsuario, "nombre", nombre, "correo", correo, "contrasena")
            );

            for (String gusto : gustos) {
                session.run(
                    "MATCH (u:Usuario {id: $id}) " +
                    "MERGE (g:Gusto {nombre: $gusto}) " +
                    "CREATE (u)-[:INTERESADO_EN]->(g)",
                    Values.parameters("id", idUsuario, "gusto", gusto)
                );
            }

            for (String disgusto : disgustos) {
                session.run(
                    "MATCH (u:Usuario {id: $id}) " +
                    "MERGE (d:Disgusto {nombre: $disgusto}) " +
                    "CREATE (u)-[:NO_INTERESADO_EN]->(d)",
                    Values.parameters("id", idUsuario, "disgusto", disgusto)
                );
            }
        }
    }

    // Método para iniciar sesión
    public boolean iniciarSesion(String correo, String contrasena) {
        try (Session session = driver.session()) {
            Result result = session.run(
                "MATCH (u:Usuario {correo: $correo}) RETURN u.contrasena AS contrasena, u.id AS id",
                Values.parameters("correo", correo)
            );

            if (result.hasNext()) {
                Value record = result.next().get("contrasena");
                String contrasenaCifrada = record.asString();
                if (verificarContrasena(contrasena, contrasenaCifrada)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean verificarContrasena(String contrasena, String contrasenaCifrada) {
        //AYUDA AYUDA AYUDA: verificacion 
        return contrasena.equals(contrasenaCifrada); // Simulación
    }

    // Método para actualizar usuario
    public void actualizarUsuario(String idUsuario, String nombre, String correo, List<String> gustos, List<String> disgustos) {
        try (Session session = driver.session()) {
            session.run(
                "MATCH (u:Usuario {id: $id}) " +
                "SET u.nombre = $nombre, u.correo = $correo",
                Values.parameters("id", idUsuario, "nombre", nombre, "correo", correo)
            );

            // Actualizar gustos
            session.run(
                "MATCH (u:Usuario {id: $id})-[r:INTERESADO_EN]->(g:Gusto) DELETE r",
                Values.parameters("id", idUsuario)
            );

            for (String gusto : gustos) {
                session.run(
                    "MATCH (u:Usuario {id: $id}) " +
                    "MERGE (g:Gusto {nombre: $gusto}) " +
                    "CREATE (u)-[:INTERESADO_EN]->(g)",
                    Values.parameters("id", idUsuario, "gusto", gusto)
                );
            }

            // Actualizar disgustos
            session.run(
                "MATCH (u:Usuario {id: $id})-[r:NO_INTERESADO_EN]->(d:Disgusto) DELETE r",
                Values.parameters("id", idUsuario)
            );

            for (String disgusto : disgustos) {
                session.run(
                    "MATCH (u:Usuario {id: $id}) " +
                    "MERGE (d:Disgusto {nombre: $disgusto}) " +
                    "CREATE (u)-[:NO_INTERESADO_EN]->(d)",
                    Values.parameters("id", idUsuario, "disgusto", disgusto)
                );
            }
        }
    }

    // Método para eliminar un usuario
    public void eliminarUsuario(String idUsuario) {
        try (Session session = driver.session()) {
            session.run(
                "MATCH (u:Usuario {id: $id}) DETACH DELETE u",
                Values.parameters("id", idUsuario)
            );
        }
    }

    // Método para generar recomendaciones híbridas
    public List<Perfil> generarRecomendacionesHibridas(String usuarioId) {
        List<Perfil> recomendacionesColaborativas = generarRecomendacionesColaborativas(usuarioId);
        List<Perfil> recomendacionesBasadasEnContenido = generarRecomendacionesBasadasEnContenido(usuarioId);

        Set<Perfil> recomendacionesUnicas = new HashSet<>(recomendacionesColaborativas);
        recomendacionesUnicas.addAll(recomendacionesBasadasEnContenido);

        return new ArrayList<>(recomendacionesUnicas);
    }

    // Implementación básica de recomendaciones colaborativas
    private List<Perfil> generarRecomendacionesColaborativas(String usuarioId) {
        List<Perfil> recomendaciones = new ArrayList<>();
        // Lógica de filtrado colaborativo (simplificada)
        try (Session session = driver.session()) {
        // Lógica de filtrado colaborativo (simplificada)
        try (Session session = driver.session()) {
            Result result = session.run(
                "MATCH (u1:Usuario {id: $usuarioId})-[:INTERESADO_EN]->(g:Gusto)<-[:INTERESADO_EN]-(u2:Usuario), " +
                "(u2)-[:INTERESADO_EN]->(g2:Gusto) " +
                "WHERE u1 <> u2 " +
                "RETURN g2.nombre AS nombre, count(*) AS frecuencia " +
                "ORDER BY frecuencia DESC " +
                "LIMIT 5",
                Values.parameters("usuarioId", usuarioId)
            );

                "MATCH (u1:Usuario {id: $usuarioId})-[:INTERESADO_EN]->(g:Gusto)<-[:INTERESADO_EN]-(u2:Usuario), " +
                "(u2)-[:INTERESADO_EN]->(g2:Gusto) " +
                "WHERE u1 <> u2 " +
                "RETURN g2.nombre AS nombre, count(*) AS frecuencia " +
                "ORDER BY frecuencia DESC " +
                "LIMIT 5",
                Values.parameters("usuarioId", usuarioId)
            );

            while (result.hasNext()) {
                Value record = result.next().get("nombre");
                recomendaciones.add(new Perfil(UUID.randomUUID().toString(), record.asString(), new ArrayList<>(), new ArrayList<>()));
                Value record = result.next().get("nombre");
                recomendaciones.add(new Perfil(UUID.randomUUID().toString(), record.asString(), new ArrayList<>(), new ArrayList<>()));
            }
        }
        return recomendaciones;
    }

    // Implementación básica de recomendaciones basadas en contenido
    private List<Perfil> generarRecomendacionesBasadasEnContenido(String usuarioId) {
        List<Perfil> recomendaciones = new ArrayList<>();
        // Lógica de filtrado basado en contenido (simplificada)
        try (Session session = driver.session()) {
    // Implementación básica de recomendaciones basadas en contenido
    private List<Perfil> generarRecomendacionesBasadasEnContenido(String usuarioId) {
        List<Perfil> recomendaciones = new ArrayList<>();
        // Lógica de filtrado basado en contenido (simplificada)
        try (Session session = driver.session()) {
            Result result = session.run(
                "MATCH (u:Usuario {id: $usuarioId})-[:INTERESADO_EN]->(g:Gusto)<-[:INTERESADO_EN]-(p:Perfil) " +
                "RETURN p.id AS id, p.nombre AS nombre, collect(g.nombre) AS intereses " +
                "LIMIT 5",
                Values.parameters("usuarioId", usuarioId)
            );

                "MATCH (u:Usuario {id: $usuarioId})-[:INTERESADO_EN]->(g:Gusto)<-[:INTERESADO_EN]-(p:Perfil) " +
                "RETURN p.id AS id, p.nombre AS nombre, collect(g.nombre) AS intereses " +
                "LIMIT 5",
                Values.parameters("usuarioId", usuarioId)
            );

            while (result.hasNext()) {
                Value record = result.next().get("id");
                String id = record.asString();
                String nombre = result.next().get("nombre").asString();
                List<String> intereses = result.next().get("intereses").asList(Value::asString);
                recomendaciones.add(new Perfil(id, nombre, intereses, new ArrayList<>()));
                Value record = result.next().get("id");
                String id = record.asString();
                String nombre = result.next().get("nombre").asString();
                List<String> intereses = result.next().get("intereses").asList(Value::asString);
                recomendaciones.add(new Perfil(id, nombre, intereses, new ArrayList<>()));
            }
        }
        return recomendaciones;
        return recomendaciones;
    }
}
