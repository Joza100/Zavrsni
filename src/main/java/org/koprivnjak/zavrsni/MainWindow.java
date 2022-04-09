package org.koprivnjak.zavrsni;

import com.github.bhlangonijr.chesslib.Side;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.koprivnjak.zavrsni.states.OnlineState;
import org.koprivnjak.zavrsni.states.PlayerVsComputerState;
import org.koprivnjak.zavrsni.states.PlayerVsPlayerState;
import org.koprivnjak.zavrsni.ui.BoardUI;
import org.koprivnjak.zavrsni.ui.Closable;

import java.util.Random;


public class MainWindow extends Application {
    private static final double LEFT_PANE_WIDTH = 300;

    private Pane currentState;

    private BorderPane root;

    private GridPane leftButtonPane;
    private Button playComputerButton;
    private Button playLocalButton;
    private Button playOnlineServerButton;
    private Button playOnlineClientButton;

    private AnchorPane playComputerSetup;
    private Label playComputerLabel;
    private FlowPane playComputerSettingsPane;
    private Label selectSideLabel;
    private ComboBox<String> selectSideComboBox;
    private Label depthLabel;
    private TextField depthTextField;
    private Button playComputerCancelButton;
    private Button playComputerConfirmButton;
    private Label invalidDepthLabel;
    private Label invalidSideLabel;



    private Random random;

    public MainWindow(){
        random = new Random();
    }

    @Override
    public void start(Stage primaryStage) {
        createUI(primaryStage);
    }

    private void createUI(Stage primaryStage) {
        //Creating root and adding all elements
        root = new BorderPane();

        playComputerSetup = new AnchorPane();
        playComputerSetup.setId("playComputerSetup");

        playComputerLabel = new Label("Play Computer");
        playComputerLabel.setId("playComputerLabel");
        AnchorPane.setTopAnchor(playComputerLabel, 20.0);

        playComputerCancelButton = new Button("Cancel");
        playComputerCancelButton.getStyleClass().add("confirmButtons");
        playComputerCancelButton.setMinSize(100, 40);
        playComputerCancelButton.setOnMouseClicked(this::onPlayComputerCancelButtonClicked);
        playComputerConfirmButton = new Button("Play");
        playComputerConfirmButton.getStyleClass().add("confirmButtons");
        playComputerConfirmButton.setMinSize(100, 40);
        playComputerConfirmButton.setOnMouseClicked(this::onPlayComputerConfirmButtonClicked);
        AnchorPane.setBottomAnchor(playComputerCancelButton, 500.0);
        AnchorPane.setBottomAnchor(playComputerConfirmButton, 500.0);
        AnchorPane.setLeftAnchor(playComputerCancelButton, 5.0);
        AnchorPane.setRightAnchor(playComputerConfirmButton, 80.0);

        playComputerSettingsPane = new FlowPane();
        playComputerSettingsPane.setOrientation(Orientation.VERTICAL);
        playComputerSettingsPane.setVgap(4);
        selectSideLabel = new Label("Select your side: ");
        selectSideLabel.getStyleClass().add("inputLabel");
        selectSideComboBox = new ComboBox<>(FXCollections.observableArrayList("White", "Black", "Random"));
        depthLabel = new Label("Select Stockfish depth: ");
        depthLabel.getStyleClass().add("inputLabel");
        depthTextField = new TextField();
        AnchorPane.setLeftAnchor(playComputerSettingsPane, 5.0);
        AnchorPane.setTopAnchor(playComputerSettingsPane, 80.0);
        invalidDepthLabel = new Label("The inputted depth is invalid.");
        invalidDepthLabel.setVisible(false);
        invalidDepthLabel.getStyleClass().add("errorText");
        invalidSideLabel = new Label("Side is not selected.");
        invalidSideLabel.setVisible(false);
        invalidSideLabel.getStyleClass().add("errorText");
        playComputerSettingsPane.getChildren().addAll(selectSideLabel, selectSideComboBox, depthLabel, depthTextField,
                invalidDepthLabel, invalidSideLabel);


        playComputerSetup.getChildren().addAll(playComputerLabel, playComputerSettingsPane, playComputerConfirmButton,
                playComputerCancelButton);
        playComputerSetup.setMinWidth(LEFT_PANE_WIDTH);



        leftButtonPane = new GridPane();
        playComputerButton = new Button("Computer");
        playComputerButton.setOnMouseClicked(this::onPlayComputerButtonClicked);
        playLocalButton = new Button("1v1");
        playLocalButton.setOnMouseClicked(this::startLocalGame);
        playOnlineServerButton = new Button("Online server");
        playOnlineServerButton.setOnMouseClicked(this::startOnlineGameServer);
        playOnlineClientButton = new Button("Online client");
        playOnlineClientButton.setOnMouseClicked(this::startOnlineGameClient);
        leftButtonPane.setMinWidth(LEFT_PANE_WIDTH);
        leftButtonPane.add(playComputerButton, 0, 0);
        leftButtonPane.add(playLocalButton, 1, 0);
        leftButtonPane.add(playOnlineServerButton, 0, 1);
        leftButtonPane.add(playOnlineClientButton, 1, 1);

        for (int row = 0 ; row < 2 ; row++){
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.ALWAYS);
            leftButtonPane.getRowConstraints().add(rc);
        }

