import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Application {
    private RecommendationSystem recommendationSystem;
    private Scanner scanner;
    private User currentUser;

    public Application() {
        this.recommendationSystem = new RecommendationSystem("bolt://localhost:7687", "neo4j", "password");
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean exit = false;

        while (!exit) {
            System.out.println("Bienvenido al sistema de recomendaciones");
            System.out.println("1. Crear usuario");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consumir la nueva línea

                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        login();
                        if (currentUser != null) {
                            userSession();
                        }
                        break;
                    case 3:
                        exit = true;
                        System.out.println("Saliendo del programa...");
                        break;
                    default:
                        System.out.println("Opción no válida, por favor intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine();  // Consumir la nueva línea
            }
        }
    }

    private void createUser() {
        System.out.print("Ingrese nombre de usuario: ");
        String username = scanner.nextLine().toLowerCase();
        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine().toLowerCase();

        User newUser = new User(username, password);

        newUser.addDealBreaker("sexo", getValidInput("Ingrese sexo (masculino/femenino): ", new String[]{"masculino", "femenino"}));
        newUser.addDealBreaker("sexualidad", getValidInput("Ingrese sexualidad (heterosexual/homosexual): ", new String[]{"heterosexual", "homosexual"}));
        System.out.print("Ingrese tipo de relación que busca: ");
        newUser.addDealBreaker("tipo de relación", scanner.nextLine().toLowerCase());

        while (true) {
            System.out.print("¿Desea agregar deal breakers adicionales? (si/no): ");
            String response = scanner.nextLine().toLowerCase();
            if (response.equals("si")) {
                boolean addMore = true;
                while (addMore) {
                    System.out.print("Ingrese categoría del deal breaker: ");
                    String category = scanner.nextLine().toLowerCase();
                    System.out.print("Ingrese deal breaker adicional: ");
                    String value = scanner.nextLine().toLowerCase();
                    newUser.addDealBreaker(category, value);
                    System.out.print("¿Desea agregar otro deal breaker? (si/no): ");
                    addMore = scanner.nextLine().equalsIgnoreCase("si");
                }
            } else if (response.equals("no")) {
                break;
            } else {
                System.out.println("Respuesta no válida. Por favor, responda 'si' o 'no'.");
            }
        }

        while (true) {
            System.out.print("¿Desea agregar intereses? (si/no): ");
            String response = scanner.nextLine().toLowerCase();
            if (response.equals("si")) {
                boolean addMore = true;
                while (addMore) {
                    System.out.print("Ingrese categoría del interés: ");
                    String category = scanner.nextLine().toLowerCase();
                    System.out.print("Ingrese interés: ");
                    String interest = scanner.nextLine().toLowerCase();
                    newUser.addLike(category, interest);
                    System.out.print("¿Desea agregar otro interés? (si/no): ");
                    addMore = scanner.nextLine().equalsIgnoreCase("si");
                }
            } else if (response.equals("no")) {
                break;
            } else {
                System.out.println("Respuesta no válida. Por favor, responda 'si' o 'no'.");
            }
        }

        recommendationSystem.addUser(newUser);
        System.out.println("Usuario creado exitosamente.");
    }

    private String getValidInput(String prompt, String[] validOptions) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toLowerCase();
            for (String option : validOptions) {
                if (option.equalsIgnoreCase(input)) {
                    return input;
                }
            }
            System.out.println("Entrada no válida. Por favor, intente de nuevo.");
        }
    }

    private void login() {
        System.out.print("Ingrese nombre de usuario: ");
        String username = scanner.nextLine().toLowerCase();
        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine().toLowerCase();

        User user = recommendationSystem.getUser(username);

        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("Inicio de sesión exitoso.");
        } else {
            if (user == null) {
                System.out.println("El usuario no existe.");
            } else {
                System.out.println("Nombre de usuario o contraseña incorrectos.");
            }
            currentUser = null;
        }
    }

    private void userSession() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\nMenú de usuario:");
            System.out.println("1. Agregar gusto");
            System.out.println("2. Ver recomendaciones");
            System.out.println("3. Eliminar usuario");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consumir la nueva línea

                switch (choice) {
                    case 1:
                        addLike();
                        break;
                    case 2:
                        viewRecommendations();
                        break;
                    case 3:
                        deleteUser();
                        exit = true;
                        break;
                    case 4:
                        exit = true;
                        System.out.println("Cerrando sesión...");
                        break;
                    default:
                        System.out.println("Opción no válida, por favor intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine();  // Consumir la nueva línea
            }
        }
    }

    private void addLike() {
        System.out.print("Ingrese categoría del gusto: ");
        String category = scanner.nextLine().toLowerCase();
        System.out.print("Ingrese gusto: ");
        String like = scanner.nextLine().toLowerCase();

        currentUser.addLike(category, like);
        recommendationSystem.addInterest(currentUser.getUsername(), category, like);
        System.out.println("Gusto añadido exitosamente.");
    }

    private void viewRecommendations() {
        System.out.println("Recomendaciones:");
        List<User> recommendations = recommendationSystem.getRecommendations(currentUser);
        for (User recommendation : recommendations) {
            System.out.println(recommendation.getUsername());
        }
    }

    private void deleteUser() {
        recommendationSystem.removeUser(currentUser.getUsername());
        System.out.println("Usuario eliminado exitosamente.");
        currentUser = null;
    }

    public static void main(String[] args) {
        Application app = new Application();
        app.start();
    }
}