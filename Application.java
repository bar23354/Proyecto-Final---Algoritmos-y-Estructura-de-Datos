/**
 * Universidad del Valle de Guatemala
 * @author Isabella Obando, 23074
 * @author Mia Alejandra Fuentes Merida, 23775
 * @author Roberto Barreda, 23354
 * @description Clase principal de la aplicación que interactúa con una base de datos Neo4j para
 * @date creación 23/05/2024 última modificación 28/05/2024
 */

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase principal de la aplicación de sistema de recomendaciones.
 */
public class Application {
    private RecommendationSystem recommendationSystem; // Maneja la lógica de recomendaciones
    private Scanner scanner; // Maneja la entrada del usuario
    private User currentUser; // Representa al usuario que ha iniciado sesión actualmente

    /**
     * Constructor de la clase Application.
     * Inicializa el sistema de recomendaciones y el escáner de entrada.
     */
    public Application() {
        this.recommendationSystem = new RecommendationSystem("bolt://localhost:7687", "neo4j", "password"); // Inicializa el sistema de recomendaciones
        this.scanner = new Scanner(System.in); // Inicializa el escáner para la entrada del usuario
    }

    /**
     * Método principal que inicia la aplicación.
     * Muestra el menú principal y maneja la lógica de navegación.
     */
    public void start() {
        boolean exit = false; // Bandera para controlar el bucle principal

        while (!exit) {
            // Muestra el menú principal
            System.out.println("Bienvenido al sistema de recomendaciones");
            System.out.println("1. Crear usuario");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume la nueva línea

                // Maneja la opción del menú seleccionada por el usuario
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
                scanner.nextLine();  // Consume la nueva línea
            }
        }
    }

    /**
     * Método para crear un nuevo usuario.
     * Solicita información del usuario y agrega el nuevo usuario al sistema de recomendaciones.
     */
    private void createUser() {
        System.out.print("Ingrese nombre de usuario: ");
        String username = scanner.nextLine().toLowerCase();
        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine().toLowerCase();

        User newUser = new User(username, password);

        newUser.addDealBreaker("sexo", getValidInput("Ingrese sexo (masculino/femenino): ", new String[]{"masculino", "femenino"}));
        newUser.addDealBreaker("sexualidad", getValidInput("Ingrese sexualidad (heterosexual/homosexual/bisexual): ", new String[]{"heterosexual", "homosexual", "bisexual"}));
        newUser.addDealBreaker("tipo de relación", getValidInput("Ingrese tipo de relación (seria/casual): ", new String[]{"seria", "casual"}));

        while (true) {
            System.out.print("¿Desea agregar deal breakers adicionales? (si/no): ");
            String response = scanner.nextLine().toLowerCase();
            if (response.equals("si")) {
                boolean addMore = true;
                while (addMore) {
                    System.out.print("Ingrese deal breaker adicional: ");
                    String value = scanner.nextLine().toLowerCase();
                    newUser.addDislike(value);
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
                    System.out.print("Ingrese interés: ");
                    String interest = scanner.nextLine().toLowerCase();
                    newUser.addLike(interest);
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

    /**
     * Método para obtener una entrada válida del usuario.
     * 
     * @param prompt El mensaje a mostrar al usuario.
     * @param validOptions Las opciones válidas que el usuario puede ingresar.
     * @return La entrada válida ingresada por el usuario.
     */
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

    /**
     * Método para iniciar sesión con un usuario existente.
     * Verifica las credenciales del usuario y establece la sesión actual.
     */
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

    /**
     * Método que maneja la sesión del usuario.
     * Muestra el menú de usuario y maneja la lógica de navegación.
     */
    private void userSession() {
        boolean exit = false; // Bandera para controlar el bucle de la sesión del usuario

        while (!exit) {
            // Muestra el menú de usuario
            System.out.println("\nMenú de usuario:");
            System.out.println("1. Agregar gusto");
            System.out.println("2. Ver recomendaciones");
            System.out.println("3. Eliminar usuario");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume la nueva línea

                // Maneja la opción del menú seleccionada por el usuario
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
                scanner.nextLine();  // Consume la nueva línea
            }
        }
    }

    /**
     * Método para agregar un nuevo gusto al usuario actual.
     */
    private void addLike() {
        System.out.print("Ingrese gusto: ");
        String like = scanner.nextLine().toLowerCase();

        currentUser.addLike(like);
        recommendationSystem.addInterest(currentUser.getUsername(), like);
        System.out.println("Gusto añadido exitosamente.");
    }

    /**
     * Método para ver las recomendaciones para el usuario actual.
     */
    private void viewRecommendations() {
        System.out.println("Basados en tus deal breakers y gustos, te recomendamos:");
        List<Map<String, Object>> recommendations = recommendationSystem.getRecommendations(currentUser);
        for (Map<String, Object> recommendation : recommendations) {
            System.out.println("Usuario: " + recommendation.get("username"));
            System.out.println("Intereses Comunes: " + recommendation.get("commonInterests"));
            System.out.println();
        }
    }

    /**
     * Método para eliminar el usuario actual.
     */
    private void deleteUser() {
        recommendationSystem.removeUser(currentUser.getUsername());
        System.out.println("Usuario eliminado exitosamente.");
        currentUser = null;
    }

    /**
     * Método principal que inicia la aplicación.
     * 
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        Application app = new Application();
        app.start();
    }
}