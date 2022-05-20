package org.koprivnjak.zavrsni.ui;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class MoveLabel extends Label {
    private BoardUI boardUI;
    private int move;

    public MoveLabel(BoardUI boardUI, String text, int move) {
        super(text);
        this.boardUI = boardUI;
        this.move = move;
        getStyleClass().add("moveLabel");
        setOnMouseClicked(this::onMouseClicked);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        boardUI.setSelectedMove(move);
        boardUI.draw();
    }
    public void showAsSelected(){
        getStyleClass().add("moveLabelSelected");
    }
    public void showAsNotSelected(){
        getStyleClass().remove("moveLabelSelected");
    }

    public int getMove() {
        return move;
    }
}
