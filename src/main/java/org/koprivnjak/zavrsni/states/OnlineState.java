package org.koprivnjak.zavrsni.states;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.BoardEventType;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.koprivnjak.zavrsni.networking.*;
import org.koprivnjak.zavrsni.ui.BoardUI;
import org.koprivnjak.zavrsni.ui.GameStateLabel;
import org.koprivnjak.zavrsni.ui.MoveKeyboardListener;
import org.koprivnjak.zavrsni.ui.MovePane;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class OnlineState extends BorderPane implements State {


    //private static final double RIGHT_PANE_WIDTH = 300;

    private BoardUI boardUI;


    private AnchorPane rightPane;
    private MovePane movePane;
    private HBox rightPaneHBox;
    private VBox rightButtons;
    private Button resignButton;
    private Button drawButton;

    private Button drawAcceptButton;
    private Button drawDeclineButton;
    private GameStateLabel gameStateLabel;

    private Board board;

    private ServerSocket serverSocket;
    private Socket socket;
    private Communication communication;

    private boolean isServer;
    private Side side;


    public OnlineState(){
        boardUI = new BoardUI();

        rightButtons = new VBox();
        rightButtons.setAlignment(Pos.CENTER_LEFT);
        //rightButtons.setOrientation(Orientation.VERTICAL);
        resignButton = new Button("Resign");
        resignButton.setMinSize(100, 50);
        drawButton = new Button("Draw");
        drawButton.setMinSize(100, 50);
        rightButtons.getChildren().addAll(resignButton, drawButton);
        rightButtons.getStyleClass().add("setupPane");

        movePane = new MovePane(boardUI);
        MoveKeyboardListener moveKeyboardListener = new MoveKeyboardListener(boardUI);
        movePane.setOnKeyPressed(moveKeyboardListener);
        boardUI.addEventFilter(KeyEvent.ANY, moveKeyboardListener);

        gameStateLabel = new GameStateLabel();
        rightPaneHBox = new HBox();
        AnchorPane.setBottomAnchor(rightPaneHBox, 300.0);
        AnchorPane.setRightAnchor(rightPaneHBox, 70.0);
        rightPaneHBox.getChildren().addAll(movePane, rightButtons);

        rightPane = new AnchorPane();
        rightPane.getChildren().addAll(rightPaneHBox, gameStateLabel);


        setRight(rightPane);
        setCenter(boardUI);


        drawAcceptButton = new Button("Accept draw");
        drawDeclineButton = new Button("Decline draw");
        drawAcceptButton.setMinSize(100, 50);
        drawDeclineButton.setMinSize(100, 50);

    }

    public OnlineState(ServerSocket serverSocket, Socket socket, Side side){
        this();
        isServer = true;
        this.side = side;
        this.serverSocket = serverSocket;
        this.socket = socket;
    }
    public OnlineState(Socket socket){
        this();
        isServer = false;
        this.socket = socket;
        this.side = Side.WHITE;
    }

    @Override
    public void start() {
        startCommunication();
    }

    private void startCommunication(){
        communication = new Communication(socket);
        if(isServer) {
            communication.sendPacket(new StartGamePacket(side.flip().name()));
        }
        communication.setPacketReceivedListener(packet -> {
            if(packet instanceof MovePacket movePacket) {
                Move move = new Move(movePacket.getLan(), null);
                board.doMove(move);
                boardUI.draw();
            } else if (packet instanceof ResignPacket){
                win();
            } else if (packet instanceof DrawPacket drawPacket) {
                if(drawPacket.getDrawRequest() == DrawPacket.DrawRequest.OFFER){
                    Platform.runLater(() -> rightButtons.getChildren().addAll(drawAcceptButton, drawDeclineButton));
                    gameStateLabel.show("Your opponent offered a draw.", "white");
                } else if (drawPacket.getDrawRequest() == DrawPacket.DrawRequest.ACCEPT){
                    gameStateLabel.hide();
                    Platform.runLater(this::draw);
                } else if (drawPacket.getDrawRequest() == DrawPacket.DrawRequest.DECLINE){
                    gameStateLabel.hide();
                }
            } else if (!isServer){
                if(packet instanceof StartGamePacket startGamePacket){
                    side = Side.fromValue(startGamePacket.getSide());
                    boardUI.flip(side);
                }
            }
        });
        resignButton.setOnMouseClicked(this::onResignClicked);
        drawButton.setOnMouseClicked(this::onDrawOfferClicked);
        drawAcceptButton.setOnMouseClicked(this::onDrawAcceptClicked);
        drawDeclineButton.setOnMouseClicked(this::onDrawDeclineClicked);

        boardUI.setBoardClickListener(move -> {
            for (Move legalMove : board.legalMoves()){
                if(move.equals(legalMove) && board.getSideToMove() == side){
                    board.doMove(move);
                    communication.sendPacket(new MovePacket(move.toString()));
                    boardUI.draw();
                }
            }
        });
        board = new Board();
        board.addEventListener(BoardEventType.ON_MOVE, movePane);
        board.addEventListener(BoardEventType.ON_MOVE, (event) -> {
            if(board.isMated()){
                if (board.getSideToMove() == side){
                    lose();
                } else {
                    win();
                }
            } else if (board.isDraw()){
                draw();
            }
        });
        boardUI.setBoard(board);
        boardUI.flip(side);
    }

    private void onDrawDeclineClicked(MouseEvent mouseEvent) {
        communication.sendPacket(new DrawPacket(DrawPacket.DrawRequest.DECLINE));
        gameStateLabel.hide();
        rightButtons.getChildren().remove(drawAcceptButton);
        rightButtons.getChildren().remove(drawDeclineButton);
    }

    private void onDrawAcceptClicked(MouseEvent mouseEvent) {
        communication.sendPacket(new DrawPacket(DrawPacket.DrawRequest.ACCEPT));
        gameStateLabel.hide();
        rightButtons.getChildren().remove(drawAcceptButton);
        rightButtons.getChildren().remove(drawDeclineButton);
        draw();
    }

    private void onDrawOfferClicked(MouseEvent mouseEvent) {
        communication.sendPacket(new DrawPacket(DrawPacket.DrawRequest.OFFER));
        gameStateLabel.show("Draw offered...", "white");
    }

    private void onResignClicked(MouseEvent event){
        communication.sendPacket(new ResignPacket());
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
        drawButton.setOnMouseClicked(null);
        resignButton.setOnMouseClicked(null);
        boardUI.setBoardClickListener(null);
    }

    @Override
    public void close() {
        if(communication != null){
            communication.close();
        }
        try {
            if(isServer) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
