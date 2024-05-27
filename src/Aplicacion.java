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

public class Aplicacion {
    private static Scanner scanner = new Scanner(System.in);
    private static final String[] CATEGORIES = {"Deportes", "Cultura", "Religión", "Valores"};
    private Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));

    public static void main(String[] args) {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "password";
        String databaseName = "neo4j2";
        String name = null;
        Scanner scanner = new Scanner(System.in);

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
                            String createUserResult = db.createUser(name, userPassword, databaseName);
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

    public void signin(String uri, String user, String password, String databaseName) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            System.out.println("Ingrese su nombre de usuario: ");
            String name = scanner.nextLine();
            System.out.println("Ingrese su contraseña: ");
            String pass = scanner.nextLine();
            String result = db.createUser(name, pass, databaseName);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLike(String uri, String user, String password, String databaseName, String userName, String category, String like) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            String result = db.addLike(userName, category, like, databaseName);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDislike(String uri, String user, String password, String databaseName, String userName, String category, String dislike) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            String result = db.addDislike(userName, category, dislike, databaseName);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LinkedList<String> connectUsersBasedOnLikes(String uri, String user, String password, String databaseName) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            return db.connectUsersBasedOnLikes(databaseName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LinkedList<String> disconnectUsersBasedOnDislikes(String uri, String user, String password, String databaseName) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            return db.disconnectUsersBasedOnDislikes(databaseName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
                    db.createUser(username, userPassword, databaseName);
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
    

    private static String normalizeString(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.toLowerCase().trim();
    }
}
