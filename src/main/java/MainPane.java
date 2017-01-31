import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by geoffrey_wang on 1/30/17.
 */
public class MainPane extends VBox {

    private ScrollPane scrollPane;
    private JFXDatePicker datePicker;
    private VBox vBox;
    private JFXTextField titleField;

    public MainPane(){
        //Main Pane Settings
        this.setSpacing(5);
        this.setAlignment(Pos.CENTER);

        //Adding the Date Picker to the pane
        datePicker = new JFXDatePicker();
        this.getChildren().setAll(datePicker);

        //Adding the "add line button"
        JFXButton addLineButton = new JFXButton("+");
        addLineButton.setId("addButton");
        addLineButton.setOnMouseClicked(event -> {
            addLine();
        });

        titleField = new JFXTextField();
        titleField.setPromptText("Title");

        //Putting the add line button and the date picker in the same row
        HBox hBox = new HBox(titleField,datePicker,addLineButton);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(10);
        this.getChildren().add(hBox);

        //Initializing the vBox
        vBox = new VBox();
        vBox.setSpacing(5);
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPadding(new Insets(5,0,0,0));
        vBox.setId("scrollPane");

        //Initializing the scrollPane
        scrollPane = new ScrollPane(vBox);
        this.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToWidth(true);
        scrollPane.setId("scrollPane");
        this.getChildren().add(scrollPane);


        //Adding the submit button to the bottom
        JFXButton submitButton = new JFXButton("Submit");
        submitButton.setOnMouseClicked(event -> {
            saveData();
        });
        submitButton.setId("submitButton");
        this.getChildren().add(submitButton);
    }

    private void addLine(){
        JFXButton killButton = new JFXButton("-");
        killButton.setId("removeButton");

        ScheduleLine line = new ScheduleLine(killButton);
        line.getStyleClass().add("scheduleLine");
        killButton.setOnMouseClicked(event -> {
            vBox.getChildren().remove(line);
        });
        vBox.getChildren().add(line);
    }

    public boolean saveData(){
        JFXSnackbar snackbar = new JFXSnackbar(this);
        List<String> lines = new ArrayList<>();

        if(titleField.getText() == null|| titleField.getText().equals("")){
            snackbar.show("Error! Please enter a title!",2000);
            return false;
        }

        if(datePicker.getValue() == null){
            snackbar.show("Error! Please enter a date!",2000);
            return false;
        }

        lines.add(datePicker.getValue().getMonth().getValue() + "");
        lines.add(datePicker.getValue().getDayOfMonth() + "");
        lines.add(datePicker.getValue().getYear() + "");
        lines.add(vBox.getChildren().size()+2 + "");

        lines.add(titleField.getText() + " ");
        lines.add("---------------");


        for(Node node: vBox.getChildren()){
            ScheduleLine temp = (ScheduleLine) node;
            lines.add(temp.getBlockData());
        }

        lines.add(datePicker.getValue().toString());
        lines.add("---------------");

        for(Node node: vBox.getChildren()){
            ScheduleLine temp = (ScheduleLine) node;
            lines.add(temp.getExtraData());
        }

        String home = System.getenv("HOME");

        File documents = new File(home+"/Documents/WHS Bell Schedule Output");

        if(!documents.exists())
        {
            documents.mkdir();
        }

        java.nio.file.Path file = Paths.get(home+"/Documents/WHS Bell Schedule Output/BellTimes.txt");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        snackbar.show("Saved!",2000);
        return true;
    }
}
