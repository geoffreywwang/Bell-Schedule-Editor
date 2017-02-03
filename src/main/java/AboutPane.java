import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * This pane displays and references all the third party libraries used in this application
 */
public class AboutPane extends VBox {

    /**
     * Create the pane and populate it with labels
     */
    public AboutPane() {
        ArrayList<Label> lines = new ArrayList<>();

        lines.add(new Label("About:"));
        lines.add(new Label("\t Version: " + Main.VERSION));
        lines.add(new Label("\t This app was created by Geoffrey Wang and is licensed under the MIT License."));
        lines.add(new Label(" "));
        lines.add(new Label("The following third party libraries were used in this application:"));
        lines.add(new Label(" "));
        lines.add(new Label("\t Dropbox API v2"));
        lines.add(new Label("\t \t Copyright (c) 2017 Dropbox"));
        lines.add(new Label(" "));
        lines.add(new Label("\t Appbundle Maven Plugin"));
        lines.add(new Label("\t \t Created by Takashi AOKI and other contributors and licensed under the Apache License, Version 2.0"));
        lines.add(new Label(" "));
        lines.add(new Label("\t JFoenix UI library"));
        lines.add(new Label("\t \t Created by the JFoenix team and licensed under the Apache License, Version 2.0"));
        lines.add(new Label(" "));

        this.getChildren().addAll(lines);
    }
}
