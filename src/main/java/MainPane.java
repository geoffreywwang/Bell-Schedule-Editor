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

    //Used for displaying messages
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
        setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setId("scrollPane");
        this.getChildren().add(scrollPane);

        //Create the default save button
        JFXButton submitButton = new JFXButton("Save...");
        submitButton.setPrefWidth(150);
        submitButton.setId("submitButton");

        //Create the "save to local" button
        JFXButton createLocalButton = new JFXButton("Save as local file");
        createLocalButton.setButtonType(JFXButton.ButtonType.RAISED);
        createLocalButton.getStyleClass().addAll("unselectedButton");
        createLocalButton.setOnMouseClicked(event -> {
            if (saveData()) {
                snackbar.show("Saved successful, check your Documents folder!", 2000);
            }
        });

        //Create the "save to server" button
        JFXButton createRemoteButton = new JFXButton("Save to server");
        createRemoteButton.setButtonType(JFXButton.ButtonType.RAISED);
        createRemoteButton.getStyleClass().addAll("unselectedButton");
        createRemoteButton.setOnMouseClicked(event -> {
            if (!this.getChildren().contains(apiKeyField)) {
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
            } else {
                snackbar.show("Please fill out the box above and press enter!", 2000);
            }
        });

        //Create the "clear server" button
        JFXButton clearServerButton = new JFXButton("Clear Server");
        clearServerButton.setButtonType(JFXButton.ButtonType.RAISED);
        clearServerButton.getStyleClass().addAll("unselectedButton");
        clearServerButton.setOnMouseClicked(event -> {
            if (!this.getChildren().contains(apiKeyField)) {
                //Make the BellTimes.txt empty
                FileWriter fw = null;
                BufferedWriter bw = null;
                try {
                    fw = new FileWriter("BellTimes.txt");
                    bw = new BufferedWriter(fw);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                        if (fw != null) {
                            fw.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                //Push the empty BellTimes.txt to the server
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
            } else {
                snackbar.show("Please fill out the box above and press enter!", 2000);
            }
        });

        //Set up the save button dropdown
        JFXNodesList buttons = new JFXNodesList();
        buttons.setSpacing(5);
        buttons.addAnimatedNode(submitButton);
        buttons.addAnimatedNode(createLocalButton);
        buttons.addAnimatedNode(createRemoteButton);
        buttons.addAnimatedNode(clearServerButton);

        //Add the save button dropdown to the node
        this.getChildren().add(buttons);
    }

    /**
     * Add a new ScheduleLine into the scrollPane
     */
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

    /**
     * Save the data to a folder in the documents, as well as appends it to the server BellTimes.txt file
     *
     * @return saveSuccessful
     */
    public boolean saveData() {
        //Initializes the save file data arraylist
        List<String> lines = new ArrayList<>();

        //Checks if there is a Title
        if (titleField.getText() == null || titleField.getText().equals("")) {
            snackbar.show("Error! Please enter a title!", 2000);
            return false;
        }

        //Checks if there is a Date
        if (datePicker.getValue() == null) {
            snackbar.show("Error! Please enter a date!", 2000);
            return false;
        }

        //Makes and adds the header (specifies which day should display the data)
        lines.add(datePicker.getValue().getMonth().getValue() + "");
        lines.add(datePicker.getValue().getDayOfMonth() + "");
        lines.add(datePicker.getValue().getYear() + "");
        lines.add(scrollPaneContent.getChildren().size() + 2 + "");

        //Adds the Title to the content
        lines.add(titleField.getText() + " ");
        lines.add("---------------");

        //Adds all the block/activity names
        for (Node node : scrollPaneContent.getChildren()) {
            ScheduleLine temp = (ScheduleLine) node;
            lines.add(temp.getBlockData());
        }

        //Adds the Date next to the title
        lines.add(datePicker.getValue().toString());
        lines.add("---------------");

        //Adds the "extra content"
        for (Node node : scrollPaneContent.getChildren()) {
            ScheduleLine temp = (ScheduleLine) node;
            lines.add(temp.getExtraData());
        }

        //Finds the user's home path
        String home = System.getenv("HOME");

        //Creates a folder in the Documents if it doesn't already exist
        File documents = new File(home + "/Documents/WHS Bell Schedule Editor");
        if (!documents.exists()) {
            documents.mkdir();
        }

        //Save a txt in the folder, labeled by date
        java.nio.file.Path file = Paths.get(home + "/Documents/WHS Bell Schedule Editor/" + datePicker.getValue().toString() + ".txt");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Appends the data onto the server grabbed txt file
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter("BellTimes.txt", true);
            bw = new BufferedWriter(fw);

            for (String s : lines) {
                bw.write(s + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    /**
     * Upload a file to dropbox
     *
     * @param currentFileLocation Location of the file one wants to upload
     * @param targetFileName      Name of file on Dropbox
     */
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

    /**
     * Create a field for the API key
     */
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
