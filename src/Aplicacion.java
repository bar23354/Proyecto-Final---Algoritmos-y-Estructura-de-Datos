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

    public static void main(String[] args) {
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "password";
        String databaseName = "neo4j2";
        String name;
        String result;

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
                            db.addLike(currentUser.getUsername(), category, interest, databaseName);
                        });
                    });
                    System.out.println("Dislikes:");
                    currentUser.getDislikes().forEach((category, dislikes) -> {
                        System.out.println(category + ": " + dislikes);
                        dislikes.forEach(dislike -> {
                            db.addDislike(currentUser.getUsername(), category, dislike, databaseName);
                        });
                    });
                    System.out.println();
                }
            }
        } catch (IOException e) {
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
                System.out.println("5. Salir");
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
                        continuar = false;
                        break;
                    case 6:
                        System.out.print("Ingrese el nombre de usuario a eliminar: ");
                        String deleteUser = scanner.nextLine();
                        result = neo4jConnection.deleteUser(deleteUser, databaseName);
                        System.out.println(result);
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

    // Método para cargar usuarios desde un archivo CSV
    private static List<User> loadUsersFromCSV(String filename, String uri, String user, String password, String databaseName) throws IOException {
        List<User> users = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            Neo4jConnection db = new Neo4jConnection(uri, user, password);
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String username = data[0].trim();
                // Verificar si el usuario ya existe
                if (!userExists(username, db, databaseName)) {
                    // Si el usuario no existe, crearlo
                    createUser(username, db, databaseName);
                }
                User userObj = new User(username); // Crear un nuevo usuario
                // Añadir likes y dislikes
                for (int i = 2; i < data.length; i++) {
                    String interestName = data[i].trim();
                    String sanitizedInterestName = normalizeString(interestName);
                    if (i < 6) {
                        userObj.addLike(getCategoryByIndex(i), sanitizedInterestName); // Agregar like al usuario
                    } else {
                        userObj.addDislike(getCategoryByIndex(i), sanitizedInterestName); // Agregar dislike al usuario
                    }
                }
                users.add(userObj); // Agregar usuario a la lista
            }
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
    
    private static void addLike(String username, String category, String interest, Neo4jConnection db, String databaseName) {
        // Añadir un like a un usuario en la base de datos Neo4j
        db.addLike(username, category, interest, databaseName);
    }
    
    private static void addDislike(String username, String category, String interest, Neo4jConnection db, String databaseName) {
        // Añadir un dislike a un usuario en la base de datos Neo4j
        db.addDislike(username, category, interest, databaseName);
    }
    

    private static String getCategoryByIndex(int index) {
        switch (index) {
            case 2:
            case 6:
                return "Deportes";
            case 3:
            case 7:
                return "Cultura";
            case 4:
            case 8:
                return "Religión";
            case 5:
            case 9:
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
