package org.koprivnjak.zavrsni.ui;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class MoveKeyboardListener implements EventHandler<KeyEvent> {

    private BoardUI boardUI;

    public MoveKeyboardListener(BoardUI boardUI) {
        this.boardUI = boardUI;
    }

    @Override
    public void handle(KeyEvent event) {
        if(event.getEventType() == KeyEvent.KEY_PRESSED){
            if (event.getCode() == KeyCode.LEFT){
                boardUI.selectedMoveLeft();
            } else if (event.getCode() == KeyCode.RIGHT){
                boardUI.selectedMoveRight();
            } else if (event.getCode() == KeyCode.UP){
                boardUI.setSelectedMove(0);
            } else if (event.getCode() == KeyCode.DOWN){
                boardUI.setSelectedMove(boardUI.getMoveCount());
            }
            boardUI.draw();
            event.consume();
        }
    }
}
