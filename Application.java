import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Application {
    private RecommendationSystem recommendationSystem;
    private Scanner scanner;
    private User currentUser;

    public Application() {
        this.recommendationSystem = new RecommendationSystem();
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

        System.out.print("Ingrese sexo (masculino/femenino): ");
        newUser.addDealBreaker("sexo", scanner.nextLine().toLowerCase());
        System.out.print("Ingrese sexualidad: ");
        newUser.addDealBreaker("sexualidad", scanner.nextLine().toLowerCase());
        System.out.print("Ingrese tipo de relación que busca: ");
        newUser.addDealBreaker("tipo de relación", scanner.nextLine().toLowerCase());

        System.out.print("¿Desea agregar deal breakers adicionales? (sí/no): ");
        if (scanner.nextLine().equalsIgnoreCase("sí")) {
            boolean addMore = true;
            while (addMore) {
                System.out.print("Ingrese deal breaker adicional: ");
                newUser.addDealBreaker("adicional", scanner.nextLine().toLowerCase());
                System.out.print("¿Desea agregar otro deal breaker? (sí/no): ");
                addMore = scanner.nextLine().equalsIgnoreCase("sí");
            }
        }

        recommendationSystem.addUser(newUser);
        System.out.println("Usuario creado exitosamente.");
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
