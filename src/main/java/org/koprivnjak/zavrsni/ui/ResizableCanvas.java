package org.koprivnjak.zavrsni.ui;
import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void resize(double width, double height) {
        this.setWidth(width);
        this.setHeight(height);
    }
}