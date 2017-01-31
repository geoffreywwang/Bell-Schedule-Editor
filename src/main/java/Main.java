import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        MainPane mainPane = new MainPane();
        mainPane.setPadding(new Insets(10));
        Scene scene = new Scene(mainPane,700,720);
        mainPane.getStyleClass().addAll("scene");
        scene.getStylesheets().add("main.css");

        primaryStage.setTitle("WHS Planner Bell Schedule Editor");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
