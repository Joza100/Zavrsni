package org.koprivnjak.zavrsni.states;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import javafx.concurrent.Task;
import javafx.scene.layout.BorderPane;
import org.koprivnjak.zavrsni.networking.Communication;
import org.koprivnjak.zavrsni.networking.MovePacket;
import org.koprivnjak.zavrsni.ui.BoardUI;
import org.koprivnjak.zavrsni.ui.Closable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OnlineState extends BorderPane implements Closable {

    class WaitForPlayerTask extends Task<Object> {
        @Override
        protected Socket call() throws Exception {
            socket = serverSocket.accept();
            return null;
        }
    }

    private Board board;
    private BoardUI boardUI;

    private ServerSocket serverSocket;
    private Socket socket;
    private Communication communication;

    private boolean isServer;

    private Side side;

    public OnlineState(int port){
        isServer = true;
        side = Side.WHITE;
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

    }
    public OnlineState(int port, String ipAddress){
        isServer = false;
        side = Side.BLACK;
        try {
            socket = new Socket(ipAddress, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startCommunication();
    }

    private void startCommunication(){
        boardUI = new BoardUI();
        setCenter(boardUI);
        board = new Board();
        boardUI.setBoard(board);
        communication = new Communication(socket);
        communication.setPacketReceivedListener(packet -> {
            if(packet instanceof MovePacket movePacket) {
                Move move = new Move(movePacket.getLan(), null);
                board.doMove(move);
                boardUI.draw();
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
