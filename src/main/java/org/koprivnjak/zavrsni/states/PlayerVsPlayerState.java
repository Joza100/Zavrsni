package org.koprivnjak.zavrsni.states;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.Move;
import javafx.scene.layout.BorderPane;
import org.koprivnjak.zavrsni.ui.BoardUI;
import org.koprivnjak.zavrsni.ui.Closable;

public class PlayerVsPlayerState extends BorderPane implements Closable {
    private Board board;
    private BoardUI boardUI;

    public PlayerVsPlayerState(){
        board = new Board();

        boardUI = new BoardUI();
        boardUI.setBoard(board);
        boardUI.setBoardClickListener(move -> {

            for (Move legalMove : board.legalMoves()){
                if(legalMove.equals(move)){
                    board.doMove(move);
                    boardUI.draw();
                    break;
                }
            }
        });
        setCenter(boardUI);
    }
    @Override
    public void close() {
    }
}
