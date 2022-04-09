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
import org.koprivnjak.zavrsni.states.OnlineState;
import org.koprivnjak.zavrsni.ui.BoardUI;
import org.koprivnjak.zavrsni.ui.Closable;
import org.koprivnjak.zavrsni.states.PlayerVsComputerState;
import org.koprivnjak.zavrsni.states.PlayerVsPlayerState;


public class MainWindow extends Application {


    public static MainWindow mainWindow;

    private Closable currentState;

    private BorderPane root;
    private FlowPane leftPane;
    private Button playComputerButton;
    private Button playLocalButton;
    private Button playOnlineServerButton;
    private Button playOnlineClientButton;

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
        playOnlineServerButton = new Button("Online server");
        playOnlineServerButton.setOnMouseClicked(this::startOnlineGameServer);
        playOnlineClientButton = new Button("Online client");
        playOnlineClientButton.setOnMouseClicked(this::startOnlineGameClient);
        leftPane.getChildren().add(playComputerButton);
        leftPane.getChildren().add(playLocalButton);
        leftPane.getChildren().add(playOnlineServerButton);
        leftPane.getChildren().add(playOnlineClientButton);

        root.setCenter(new BoardUI());

        root.setLeft(leftPane);
        //Making a window
        Scene scene = new Scene(root, 800, 800, true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess");
        primaryStage.setOnCloseRequest(event -> closeCurrentState());
        primaryStage.show();
    }

    private void startComputerGame(MouseEvent mouseEvent) {
        closeCurrentState();
        currentState = new PlayerVsComputerState(Side.WHITE);
        root.setCenter((BorderPane) currentState);
    }

    public void startLocalGame(Event event){
        closeCurrentState();
        currentState = new PlayerVsPlayerState();
        root.setCenter((BorderPane) currentState);
    }

    private void startOnlineGameServer(MouseEvent mouseEvent) {
        closeCurrentState();
        currentState = new OnlineState(6666);
        root.setCenter((BorderPane) currentState);
    }
    private void startOnlineGameClient(MouseEvent mouseEvent){
        closeCurrentState();
        currentState = new OnlineState(6666, "localhost");
        root.setCenter((BorderPane) currentState);
    }

    private void closeCurrentState(){
        if(currentState != null){
            currentState.close();
        }
    }
}
