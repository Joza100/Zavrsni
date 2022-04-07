package org.koprivnjak.zavrsni.util;

import com.github.bhlangonijr.chesslib.Square;

public class Utility {
    public static Square rankFileToSquare(int rank, int file){
        return Square.squareAt(file + rank * 8);
    }
    public static double fileToXCoordinate(Square square, double squareDimensions){
        return (square.getFile().value().charAt(5) - 65) * squareDimensions;
    }
    public static double rankToYCoordinate(Square square, double dimensions, double squareDimensions){
        return dimensions - (square.getRank().value().charAt(5) - 48) * squareDimensions;
    }
    public static int xCoordinateToFile(double x, double squareDimensions){
        return (int) (x / squareDimensions);
    }
    public static int yCoordinateToRank(double y, double squareDimensions) {
        return 7 - (int) (y / squareDimensions);
    }
}
