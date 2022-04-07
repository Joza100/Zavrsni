package org.koprivnjak.zavrsni.ui;


import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.koprivnjak.zavrsni.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class BoardUI extends Pane {
    private static final Color LIGHT_SQUARE = Color.rgb(240, 217, 181);
    private static final Color DARK_SQUARE = Color.rgb(181, 136, 99);
    private static final Color MOVE_SQUARE = new Color(0.5, 0.5, 0.5, 0.4);
    private static final Color CHECK_SQUARE = new Color(0.5, 0, 0, 0.5);
    private static final Image PIECE_SET = new Image("/PieceSet.png");


    //Sizes
    private double width;
    private double height;
    private double dimensions;
    private double squareDimensions;

    private Canvas canvas;

    private Board board;

    private List<PieceDraw> pieceDrawList;
    private PieceDraw selectedPieceDraw;

    private BoardClickListener boardClickListener;

    public BoardUI() {
        pieceDrawList = new ArrayList<>();
        canvas = new ResizableCanvas();
        getChildren().add(canvas);
        widthProperty().addListener((observable, oldValue, newValue) -> {
                    width = newValue.doubleValue();
                    draw();
                }
        );
        heightProperty().addListener((observable, oldValue, newValue) -> {
                    height = newValue.doubleValue();
                    draw();
                }
        );
        setOnMousePressed(this::onMousePressed);
        setOnMouseReleased(this::onMouseReleased);
        setOnMouseDragged(this::onMouseDragged);
    }

    private void onMousePressed(MouseEvent mouseEvent) {
        for (PieceDraw pieceDraw : pieceDrawList) {
            if ((mouseEvent.getX() > pieceDraw.getX() && mouseEvent.getX() < pieceDraw.getX() + squareDimensions) &&
                    (mouseEvent.getY() > pieceDraw.getY() && mouseEvent.getY() < pieceDraw.getY() + squareDimensions)){
                selectedPieceDraw = pieceDraw;
            }
        }
        redraw();
    }
    private void onMouseReleased(MouseEvent mouseEvent) {
        if(selectedPieceDraw == null){
            return;
        }
        int initialX = Utility.xCoordinateToFile(selectedPieceDraw.getInitialX(), squareDimensions);
        int initialY = Utility.yCoordinateToRank(selectedPieceDraw.getInitialY(), squareDimensions);
        int x = Utility.xCoordinateToFile(mouseEvent.getX(), squareDimensions);
        int y = Utility.yCoordinateToRank(mouseEvent.getY(), squareDimensions);
        Move move;
        if (y == 7 && selectedPieceDraw.getPiece() == Piece.WHITE_PAWN){
            move = new Move(Utility.rankFileToSquare(initialY, initialX), Utility.rankFileToSquare(y, x), Piece.WHITE_QUEEN);
        } else if (y == 0 && selectedPieceDraw.getPiece() == Piece.BLACK_PAWN){
            move = new Move(Utility.rankFileToSquare(initialY, initialX), Utility.rankFileToSquare(y, x), Piece.BLACK_QUEEN);
        } else {
            move = new Move(Utility.rankFileToSquare(initialY, initialX), Utility.rankFileToSquare(y, x));
        }
        if(boardClickListener != null){
            boardClickListener.onClick(move);
        }
        selectedPieceDraw.returnToInitialPosition();
        selectedPieceDraw = null;
        redraw();

    }
    private void onMouseDragged(MouseEvent mouseEvent) {
        if(selectedPieceDraw == null){
            return;
        }
        selectedPieceDraw.setX((int) (mouseEvent.getX() - squareDimensions / 2));
        selectedPieceDraw.setY((int) (mouseEvent.getY() - squareDimensions / 2));
        redraw();
    }


    public void draw(){
        dimensions = Math.min(width, height);
        squareDimensions = dimensions / 8;
        canvas.resize(dimensions, dimensions);

        createPiecesDraws();
        redraw();
    }
    public void redraw(){
        drawSquares();
        drawPieces();
    }

    private void drawSquares(){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(LIGHT_SQUARE);
        gc.fillRect(0, 0, dimensions, dimensions);
        gc.setFill(DARK_SQUARE);
        for (double x = 0; x < dimensions; x += squareDimensions * 2){
            for (double y = squareDimensions; y < dimensions; y += squareDimensions * 2){
                gc.fillRect(x, y, squareDimensions, squareDimensions);
            }
        }
        for (double x = squareDimensions; x < dimensions; x += squareDimensions * 2){
            for (double y = 0; y < dimensions; y += squareDimensions * 2){
                gc.fillRect(x, y, squareDimensions, squareDimensions);
            }
        }
        if (board != null && selectedPieceDraw != null){
            Square square = selectedPieceDraw.getSquare();
            List<Move> moves = board.legalMoves();
            for(Move move : moves){
                if(move.getFrom().equals(square)){
                    double x = (move.getTo().getFile().value().charAt(5) - 65) * squareDimensions;
                    double y = dimensions - (move.getTo().getRank().value().charAt(5) - 48) * squareDimensions;
                    gc.setFill(MOVE_SQUARE);
                    gc.fillRect(x, y, squareDimensions, squareDimensions);
                }
            }
        }
        if(board != null && board.isKingAttacked()){
            Square square = board.getKingSquare(board.getSideToMove());
            double x = Utility.fileToXCoordinate(square, squareDimensions);
            double y = Utility.rankToYCoordinate(square, dimensions, squareDimensions);
            gc.setFill(CHECK_SQUARE);
            gc.fillRect(x, y, squareDimensions, squareDimensions);
        }
    }

    private void createPiecesDraws(){
        if(board == null){
            return;
        }
        pieceDrawList.clear();
        for (Square square : Square.values()) {
            Piece piece = board.getPiece(square);
            if (piece != Piece.NONE){
                createPieceDraw(square, piece);
            }
        }
    }
    private void createPieceDraw(Square square, Piece piece){
        int x = switch (piece){
            case WHITE_KING, BLACK_KING -> 0;
            case WHITE_QUEEN, BLACK_QUEEN -> 133;
            case WHITE_BISHOP, BLACK_BISHOP -> 266;
            case WHITE_KNIGHT, BLACK_KNIGHT -> 399;
            case WHITE_ROOK, BLACK_ROOK -> 532;
            case WHITE_PAWN, BLACK_PAWN -> 665;
            case NONE -> 1;
        };
        int y = switch(piece){
            case WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK, WHITE_PAWN -> 0;
            case BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, BLACK_PAWN  -> 133;
            case NONE -> 1;
        };
        PixelReader reader = PIECE_SET.getPixelReader();
        WritableImage pieceImage = new WritableImage(reader, x, y, 133, 133);
        PieceDraw pieceDraw = new PieceDraw(Utility.fileToXCoordinate(square, squareDimensions),
                Utility.rankToYCoordinate(square, dimensions, squareDimensions), piece, pieceImage, square);
        pieceDrawList.add(pieceDraw);

    }

    private void drawPieces(){
        GraphicsContext gc = canvas.getGraphicsContext2D();

        for (PieceDraw pieceDraw : pieceDrawList) {
            if (pieceDraw == selectedPieceDraw){
                continue;
            }
            gc.drawImage(pieceDraw.getImage(), pieceDraw.getX(), pieceDraw.getY(), squareDimensions, squareDimensions);
        }
        if (selectedPieceDraw != null){
            gc.drawImage(selectedPieceDraw.getImage(), selectedPieceDraw.getX(), selectedPieceDraw.getY(), squareDimensions, squareDimensions);
        }
    }

    public void setBoardClickListener(BoardClickListener boardClickListener) {
        this.boardClickListener = boardClickListener;
    }

    public void setBoard(Board board) {
        this.board = board;
        draw();
    }
}