package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Aplicacion {
    private static Scanner scanner = new Scanner(System.in);
    private static final String[] CATEGORIES = {"Deportes", "Cultura", "Religión", "Valores"};

public static void main(String[] args) throws IOException {
    String uri = "bolt://localhost:7687";
    String user = "neo4j";
    String password = "password";
    String databaseName = "neo4j2";
    String name = null;

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
        } catch (Exception e) {
            System.err.println("Error cerrando la conexión a Neo4j: " + e.getMessage());
            e.printStackTrace();
        }
    } catch (Exception e) {
        System.err.println("Error al cargar usuarios desde CSV: " + e.getMessage());
        e.printStackTrace();
    }    

    System.out.println("Opciones: ");
    System.out.println("1. Iniciar sesión");
    System.out.println("2. Registrarse");
    int option = scanner.nextInt();
    scanner.nextLine(); // Consume newline

    Aplicacion app = new Aplicacion();
    Neo4jConnection neo4jConnection = new Neo4jConnection(uri, user, password);

    if (option == 1) {
        name = app.login(uri, user, password, databaseName);
        while (name == null) {
            System.out.println("Usuario o contraseña incorrectos");
            System.out.println("¿Desea registrarse? (s/n)");
            String register = scanner.nextLine();
            if (register.equals("s")) {
                app.signin(uri, user, password, databaseName);
                name = app.login(uri, user, password, databaseName);
            }
        }
    } else {
        app.signin(uri, user, password, databaseName);
        name = app.login(uri, user, password, databaseName);
    }

    if (name != null) {
        System.out.println("Bienvenido " + name);

        boolean continuar = true;
        while (continuar) {
            System.out.println("Opciones: ");
            System.out.println("1. Añadir gusto");
            System.out.println("2. Añadir disgusto");
            System.out.println("3. Ver conexiones basadas en gustos");
            System.out.println("4. Ver desconexiones basadas en disgustos");
            System.out.println("5. Eliminar usuario");
            System.out.println("6. Salir");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consume newline
                switch (opcion) {
                    case 1:
                        String[] likeData = getInterestData();
                        app.addLike(uri, user, password, databaseName, name, likeData[0], likeData[1]);
                        break;
                    case 2:
                        String[] dislikeData = getInterestData();
                        app.addDislike(uri, user, password, databaseName, name, dislikeData[0], dislikeData[1]);
                        break;
                    case 3:
                        LinkedList<String> connections = app.connectUsersBasedOnLikes(uri, user, password, databaseName);
                        System.out.println("Conexiones basadas en gustos: ");
                        for (String connection : connections) {
                            System.out.println(connection);
                        }
                        break;
                    case 4:
                        LinkedList<String> disconnections = app.disconnectUsersBasedOnDislikes(uri, user, password, databaseName);
                        System.out.println("Desconexiones basadas en disgustos: ");
                        for (String disconnection : disconnections) {
                            System.out.println(disconnection);
                        }
                        break;
                    case 5:
                        System.out.print("Ingrese el nombre de usuario a eliminar: ");
                        String deleteUser = scanner.nextLine();
                        String result = neo4jConnection.deleteUser(deleteUser, databaseName);
                        System.out.println(result);
                        break;
                    case 6:
                        continuar = false;
                        break;
                    default:
                        System.out.println("Opción inválida.");
                        break;
                }
            }
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
                try {
                    System.out.println("Línea del CSV: " + line); // Imprimir la línea para depuración
                    String[] data = line.split(",");
                    String username = data[0].trim();
                    System.out.println("Nombre de usuario: " + username); // Imprimir el nombre de usuario para depuración
    
                    if (!userExists(username, db, databaseName)) {
                        createUser(username, db, databaseName);
                    }
    
                    User userObj = new User(username);
    
                    for (int i = 1; i < data.length; i++) {
                        String interestName = data[i].trim();
                        String sanitizedInterestName = normalizeString(interestName);
                        if (i <= 4) {
                            userObj.addLike(getCategoryByIndex(i), sanitizedInterestName);
                        } else {
                            userObj.addDislike(getCategoryByIndex(i - 4), sanitizedInterestName);
                        }
                    }
                    users.add(userObj);
                } catch (Exception e) {
                    System.err.println("Error procesando línea: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo CSV: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
    

    private static boolean userExists(String username, Neo4jConnection db, String databaseName) {
        // Consultar la base de datos Neo4j para verificar si el usuario ya existe
        String password = db.getUserPassword(username, databaseName);
        return password != null;
    }

    private static void createUser(String username, Neo4jConnection db, String databaseName) {
        // Crear un nuevo usuario en la base de datos Neo4j
        db.createUser(username, "password", databaseName); // Se puede establecer una contraseña predeterminada
    }

    private static String getCategoryByIndex(int index) {
        switch (index) {
            case 1:
            case 5:
                return "Deportes";
            case 2:
            case 6:
                return "Cultura";
            case 3:
            case 7:
                return "Religión";
            case 4:
            case 8:
                return "Valores";
            default:
                return "Desconocido";
        }
    }
    

    private static String normalizeString(String input) {
        input = input.toLowerCase();
        input = Normalizer.normalize(input, Normalizer.Form.NFD);
        input = input.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        input = input.replaceAll("\\s+", "");
        return input;
    }
    
}

