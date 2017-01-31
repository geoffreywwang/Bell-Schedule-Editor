import com.jfoenix.controls.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.time.format.DateTimeFormatter;

/**
 * Created by geoffrey_wang on 1/30/17.
 */
public class ScheduleLine extends Pane{
    private int state = 0; //0 is none, 1 is time, 2 is message, -1 is KILL
    private boolean optionsIsClosed = true;
    private JFXNodesList buttons;
    private Node extraNode = null;
    private JFXDatePicker startTime, endTime;
    private HBox main;
    private JFXTextField textField, messageField;

    public ScheduleLine(JFXButton killButton){
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

        main = new HBox();
        main.prefWidthProperty().bind(this.widthProperty());
        main.setSpacing(10);
        main.setPadding(new Insets(8,5,5,8));

        buttons = new JFXNodesList();
        buttons.setSpacing(-25);
        buttons.setMaxHeight(20);

        main.getChildren().add(killButton);

        JFXButton optionsButton = new JFXButton("Extra Options");
        optionsButton.setId("optionsButton");
        optionsButton.setOnMouseClicked(event -> {
            if(optionsIsClosed){
                buttons.setSpacing(5);
            }else{
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
            main.setHgrow(messageField, Priority.ALWAYS);
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


        textField = new JFXTextField();
        textField.setPromptText("Block/Activity Name");
        textField.setPrefWidth(200);
        textField.setOnMouseClicked(event -> {
            closeButtons();
        });

        main.getChildren().setAll(killButton, buttons,textField);
        this.getChildren().setAll(main);
    }

    public void closeButtons(){
        if(!optionsIsClosed){
            buttons.animateList();
            optionsIsClosed = true;
            buttons.setSpacing(-25);
        }
    }

    private void resetButtonColors(){
        for(Node node:buttons.getChildren()){
            if(node.getStyleClass().contains("selectedButton")) {
                node.getStyleClass().remove("selectedButton");
                node.getStyleClass().addAll("unselectedButton");
            }
        }
    }

    private void setButtonSelected(JFXButton button){
        if(button.getStyleClass().contains("unselectedButton")) {
            button.getStyleClass().remove("unselectedButton");
            button.getStyleClass().addAll("selectedButton");
        }
    }

    private void resetExtraNode(){
        if(main.getChildren().contains(extraNode)) {
            main.getChildren().remove(extraNode);
        }
    }

    public String getBlockData(){
        return textField.getText() + " ";
    }

    public String getExtraData(){
        if(state == 0){
            return " ";
        }else if(state == 1){
            String start = "";
            String end = "";
            if(startTime.getTime() != null) {
                start = startTime.getTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
                start = start.substring(0,start.length()-3);
                if(startTime.getTime().getHour() != 0) {
                    if (startTime.getTime().getHour() < 10 || (startTime.getTime().getHour() > 12 && startTime.getTime().getHour() < 22)) {
                        start = start.substring(1);
                    }
                }
            }
            if(endTime.getTime() != null) {
                end = endTime.getTime().format(DateTimeFormatter.ofPattern("hh:mm a"));
                end = end.substring(0,end.length()-3);
                if(endTime.getTime().getHour() != 0) {
                    if (endTime.getTime().getHour() < 10 || (endTime.getTime().getHour() > 12 && endTime.getTime().getHour() < 22)) {
                        end = end.substring(1);
                    }
                }
            }
            return start + " - " + end;
        }else{
            return messageField.getText()+ " ";
        }
    }
}
