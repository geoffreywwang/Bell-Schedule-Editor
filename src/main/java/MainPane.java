import com.jfoenix.controls.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Main Content of the window
 */
public class MainPane extends VBox {

    //Used for the extra info
    private JFXDatePicker datePicker;
    private JFXTextField titleField, apiKeyField;

    //Used for the scroll pane
    private ScrollPane scrollPane;
    private VBox scrollPaneContent;

    private JFXSnackbar snackbar;

    /**
     * Sets up pane and initializes everything
     */
    public MainPane() {

        //General Pane Settings
        this.setSpacing(5);
        this.setAlignment(Pos.CENTER);

        //Create a snackbar to display messages
        snackbar = new JFXSnackbar(this);

        //Add the Title Field to the pane
        titleField = new JFXTextField();
        titleField.setPromptText("Title");

        //Add the Date Picker to the pane
        datePicker = new JFXDatePicker();

        //Add the Add Line Button
        JFXButton addLineButton = new JFXButton("+");
        addLineButton.setId("addButton");
        addLineButton.setOnMouseClicked(event -> {
            addLine();
        });

        //Enclose the titleField, datePicker, and addLineButton in one row
        HBox hBox = new HBox(titleField, datePicker, addLineButton);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);
        this.getChildren().add(hBox);


        //Initialize the scrollPaneContent
        scrollPaneContent = new VBox();
        scrollPaneContent.setSpacing(5);
        scrollPaneContent.setAlignment(Pos.TOP_CENTER);
        scrollPaneContent.setPadding(new Insets(5, 0, 0, 0));
        scrollPaneContent.setId("scrollPane");

        //Initialize the scrollPane
        scrollPane = new ScrollPane(scrollPaneContent);
        this.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setId("scrollPane");
        this.getChildren().add(scrollPane);

        //Create the submit button
        JFXButton submitButton = new JFXButton("Save...");
        submitButton.setPrefWidth(150);
        submitButton.setId("submitButton");

        JFXButton createLocalButton = new JFXButton("Save as local file");
        createLocalButton.setButtonType(JFXButton.ButtonType.RAISED);
        createLocalButton.getStyleClass().addAll("unselectedButton");
        createLocalButton.setOnMouseClicked(event -> {
            if (saveData()) {
                snackbar.show("Saved successful, check your Documents folder!", 2000);
            }
        });

        JFXButton createRemoteButton = new JFXButton("Save to server");
        createRemoteButton.setButtonType(JFXButton.ButtonType.RAISED);
        createRemoteButton.getStyleClass().addAll("unselectedButton");
        createRemoteButton.setOnMouseClicked(event -> {
            try {
                String apiKey = KeyTool.getAPIKey();
                if (apiKey == null) {
                    throw new FileNotFoundException();
                } else if (apiKey.length() < 10) {
                    throw new FileNotFoundException();
                }
                if (saveData()) {
                    uploadToDropbox("BellTimes.txt", "BellTimes.txt");
                    snackbar.show("Upload successful!", 1000);
                }
            } catch (FileNotFoundException e) {
                initiateAPIKeyField();
                snackbar.show("Please supply API key and try again...", 1000);
            }

        });

        JFXButton clearServerButton = new JFXButton("Clear Server");
        clearServerButton.setButtonType(JFXButton.ButtonType.RAISED);
        clearServerButton.getStyleClass().addAll("unselectedButton");
        clearServerButton.setOnMouseClicked(event -> {
            FileWriter fw = null;
            BufferedWriter bw = null;

            try {
                fw = new FileWriter("BellTimes.txt");
                bw = new BufferedWriter(fw);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null)
                        bw.close();
                    if (fw != null)
                        fw.close();

                } catch (IOException ex) {
                    ex.printStackTrace();

                }
            }

            try {
                String apiKey = KeyTool.getAPIKey();
                if (apiKey == null) {
                    throw new FileNotFoundException();
                } else if (apiKey.length() < 10) {
                    throw new FileNotFoundException();
                }
                if (saveData()) {
                    uploadToDropbox("BellTimes.txt", "BellTimes.txt");
                    snackbar.show("Clear successful!", 1000);
                }
            } catch (FileNotFoundException e) {
                initiateAPIKeyField();
                snackbar.show("Please supply API key and try again...", 1000);
            }
        });

        JFXNodesList buttons = new JFXNodesList();
        buttons.setSpacing(5);
        buttons.addAnimatedNode(submitButton);
        buttons.addAnimatedNode(createLocalButton);
        buttons.addAnimatedNode(createRemoteButton);
        buttons.addAnimatedNode(clearServerButton);


        this.getChildren().add(buttons);
    }

    private void addLine() {
        JFXButton killButton = new JFXButton("-");
        killButton.setId("removeButton");

        ScheduleLine line = new ScheduleLine(killButton);
        line.getStyleClass().add("scheduleLine");
        killButton.setOnMouseClicked(event -> {
            scrollPaneContent.getChildren().remove(line);
        });
        scrollPaneContent.getChildren().add(line);
    }

    public boolean saveData() {
        List<String> lines = new ArrayList<>();

        if (titleField.getText() == null || titleField.getText().equals("")) {
            snackbar.show("Error! Please enter a title!", 2000);
            return false;
        }

        if (datePicker.getValue() == null) {
            snackbar.show("Error! Please enter a date!", 2000);
            return false;
        }

        lines.add(datePicker.getValue().getMonth().getValue() + "");
        lines.add(datePicker.getValue().getDayOfMonth() + "");
        lines.add(datePicker.getValue().getYear() + "");
        lines.add(scrollPaneContent.getChildren().size() + 2 + "");

        lines.add(titleField.getText() + " ");
        lines.add("---------------");


        for (Node node : scrollPaneContent.getChildren()) {
            ScheduleLine temp = (ScheduleLine) node;
            lines.add(temp.getBlockData());
        }

        lines.add(datePicker.getValue().toString());
        lines.add("---------------");

        for (Node node : scrollPaneContent.getChildren()) {
            ScheduleLine temp = (ScheduleLine) node;
            lines.add(temp.getExtraData());
        }

        String home = System.getenv("HOME");

        File documents = new File(home + "/Documents/WHS Bell Schedule Editor");

        if (!documents.exists()) {
            documents.mkdir();
        }

        java.nio.file.Path file = Paths.get(home + "/Documents/WHS Bell Schedule Editor/" + datePicker.getValue().toString() + ".txt");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter("BellTimes.txt", true);
            bw = new BufferedWriter(fw);

            for(String s: lines){
                bw.write(s+"\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();

            } catch (IOException ex) {
                ex.printStackTrace();

            }
        }
        return true;
    }

    public void uploadToDropbox(String currentFileLocation, String targetFileName) {
        try {
            DropboxClient client = new DropboxClient(KeyTool.getAPIKey());
            client.uploadFile(currentFileLocation, targetFileName);
        } catch (Exception e) {
            snackbar.show("Error uploading to server!", 2000);
            snackbar.show("Please check your wifi or try uploading manually", 2000);
            e.printStackTrace();
        }
    }

    public void initiateAPIKeyField() {
        if (apiKeyField == null) {
            apiKeyField = new JFXTextField();
            apiKeyField.setPadding(new Insets(10));
            apiKeyField.setPrefWidth(350);
            apiKeyField.setPromptText("Paste your Dropbox API key and press enter...");
            apiKeyField.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    KeyTool.saveAPIKey(apiKeyField.getText());
                    this.getChildren().remove(apiKeyField);
                    apiKeyField = null;
                }
            });
            this.getChildren().add(this.getChildren().size() - 2, apiKeyField);
        }
    }
}
