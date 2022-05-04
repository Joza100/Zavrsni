package org.koprivnjak.zavrsni.ui;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


public class MoveKeyboardListener implements EventHandler<KeyEvent> {

    private MovePane movePane;

    public MoveKeyboardListener(MovePane movePane) {
        this.movePane = movePane;
    }

    @Override
    public void handle(KeyEvent event) {
        if(event.getEventType() == KeyEvent.KEY_PRESSED){
            if (event.getCode() == KeyCode.LEFT){
                movePane.selectMoveLeft();
            } else if (event.getCode() == KeyCode.RIGHT){
                movePane.selectMoveRight();
            } else if (event.getCode() == KeyCode.UP){
                movePane.selectMoveZero();
            } else if (event.getCode() == KeyCode.DOWN){
                movePane.selectLastMove();
            }
            event.consume();
        }
    }
}
