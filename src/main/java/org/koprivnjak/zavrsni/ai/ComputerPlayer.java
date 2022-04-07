package org.koprivnjak.zavrsni.ai;

import com.github.bhlangonijr.chesslib.Board;
import javafx.concurrent.Task;
import net.andreinc.neatchess.client.UCI;
import net.andreinc.neatchess.client.UCIResponse;
import net.andreinc.neatchess.client.model.Analysis;
import net.andreinc.neatchess.client.model.Move;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComputerPlayer {
    class FindMoveTask extends Task<Move> {

        @Override
        protected Move call() {

            uci.positionFen(board.getFen());
            UCIResponse<Analysis> response = uci.analysis(16);
            var analysis = response.getResultOrThrow();

// Best move
            System.out.println("Best move: " + analysis.getBestMove());
            System.out.println("Is Draw: " + analysis.isDraw());
            System.out.println("Is Mate: " + analysis.isMate());

// Possible best moves
            var moves = analysis.getAllMoves();
            moves.forEach((idx, move) -> System.out.println("\t" + move));
            return analysis.getBestMove();
        }
    }
    private Board board;

    private UCI uci;

    private OnMoveFoundListener onMoveFoundListener;

    public ComputerPlayer(Board board){
        this.board = board;

        uci = new UCI();
        uci.start("stockfish.exe");
        uci.uciNewGame();
    }

    public void findMove() {
        FindMoveTask task = new FindMoveTask();
        task.setOnSucceeded(event -> onMoveFoundListener.onMoveFound(task.getValue()));
        ExecutorService executorService
                = Executors.newFixedThreadPool(1);
        executorService.execute(task);
        executorService.shutdown();
    }

    public void setOnMoveFoundListener(OnMoveFoundListener onMoveFoundListener){
        this.onMoveFoundListener = onMoveFoundListener;
    }
    public void close() {
        uci.close();
    }
}