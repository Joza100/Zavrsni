package org.koprivnjak.zavrsni.ui;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class MoveLabel extends Label {
    private MovePane movePane;
    private int move;

    public MoveLabel(MovePane movePane, String text, int move) {
        super(text);
        this.movePane = movePane;
        this.move = move;
        getStyleClass().add("moveLabel");
        setOnMouseClicked(this::onMouseClicked);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        movePane.setSelectedMoveLabel(this);
    }

    public int getMove() {
        return move;
    }
}
