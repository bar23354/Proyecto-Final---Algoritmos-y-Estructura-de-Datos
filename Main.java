import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Recomendador recomendador = new Recomendador("bolt://localhost:7687", "neo4j", "password");

        VBox root = new VBox();
        root.setSpacing(10);

        Label label = new Label("Bienvenido al Sistema de Recomendaciones");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre");
        TextField correoField = new TextField();
        correoField.setPromptText("Correo");
        TextField contrasenaField = new TextField();
        contrasenaField.setPromptText("Contraseña");
        TextField gustosField = new TextField();
        gustosField.setPromptText("Gustos (separados por comas)");
        TextField disgustosField = new TextField();
        disgustosField.setPromptText("Disgustos (separados por comas)");

        Button registrarButton = new Button("Registrar");
        registrarButton.setOnAction(e -> {
            String nombre = nombreField.getText();
            String correo = correoField.getText();
            String contrasena = contrasenaField.getText();
            List<String> gustos = List.of(gustosField.getText().split(","));
            List<String> disgustos = List.of(disgustosField.getText().split(","));
            recomendador.registrarUsuario(nombre, correo, contrasena, gustos, disgustos);
        });

        root.getChildren().addAll(label, nombreField, correoField, contrasenaField, gustosField, disgustosField, registrarButton);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Sistema de Recomendaciones");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
