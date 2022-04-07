package org.koprivnjak.zavrsni.ui;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import javafx.scene.image.WritableImage;

public class PieceDraw {

    private double initialX;
    private double initialY;
    private double x;
    private double y;
    private Piece piece;
    private WritableImage image;
    private Square square;

    public PieceDraw(double x, double y, Piece piece, WritableImage image, Square square) {
        this.x = x;
        this.y = y;
        this.initialX = x;
        this.initialY = y;
        this.piece = piece;
        this.image = image;
        this.square = square;
    }

    public void returnToInitialPosition(){
        x = initialX;
        y = initialY;
    }

    public double getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getInitialX() {
        return initialX;
    }

    public double getInitialY() {
        return initialY;
    }

    public Piece getPiece() {
        return piece;
    }
    public WritableImage getImage(){
        return image;
    }

    public Square getSquare() {
        return square;
    }
}
