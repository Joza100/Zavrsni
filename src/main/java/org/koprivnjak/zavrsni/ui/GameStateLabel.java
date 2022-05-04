package org.koprivnjak.zavrsni.ui;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class GameStateLabel extends Label {

    public GameStateLabel() {
        AnchorPane.setBottomAnchor(this, 255.0);
        AnchorPane.setRightAnchor(this, 70.0);
        this.getStyleClass().add("gameStateLabel");
    }

    public void show(String text, String color){
        Platform.runLater(() -> {
            getStyleClass().removeAll("red", "green", "white");
            setText(text);
            getStyleClass().add(color);
        });
    }
    public void hide(){
        Platform.runLater(() -> this.setText(""));
    }
}
