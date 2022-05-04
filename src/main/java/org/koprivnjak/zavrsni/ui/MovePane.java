package org.koprivnjak.zavrsni.ui;

import com.github.bhlangonijr.chesslib.BoardEvent;
import com.github.bhlangonijr.chesslib.BoardEventListener;
import com.github.bhlangonijr.chesslib.move.Move;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class MovePane extends ScrollPane implements BoardEventListener {

    private GridPane gridPane;
    private int selectedMove;
    private MoveLabel selectedMoveLabel;
    private int currentRow;
    private int currentColumn;
    private int moveCount;

    private BoardUI boardUI;

    public MovePane(BoardUI boardUI){
        this.boardUI = boardUI;
        setMinSize(340, 500);
        setMaxSize(340, 500);

        moveCount = 0;
        currentColumn = 1;
        currentRow = 0;
        getStyleClass().add("movePane");
        gridPane = new GridPane();
        setContent(gridPane);

        ColumnConstraints columnConstraints1 = new ColumnConstraints();
        columnConstraints1.setMinWidth(15);
        gridPane.getColumnConstraints().add(columnConstraints1);
        ColumnConstraints columnConstraints2 = new ColumnConstraints();
        columnConstraints2.setMinWidth(130);
        gridPane.getColumnConstraints().add(columnConstraints2);
        ColumnConstraints columnConstraints3 = new ColumnConstraints();
        columnConstraints3.setMinWidth(130);
        gridPane.getColumnConstraints().add(columnConstraints3);

        Label numberLabel = new Label("    " + (currentRow + 1) + "    ");
        numberLabel.getStyleClass().add("moveNumberLabel");
        gridPane.add(numberLabel, 0, currentRow);
    }

    @Override
    public void onEvent(BoardEvent event) {
        Platform.runLater(() -> {
            moveCount++;
            Move move = (Move) event;
            MoveLabel label = new MoveLabel(this," " + move.toString(), moveCount);
            gridPane.add(label, currentColumn, currentRow);
            //setSelectedMoveLabel(label);
            currentColumn++;

            if (currentColumn == 3) {
                currentRow++;
                Label numberLabel = new Label("    " + (currentRow + 1) + "    ");
                numberLabel.getStyleClass().add("moveNumberLabel");
                gridPane.add(numberLabel, 0, currentRow);
                currentColumn = 1;
            }
        });
    }

    public BoardUI getBoardUI() {
        return boardUI;
    }

    public void setSelectedMoveLabel(MoveLabel selectedMoveLabel) {
        boardUI.drawPreviousPosition(selectedMoveLabel.getMove());
        selectedMoveLabel.getStyleClass().add("moveLabelSelected");
        if(this.selectedMoveLabel != null){
            this.selectedMoveLabel.getStyleClass().remove("moveLabelSelected");
        }
        selectedMove = selectedMoveLabel.getMove();
        this.selectedMoveLabel = selectedMoveLabel;
    }
    public void selectMoveLeft(){
        selectedMove--;
        if(selectedMove == 0){
            selectMoveZero();
            return;
        } else if (selectedMove < 0){
            selectedMove = 0;
            selectMoveZero();
        }
        for (Node node : gridPane.getChildren()){
            if (node instanceof MoveLabel moveLabel){
                if(moveLabel.getMove() == selectedMove){
                    setSelectedMoveLabel(moveLabel);
                }
            }
        }
    }
    public void selectMoveRight(){
        selectedMove++;
        if(selectedMove > moveCount){
            selectedMove = moveCount;
            return;
        }
        for (Node node : gridPane.getChildren()){
            if (node instanceof MoveLabel moveLabel){
                if(moveLabel.getMove() == selectedMove){
                    setSelectedMoveLabel(moveLabel);
                }
            }
        }
    }
    public void selectMoveZero(){
        selectedMove = 0;
        if(selectedMoveLabel != null) {
            selectedMoveLabel.getStyleClass().remove("moveLabelSelected");
            selectedMoveLabel = null;
        }
        boardUI.drawPreviousPosition(0);
    }
    public void selectLastMove(){
        selectedMove = moveCount;
        for (Node node : gridPane.getChildren()){
            if (node instanceof MoveLabel moveLabel){
                if(moveLabel.getMove() == selectedMove){
                    setSelectedMoveLabel(moveLabel);
                }
            }
        }
    }
}
