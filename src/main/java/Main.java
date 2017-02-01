import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
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
    public static final String URL = "https://dl.dropboxusercontent.com/s/w1rpxutgcxgb0l7/BellTimes.txt?dl=0";

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

        //Grab the server version of BellTimes.txt
        try {
            URL website = new URL(URL);
            InputStream in = website.openStream();
            if (!(in == null)) {
                Files.copy(in, Paths.get("BellTimes.txt"), StandardCopyOption.REPLACE_EXISTING);
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Set up mainPane (main content in the window)
        MainPane mainPane = new MainPane();
        mainPane.setPadding(new Insets(10));

        //Create a scene and populate it with mainPane
        Scene scene = new Scene(mainPane, 700, 720);
        mainPane.getStyleClass().addAll("scene");

        //Apply CSS Stylesheets
        scene.getStylesheets().add("main.css");

        //Initialize window preferences
        primaryStage.setTitle("WHS Planner Bell Schedule Editor");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
