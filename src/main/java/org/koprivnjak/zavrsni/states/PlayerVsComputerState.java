package org.koprivnjak.zavrsni.states;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.BoardEventType;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.koprivnjak.zavrsni.ai.ComputerPlayer;
import org.koprivnjak.zavrsni.ui.BoardUI;
import org.koprivnjak.zavrsni.ui.GameStateLabel;
import org.koprivnjak.zavrsni.ui.MoveKeyboardListener;
import org.koprivnjak.zavrsni.ui.MovePane;

public class PlayerVsComputerState extends BorderPane implements State {
    private Board board;
    private BoardUI boardUI;

    private AnchorPane rightPane;
    private HBox rightPaneHBox;
    private MovePane movePane;
    private VBox rightButtons;
    private Button resignButton;

    private GameStateLabel gameStateLabel;

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
        if(playerSide == Side.BLACK) {
            computerPlayer.findMove();
        }
        gameStateLabel = new GameStateLabel();

        rightButtons = new VBox();
        rightButtons.setAlignment(Pos.CENTER_LEFT);
        rightButtons.getStyleClass().add("setupPane");

        resignButton = new Button("Resign");
        resignButton.setMinSize(100, 50);
        resignButton.setOnMouseClicked(this::onResignClicked);
        rightButtons.getChildren().add(resignButton);

        movePane = new MovePane(boardUI);
        MoveKeyboardListener moveKeyboardListener = new MoveKeyboardListener(movePane);
        boardUI.addEventFilter(KeyEvent.ANY, moveKeyboardListener);
        movePane.setOnKeyPressed(moveKeyboardListener);
        board.addEventListener(BoardEventType.ON_MOVE, movePane);
        gameStateLabel = new GameStateLabel();

        rightPaneHBox = new HBox();
        AnchorPane.setBottomAnchor(rightPaneHBox, 300.0);
        AnchorPane.setRightAnchor(rightPaneHBox, 70.0);
        rightPaneHBox.getChildren().addAll(movePane, rightButtons);

        rightPane = new AnchorPane();

        rightPane.getChildren().addAll(rightPaneHBox, gameStateLabel);
        AnchorPane.setRightAnchor(movePane,70.0);
        AnchorPane.setBottomAnchor(movePane, 300.0);


        setCenter(boardUI);
        setRight(rightPane);

        board.addEventListener(BoardEventType.ON_MOVE, (event) -> {
            if(board.isMated()){
                if (board.getSideToMove() == playerSide){
                    lose();
                } else {
                    win();
                }
            } else if (board.isDraw()){
                draw();
            }
        });
    }

    private void onResignClicked(MouseEvent mouseEvent) {
        lose();
    }

    private void win(){
        gameStateLabel.show("You have won!", "green");
        stopGame();
    }
    private void lose(){
        gameStateLabel.show("You have lost!", "red");
        stopGame();
    }
    private void draw(){
        gameStateLabel.show("Draw", "white");
        stopGame();
    }
    private void stopGame(){
        boardUI.setBoardClickListener(null);
    }

    @Override
    public void start() {

    }
    @Override
    public void close() {
        computerPlayer.close();
    }
}
