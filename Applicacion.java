package src;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.Collections;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.Collections;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.Collections;

public class Applicacion {
    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String uri = "bolt://localhost:7687";
        String user = "neo4j";
        String password = "password";
        String databaseName = "neo4j2";
        String name;

        System.out.println("Opciones: ");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse");
        int option = scanner.nextInt();
        scanner.nextLine(); // Consume
        if (option == 1) {
            name = login(uri, user, password, databaseName);
            while (name == null) {
                System.out.println("Usuario o contraseña incorrectos");
                System.out.println("¿Desea registrarse? (s/n)");
                String register = scanner.nextLine();
                if (register.equals("s")) {
                    Applicacion app = new Applicacion();
                    app.signin(uri, user, password, databaseName);
                    name = login(uri, user, password, databaseName);
                }
            }
        } else {
            Applicacion app = new Applicacion();
            app.signin(uri, user, password, databaseName);
            name = login(uri, user, password, databaseName);
        }

        if (name != null) {
            System.out.println("Bienvenido " + name);
            Applicacion app = new Applicacion();

            System.out.println("Opciones: ");
            System.out.println("1. Añadir gusto");
            System.out.println("2. Añadir disgusto");
            System.out.println("3. Ver conexiones basadas en gustos");
            System.out.println("4. Ver desconexiones basadas en disgustos");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (opcion == 1) {
                System.out.println("Ingrese el nombre del interés: ");
                String interest = scanner.nextLine();
                app.addLike(uri, user, password, databaseName, name, interest);
            } else if (opcion == 2) {
                System.out.println("Ingrese el nombre del interés: ");
                String interest = scanner.nextLine();
                app.addDislike(uri, user, password, databaseName, name, interest);
            } else if (opcion == 3) {
                LinkedList<String> connections = app.connectUsersBasedOnLikes(uri, user, password, databaseName);
                System.out.println("Conexiones basadas en gustos: ");
                for (String connection : connections) {
                    System.out.println(connection);
                }
            } else if (opcion == 4) {
                LinkedList<String> disconnections = app.disconnectUsersBasedOnDislikes(uri, user, password,
                        databaseName);
                System.out.println("Desconexiones basadas en disgustos: ");
                for (String disconnection : disconnections) {
                    System.out.println(disconnection);
                }
            }
        }
    }

    public String signin(String uri, String user, String password, String databaseName) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            System.out.println("Ingrese su nombre de usuario: ");
            String name = System.console().readLine();
            System.out.println("Ingrese su contraseña: ");
            String pass = System.console().readLine();
            String result = db.createUser(name, pass, databaseName);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addLike(String uri, String user, String password, String databaseName, String userName,
            String interest) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            String result = db.addLike(userName, interest, databaseName);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDislike(String uri, String user, String password, String databaseName, String userName,
            String interest) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            String result = db.addDislike(userName, interest, databaseName);
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

    public LinkedList<String> disconnectUsersBasedOnDislikes(String uri, String user, String password,
            String databaseName) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            return db.disconnectUsersBasedOnDislikes(databaseName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String login(String uri, String user, String password, String databaseName) {
        try (Neo4jConnection db = new Neo4jConnection(uri, user, password)) {
            System.out.println("Ingrese su nombre de usuario: ");
            String name = System.console().readLine();
            System.out.println("Ingrese su contraseña: ");
            String pass = System.console().readLine();
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
}