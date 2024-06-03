/**
 * Universidad del Valle de Guatemala
 * @author Isabella Obando, 23074
 * @author Mia Alejandra Fuentes Merida, 23775
 * @author Roberto Barreda, 23354
 * @description Clase principal de la aplicación que interactúa con una base de datos Neo4j para
 * @date creación 23/05/2024 última modificación 28/05/2024
 */
package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

/**
 * manejar usuarios, gustos y disgustos y sus relaciones
 */

 public class Aplicacion {
    private static Scanner scanner = new Scanner(System.in);
    private static final String[] CATEGORIES = {"Deportes", "Cultura", "Religión", "Valores"};

    /**
     * Método principal que ejecuta la aplicacion
     *
     * @param args Argumentos de la linea de comandos
     */
    public static void main(String[] args) {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "password";
        String databaseName = "neo4j2";
        String name = null;
        Scanner scanner = new Scanner(System.in);

        Driver driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        // Carga de usuarios desde CSV
        try {
            List<User> users = loadUsersFromCSV("usuarios.csv", uri, user, password, databaseName);
            try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
                for (User currentUser : users) {
                    System.out.println("Usuario: " + currentUser.getUsername());
                    System.out.println("Likes:");
                    currentUser.getLikes().forEach((category, interests) -> {
                        System.out.println(category + ": " + interests);
                        interests.forEach(interest -> {
                            String result = db.addLike(currentUser.getUsername(), category, interest, databaseName);
                            if (result.startsWith("Error")) {
                                System.err.println(result);
                            }
                        });
                    });
                    System.out.println("Dislikes:");
                    currentUser.getDislikes().forEach((category, dislikes) -> {
                        System.out.println(category + ": " + dislikes);
                        dislikes.forEach(dislike -> {
                            String result = db.addDislike(currentUser.getUsername(), category, dislike, databaseName);
                            if (result.startsWith("Error")) {
                                System.err.println(result);
                            }
                        });
                    });
                }

                while (true) {
                    System.out.println("1. Crear usuario");
                    System.out.println("2. Añadir gustos y disgustos");
                    System.out.println("3. Ver conexiones basadas en gustos");
                    System.out.println("4. Ver desconexiones basadas en disgustos");
                    System.out.println("5. Eliminar usuario");
                    System.out.println("6. Salir");
                    System.out.print("Seleccione una opción: ");
                    int option = scanner.nextInt();
                    scanner.nextLine();

                    switch (option) {
                        case 1:
                            System.out.print("Ingrese el nombre del usuario: ");
                            name = scanner.nextLine();
                            System.out.print("Ingrese la contraseña del usuario: ");
                            String userPassword = scanner.nextLine();
                            String createUserResult = db.createUser(name, userPassword, databaseName, userPassword, userPassword);
                            System.out.println(createUserResult);
                            break;

                        case 2:
                            System.out.print("Ingrese el nombre del usuario: ");
                            String username = scanner.nextLine();
                            if (!db.userExists(username, databaseName)) {
                                System.out.println("El usuario no existe.");
                                break;
                            }
                            System.out.println("Seleccione una categoría:");
                            for (int i = 0; i < CATEGORIES.length; i++) {
                                System.out.println((i + 1) + ". " + CATEGORIES[i]);
                            }
                            int categoryIndex = scanner.nextInt() - 1;
                            scanner.nextLine(); // Consume newline
                            String category = CATEGORIES[categoryIndex];
                        
                            System.out.println("Ingrese el nombre del interés (anteponga '!' para disgusto): ");
                            String interest = scanner.nextLine();
                            String result;
                            if (interest.startsWith("!")) {
                                result = db.addDislike(username, category, interest.substring(1), databaseName);
                            } else {
                                result = db.addLike(username, category, interest, databaseName);
                            }
                            System.out.println(result);
                            break;                        

                        case 3:
                            LinkedList<String> connections = db.connectUsersBasedOnLikes(databaseName);
                            if (connections.isEmpty()) {
                                System.out.println("No compartes gustos con nadie. Aquí hay tres sugerencias:");
                                connections = db.getRandomUsers(3, databaseName);
                                for (String suggestion : connections) {
                                    System.out.println(suggestion);
                                }
                            } else {
                                for (String connection : connections) {
                                    System.out.println(connection);
                                }
                            }
                            break;

                        case 4:
                            LinkedList<String> disconnections = db.disconnectUsersBasedOnDislikes(databaseName);
                            if (disconnections.isEmpty()) {
                                System.out.println("No hay desconexiones basadas en disgustos.");
                            } else {
                                for (String disconnection : disconnections) {
                                    System.out.println(disconnection);
                                }
                            }
                            break;

                        case 5:
                            System.out.print("Ingrese el nombre del usuario a eliminar: ");
                            name = scanner.nextLine();
                            String deleteUserResult = db.deleteUser(name, databaseName);
                            System.out.println(deleteUserResult);
                            break;

                        case 6:
                            System.out.println("Saliendo del programa.");
                            scanner.close();
                            System.exit(0); // Cerrar el programa correctamente
                            break;

                        default:
                            System.out.println("Opción no válida.");
                    }
                }
            } catch (Exception e) {
                System.err.println("Error cerrando la conexión a Neo4j: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios desde CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene los datos de interes desde la entrada del usuario
     *
     * @return Un arreglo de Strings donde el primer elemento es la categoría y el segundo es el interés normalizado
     */
    private static String[] getInterestData() {
        System.out.println("Seleccione una categoría:");
        for (int i = 0; i < CATEGORIES.length; i++) {
            System.out.println((i + 1) + ". " + CATEGORIES[i]);
        }
        int categoryIndex = scanner.nextInt() - 1;
        scanner.nextLine(); // Consume newline
        String category = CATEGORIES[categoryIndex];

        System.out.println("Ingrese el nombre del interés: ");
        String interest = scanner.nextLine();

        return new String[]{category, normalizeString(interest)};
    }

    /**
     * permite al usuario registrarse en la aplicacion
     *
     * @param uri La URI de la base de datos.
     * @param user El nombre de usuario de la base de datos.
     * @param password La contraseña de la base de datos.
     * @param databaseName El nombre de la base de datos.
     */
    public void signin(String uri, String user, String password, String databaseName) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            System.out.println("Ingrese su nombre de usuario: ");
            String name = scanner.nextLine();
            System.out.println("Ingrese su contraseña: ");
            String pass = scanner.nextLine();
            String result = db.createUser(name, pass, databaseName, result, result);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * añade un gusto a un usuario en la base de datos
     *
     * @param uri La URI de la base de datos.
     * @param user El nombre de usuario de la base de datos.
     * @param password La contraseña de la base de datos.
     * @param databaseName El nombre de la base de datos.
     * @param userName El nombre de usuario al que se añadirá el gusto.
     * @param category La categoría del gusto.
     * @param like El gusto a añadir.
     */
    public void addLike(String uri, String user, String password, String databaseName, String userName, String category, String like) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            String result = db.addLike(userName, category, like, databaseName);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * agrega un disgusto a un usuario en la base de datos
     *
     * @param uri La URI de la base de datos.
     * @param user El nombre de usuario de la base de datos.
     * @param password La contraseña de la base de datos.
     * @param databaseName El nombre de la base de datos.
     * @param userName El nombre de usuario al que se añadirá el disgusto.
     * @param category La categoría del disgusto.
     * @param dislike El disgusto a añadir.
     */
    public void addDislike(String uri, String user, String password, String databaseName, String userName, String category, String dislike) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            String result = db.addDislike(userName, category, dislike, databaseName);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * conecta usuarios basándose en sus gustos
     *
     * @param uri La URI de la base de datos.
     * @param user El nombre de usuario de la base de datos.
     * @param password La contraseña de la base de datos.
     * @param databaseName El nombre de la base de datos.
     * @return Una lista de conexiones entre usuarios.
     */
    public LinkedList<String> connectUsersBasedOnLikes(String uri, String user, String password, String databaseName) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            return db.connectUsersBasedOnLikes(databaseName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * desconecta usuarios guiandose en sus disgustos
     *
     * @param uri La URI de la base de datos.
     * @param user El nombre de usuario de la base de datos.
     * @param password La contraseña de la base de datos.
     * @param databaseName El nombre de la base de datos.
     * @return Una lista de desconexiones entre usuarios.
     */
    public LinkedList<String> disconnectUsersBasedOnDislikes(String uri, String user, String password, String databaseName) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            return db.disconnectUsersBasedOnDislikes(databaseName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * inicia sesión en la aplicación
     *
     * @param uri La URI de la base de datos.
     * @param user El nombre de usuario de la base de datos.
     * @param password La contraseña de la base de datos.
     * @param databaseName El nombre de la base de datos.
     * @return El nombre del usuario si el inicio de sesión fue exitoso, de lo contrario null.
     */
    public String login(String uri, String user, String password, String databaseName) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            System.out.println("Ingrese su nombre de usuario: ");
            String name = scanner.nextLine();
            System.out.println("Ingrese su contraseña: ");
            String pass = scanner.nextLine();
            String pword = db.getUserPassword(name, databaseName);
            if (pword != null && pword.equals(pass)) {
                return name;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * carga una lista de usuarios desde un archivo CSV
     *
     * @param filename El nombre del archivo CSV.
     * @param uri La URI de la base de datos.
     * @param user El nombre de usuario de la base de datos.
     * @param password La contraseña de la base de datos.
     * @param databaseName El nombre de la base de datos.
     * @return Una lista de usuarios.
     */
    private static List<User> loadUsersFromCSV(String filename, String uri, String user, String password, String databaseName) {
        List<User> users = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            Neo4jConnection db = new Neo4jConnection(uri, user, password);
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 2) {
                    System.err.println("Invalid line: " + line);
                    continue; // Skip invalid lines
                }
                try {
                    String username = normalizeString(data[0]);
                    String userPassword = data[1];
                    db.createUser(username, userPassword, databaseName, userPassword, userPassword);
                    User userObj = new User(username, userPassword);
                    for (int i = 2; i < data.length; i++) {
                        String interest = data[i];
                        String[] parts = interest.split(":");
                        if (parts.length != 2) {
                            System.err.println("Invalid interest format: " + interest);
                            continue;
                        }
                        String category = parts[0];
                        String interestName = parts[1];
                        if (interestName.startsWith("!")) {
                            userObj.addDislike(category, interestName.substring(1));
                            db.addDislike(username, category, interestName.substring(1), databaseName);
                        } else {
                            userObj.addLike(category, interestName);
                            db.addLike(username, category, interestName, databaseName);
                        }
                    }
                    users.add(userObj);
                } catch (Exception e) {
                    System.err.println("Error processing line: " + line);
                    e.printStackTrace();
                }
            }
            try {
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
    

    /**
     * Normaliza una cadena eliminando acentos y convirtiéndola a minúsculas.
     *
     * @param input La cadena a normalizar.
     * @return La cadena normalizada.
     */
    private static String normalizeString(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase().trim();
    }
}
