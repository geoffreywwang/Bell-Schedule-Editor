import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.time.format.DateTimeFormatter;

/**
 * This is the node for one line in the bell schedule
 */
public class ScheduleLine extends Pane {

    //Node State
    private int state = 0; //EXTRA NODE STATE: 0 is none, 1 is time, 2 is message, -1 is remove
    private boolean optionsIsClosed = true; //Dropdown state

    //Nodes
    private HBox main; //Main content pane
    private JFXNodesList buttons; //Dropdown
    private Node extraNode = null; //Placeholder for the extra node
    private JFXDatePicker startTime, endTime; //Extra node - Time
    private JFXTextField textField, messageField; //Text fields/Extra node - Message

    /**
     * Initialize pane and set everything up
     *
     * @param removeButton Instance of the delete button
     */
    public ScheduleLine(JFXButton removeButton) {

        //Initialize for EXTRA NODE: TIME
        startTime = new JFXDatePicker();
        startTime.setShowTime(true);
        startTime.setOnMouseClicked(event1 -> {
            closeButtons();
        });
        startTime.setPrefWidth(150);

        endTime = new JFXDatePicker();
        endTime.setShowTime(true);
        endTime.setOnMouseClicked(event1 -> {
            closeButtons();
        });
        endTime.setPrefWidth(150);

        //Initialize Main Content Node
        main = new HBox();
        main.prefWidthProperty().bind(this.widthProperty());
        main.setSpacing(10);
        main.setPadding(new Insets(8, 5, 5, 8));

        //Initialize the dropdown
        buttons = new JFXNodesList();
        buttons.setSpacing(-25);
        buttons.setMaxHeight(20);

        //Initialize extra options dropdown
        JFXButton optionsButton = new JFXButton("Extra Options");
        optionsButton.setId("optionsButton");
        optionsButton.setOnMouseClicked(event -> {
            if (optionsIsClosed) {
                buttons.setSpacing(5);
            } else {
                buttons.setSpacing(-25);
            }
            optionsIsClosed = !optionsIsClosed;
        });
        buttons.addAnimatedNode(optionsButton);

        JFXButton timeButton = new JFXButton("Add Time");
        timeButton.getStyleClass().addAll("unselectedButton");
        timeButton.setButtonType(JFXButton.ButtonType.RAISED);
        timeButton.setOnMouseClicked(event -> {
            state = 1;
            resetButtonColors();
            setButtonSelected(timeButton);

            resetExtraNode();

            extraNode = new HBox(startTime, endTime);
            main.getChildren().addAll(extraNode);
        });
        buttons.addAnimatedNode(timeButton);

        JFXButton messageButton = new JFXButton("Add Message");
        messageButton.getStyleClass().addAll("unselectedButton");
        messageButton.setButtonType(JFXButton.ButtonType.RAISED);
        messageButton.setOnMouseClicked(event -> {
            state = 2;
            resetButtonColors();
            setButtonSelected(messageButton);

            resetExtraNode();
            messageField = new JFXTextField();
            messageField.setPromptText("Message");
            messageField.setOnMouseClicked(event1 -> {
                closeButtons();
            });
            HBox.setHgrow(messageField, Priority.ALWAYS);
            extraNode = messageField;
            main.getChildren().addAll(extraNode);
        });
        buttons.addAnimatedNode(messageButton);

        JFXButton noneButton = new JFXButton("None");
        noneButton.getStyleClass().addAll("unselectedButton");
        noneButton.setButtonType(JFXButton.ButtonType.RAISED);
        noneButton.setOnMouseClicked(event -> {
            state = 0;
            resetButtonColors();
            resetExtraNode();
            extraNode = null;
        });
        buttons.addAnimatedNode(noneButton);


        //Create a text field for the block name
        textField = new JFXTextField();
        textField.setPromptText("Block/Activity Name");
        textField.setPrefWidth(200);
        textField.setOnMouseClicked(event -> {
            closeButtons();
        });

        //Add all content to main and set this to point to main
        main.getChildren().setAll(removeButton, buttons, textField);
        this.getChildren().setAll(main);
    }

    /**
     * Close the options dropdown if it is open
     */
    public void closeButtons() {
        if (!optionsIsClosed) {
            buttons.animateList();
            optionsIsClosed = true;
            buttons.setSpacing(-25);
        }
    }

    /**
     * Reset the dropdown colors to white
     */
    private void resetButtonColors() {
        for (Node node : buttons.getChildren()) {
            if (node.getStyleClass().contains("selectedButton")) {
                node.getStyleClass().remove("selectedButton");
                node.getStyleClass().addAll("unselectedButton");
            }
        }
    }

    /**
     * Set a specific button to be "selected" (Blue)
     *
     * @param button Button that should be changed
     */
    private void setButtonSelected(JFXButton button) {
        if (button.getStyleClass().contains("unselectedButton")) {
            button.getStyleClass().remove("unselectedButton");
            button.getStyleClass().addAll("selectedButton");
        }
    }

    /**
     * Remove the current extra node from the pane
     */
    private void resetExtraNode() {
        if (main.getChildren().contains(extraNode)) {
            main.getChildren().remove(extraNode);
        }
    }

    /**
     * Returns the text inside the "block/activity" textfield
     *
     * @return Block name
     */
    public String getBlockData() {
        return textField.getText() + " ";
    }


    /**
     * Gets the data in the extra node if there is one selected
     *
     * @return Extra data Ex. Time or Message
     */
    public String getExtraData() {
        if (state == 0) {
            return " ";
        } else if (state == 1) {
            String start = "";
            String end = "";
            if (startTime.getTime() != null) {
                start = startTime.getTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
                start = start.substring(0, start.length() - 3);
                if (startTime.getTime().getHour() != 0) {
                    if (startTime.getTime().getHour() < 10 || (startTime.getTime().getHour() > 12 && startTime.getTime().getHour() < 22)) {
                        start = start.substring(1);
                    }
                }
            }
            if (endTime.getTime() != null) {
                end = endTime.getTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
                end = end.substring(0, end.length() - 3);
                if (endTime.getTime().getHour() != 0) {
                    if (endTime.getTime().getHour() < 10 || (endTime.getTime().getHour() > 12 && endTime.getTime().getHour() < 22)) {
                        end = end.substring(1);
                    }
                }
            }
            return start + " - " + end;
        } else {
            return messageField.getText() + " ";
        }
    }
}
