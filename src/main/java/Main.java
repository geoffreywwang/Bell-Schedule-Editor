import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Main class that starts the program
 */
public class Main extends Application {

    //URL of the shared raw txt file
    public static final String URL = "https://dl.dropboxusercontent.com/s/a8e3qfwgbfbi0qc/BellTimes.txt?dl=0";

    /**
     * Program starts here!
     *
     * @param args Default arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Creates the window and populates it
     * Sets general settings
     *
     * @param primaryStage Inital stage
     * @throws Exception Error in running the program
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        //Initializes the error log if it doesn't exist
        File file = new File("error.log");
        if (!file.exists()) {
            file.createNewFile();
        }

        //Directs all errors to print to the error log instead of in console
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        System.setErr(ps);

        //Creates Keys folder to store API keys if it doesn't already exist
        File keys = new File("Keys");
        if (!keys.exists()) {
            keys.mkdir();
        }

        //Creates the BellTimes.txt temp file if it doesn't exist
        File saveFile = new File("BellTimes.txt");
        try {
            saveFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Grab the server version of BellTimes.txt and test connection
        boolean isConnected = false;
        InputStream in = null;
        try {
            URL website = new URL(URL);
            in = website.openStream();
            if (!(in == null)) {
                Files.copy(in, Paths.get("BellTimes.txt"), StandardCopyOption.REPLACE_EXISTING);
            }
            isConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
            isConnected = false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Set up mainPane (main content in the window) and pass in connection status
        MainPane mainPane = new MainPane(isConnected);
        mainPane.setPadding(new Insets(10));

        //Create a scene and populate it with a StackPane with mainPane and a button it it
        StackPane stackPane = new StackPane();
        JFXButton aboutButton = new JFXButton();
        aboutButton.setPadding(new Insets(15));
        stackPane.getChildren().addAll(mainPane, aboutButton);
        setupPopup(aboutButton, stackPane);
        stackPane.setAlignment(Pos.TOP_LEFT);

        Scene scene = new Scene(stackPane, 700, 720);
        mainPane.getStyleClass().addAll("scene");

        //Apply CSS Stylesheets
        scene.getStylesheets().add("main.css");

        //Initialize window preferences
        primaryStage.setTitle("WHS Planner Bell Schedule Editor");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Creates a popup dialog that displays info
     */
    private void setupPopup(JFXButton aboutButton, StackPane stackPane) {
        aboutButton.setText("About");
        aboutButton.getStyleClass().addAll("unselectedButton");

        AboutPane aboutPane = new AboutPane();
        aboutPane.setPadding(new Insets(10));
        aboutButton.setOnMouseClicked(event -> {
            JFXDialog dialog = new JFXDialog(stackPane, aboutPane, JFXDialog.DialogTransition.CENTER, true);
            dialog.show();
        });
    }
}
