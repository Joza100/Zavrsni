package org.koprivnjak.zavrsni.states;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.koprivnjak.zavrsni.networking.Communication;
import org.koprivnjak.zavrsni.networking.MovePacket;
import org.koprivnjak.zavrsni.networking.StartGamePacket;
import org.koprivnjak.zavrsni.ui.BoardUI;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OnlineState extends BorderPane implements State {

    class WaitForPlayerTask extends Task<Object> {
        @Override
        protected Socket call() throws Exception {
            socket = serverSocket.accept();
            return null;
        }
    }

    private static final double RIGHT_PANE_WIDTH = 300;

    private BoardUI boardUI;

    private VBox rightButtons;
    private Button resignButton;
    private Button drawButton;

    private Board board;

    private ServerSocket serverSocket;
    private Socket socket;
    private Communication communication;

    private boolean isServer;
    private String ipAddress;
    private int port;
    private Side side;

    public OnlineState(){
        boardUI = new BoardUI();
        boardUI.getStyleClass().add("setupPane");

        rightButtons = new VBox();
        rightButtons.setAlignment(Pos.CENTER_LEFT);
        //rightButtons.setOrientation(Orientation.VERTICAL);
        resignButton = new Button("Resign");
        resignButton.setMinSize(100, 50);
        drawButton = new Button("Draw");
        drawButton.setMinSize(100, 50);
        rightButtons.getChildren().addAll(resignButton, drawButton);
        rightButtons.getStyleClass().add("setupPane");
        rightButtons.setMinWidth(RIGHT_PANE_WIDTH);

        setRight(rightButtons);
        setCenter(boardUI);
    }

    public OnlineState(int port, Side side){
        this();
        isServer = true;
        this.side = side;
        this.port = port;
    }
    public OnlineState(int port, String ipAddress){
        this();
        isServer = false;
        this.port = port;
        this.ipAddress = ipAddress;
        this.side = Side.WHITE;
    }

    @Override
    public void start() {
        if(isServer) {
            try {
                serverSocket = new ServerSocket(port);
                WaitForPlayerTask waitForPlayerTask = new WaitForPlayerTask();
                waitForPlayerTask.setOnSucceeded(event -> startCommunication());
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                executorService.execute(waitForPlayerTask);
                executorService.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                socket = new Socket(ipAddress, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startCommunication();
        }
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
            } else if (!isServer){
                if(packet instanceof StartGamePacket startGamePacket){
                    side = Side.fromValue(startGamePacket.getSide());
                    boardUI.flip(side);
                }
            }
        });

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
        boardUI.setBoard(board);
        boardUI.flip(side);
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
