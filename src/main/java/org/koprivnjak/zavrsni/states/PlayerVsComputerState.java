package org.koprivnjak.zavrsni.states;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import javafx.scene.layout.BorderPane;
import org.koprivnjak.zavrsni.ai.ComputerPlayer;
import org.koprivnjak.zavrsni.ui.BoardUI;
import org.koprivnjak.zavrsni.ui.Closable;

public class PlayerVsComputerState extends BorderPane implements Closable {
    private Board board;
    private BoardUI boardUI;

    private ComputerPlayer computerPlayer;

    public PlayerVsComputerState(Side playerSide, int depth){
        board = new Board();

        computerPlayer = new ComputerPlayer(board, depth);
        computerPlayer.setOnMoveFoundListener(move -> {
            board.doMove(move.getLan());
            boardUI.draw();
        });

        boardUI = new BoardUI();
        boardUI.setBoard(board);
        if(playerSide == Side.BLACK){
            boardUI.flip();
        }
        boardUI.setBoardClickListener(move -> {
            if (board.getSideToMove() != playerSide){
                return;
            }
            for (Move legalMove : board.legalMoves()){
                if(legalMove.equals(move)){
                    board.doMove(move);
                    boardUI.draw();
                    computerPlayer.findMove();
                    break;
                }
            }
        });
        setCenter(boardUI);
        if(playerSide == Side.BLACK){
            computerPlayer.findMove();
        }
    }
    @Override
    public void close() {
        computerPlayer.close();
    }
}