        for (int col = 0 ; col < 2; col++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setFillWidth(true);
            cc.setHgrow(Priority.ALWAYS);
            leftButtonPane.getColumnConstraints().add(cc);
        }

        playComputerButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        playLocalButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        playOnlineServerButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        playOnlineClientButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        root.setLeft(leftButtonPane);
        root.setCenter(new BoardUI());

        //Making a window
        Scene scene = new Scene(root, 800, 800, true);
        scene.getStylesheets().add("/style.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess");
        primaryStage.setOnCloseRequest(event -> closeCurrentState());
        primaryStage.show();
    }

    private void onPlayComputerConfirmButtonClicked(MouseEvent mouseEvent) {
        boolean successful = true;
        Side side = null;
        if (selectSideComboBox.getSelectionModel().getSelectedItem() == null){
            invalidSideLabel.setVisible(true);
            successful = false;
        } else if (selectSideComboBox.getSelectionModel().getSelectedItem().equals("White")){
            side = Side.WHITE;
        } else if (selectSideComboBox.getSelectionModel().getSelectedItem().equals("Black")){
            side = Side.BLACK;
        } else if (selectSideComboBox.getSelectionModel().getSelectedItem().equals("Random")){
            side = random.nextInt(0, 2) == 0 ? Side.WHITE : Side.BLACK;
        }
        String depthText = depthTextField.getText();
        int depth = 1;
        if(depthText.matches("-?\\d+")){
            depth = Integer.parseInt(depthText);
        } else {
            invalidDepthLabel.setVisible(true);
            successful = false;
        }

        if(successful){
            changeState(new PlayerVsComputerState(side, depth));
            invalidSideLabel.setVisible(false);
            invalidDepthLabel.setVisible(false);
            root.setLeft(leftButtonPane);
        }
    }

    private void onPlayComputerCancelButtonClicked(MouseEvent mouseEvent) {
        root.setLeft(leftButtonPane);
    }

    private void onPlayComputerButtonClicked(MouseEvent mouseEvent){
        root.setLeft(playComputerSetup);
    }

    public void startLocalGame(Event event){
        changeState(new PlayerVsPlayerState());
    }

    private void startOnlineGameServer(MouseEvent mouseEvent) {
        changeState(new OnlineState(6666));
    }
    private void startOnlineGameClient(MouseEvent mouseEvent){
        changeState(new OnlineState(6666, "localhost"));
    }

    private void changeState(Pane state){
        closeCurrentState();
        currentState = state;
        /*currentState.widthProperty().addListener((observable, oldValue, newValue) -> {
            double centerWidth = newValue.doubleValue();
            double paneWidth = ;
            ((Pane) root.getCenter()).setMinHeight(scene.getHeight());
            ((Pane) root.getLeft()).setPrefWidth(paneWidth);
        });*/
        root.setCenter(state);
    }

    private void closeCurrentState(){
        if(currentState != null){
            ((Closable)currentState).close();
        }
    }
}
