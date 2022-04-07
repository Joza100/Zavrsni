package org.koprivnjak.zavrsni;

import com.github.bhlangonijr.chesslib.Side;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.koprivnjak.zavrsni.ui.BoardUI;
import org.koprivnjak.zavrsni.ui.Closable;
import org.koprivnjak.zavrsni.states.PlayerVsComputerState;
import org.koprivnjak.zavrsni.states.PlayerVsPlayerState;


public class MainWindow extends Application {

    public static MainWindow mainWindow;

    private Closable currentUI;

    private BorderPane root;
    private FlowPane leftPane;
    private Button playComputerButton;
    private Button playLocalButton;
    private Button playOnlineButton;

    public MainWindow() {
        mainWindow = this;
    }

    @Override
    public void start(Stage primaryStage) {
        createUI(primaryStage);
    }

    private void createUI(Stage primaryStage) {
        //Creating root and adding all elements
        root = new BorderPane();

        leftPane = new FlowPane();
        leftPane.setOrientation(Orientation.VERTICAL);
        playComputerButton = new Button("Computer");
        playComputerButton.setOnMouseClicked(this::startComputerGame);
        playLocalButton = new Button("1v1");
        playLocalButton.setOnMouseClicked(this::startLocalGame);
        playOnlineButton = new Button("Online");
        //playOnlineButton.setOnMouseClicked(this::);
        leftPane.getChildren().add(playComputerButton);
        leftPane.getChildren().add(playLocalButton);

        root.setCenter(new BoardUI());

        root.setLeft(leftPane);
        //Making a window
        Scene scene = new Scene(root, 800, 800, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess");
        primaryStage.show();
    }


    private void startComputerGame(MouseEvent mouseEvent) {
        closeCurrentUI();
        currentUI = new PlayerVsComputerState(Side.WHITE);
        root.setCenter((BorderPane) currentUI);
    }

    public void startLocalGame(Event event){
        closeCurrentUI();
        currentUI = new PlayerVsPlayerState();
        root.setCenter((BorderPane) currentUI);
    }

    private void closeCurrentUI(){
        if(currentUI != null){
            currentUI.close();
        }
    }
}