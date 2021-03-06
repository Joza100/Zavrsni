package org.koprivnjak.zavrsni;

import com.github.bhlangonijr.chesslib.Side;
import javafx.application.Application;
import javafx.application.Platform;
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
import org.koprivnjak.zavrsni.networking.ConnectTask;
import org.koprivnjak.zavrsni.networking.WaitForPlayerTask;
import org.koprivnjak.zavrsni.states.OnlineState;
import org.koprivnjak.zavrsni.states.PlayerVsComputerState;
import org.koprivnjak.zavrsni.states.PlayerVsPlayerState;
import org.koprivnjak.zavrsni.ui.BoardUI;
import org.koprivnjak.zavrsni.states.State;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainWindow extends Application {
    private static final int INTEGER_PARSE_ERROR = -100;
    private static final double LEFT_PANE_MIN_WIDTH = 250;
    private static final double LEFT_PANE_MAX_WIDTH = 300;

    private Pane currentState;

    private Scene scene;
    private BorderPane root;

    private GridPane leftButtonPane;
    private Button playComputerButton;
    private Button playLocalButton;
    private Button createServerButton;
    private Button createClientButton;


    private AnchorPane playComputerSetup;
    private Label playComputerLabel;
    private FlowPane playComputerSettingsPane;
    private Label playComputerSelectSideLabel;
    private ComboBox<String> playComputerSelectSideComboBox;
    private Label playComputerDepthLabel;
    private TextField playComputerDepthTextField;
    private Label playComputerErrorLabel;
    private HBox playComputerConfirmButtons;
    private Button playComputerCancelButton;
    private Button playComputerConfirmButton;


    private AnchorPane createServerSetup;
    private Label createServerLabel;
    private FlowPane createServerSettingsPane;
    private Label createServerSelectSideLabel;
    private ComboBox<String> createServerSelectSideComboBox;
    private Label createServerPortLabel;
    private TextField createServerPortTextField;
    private Label createServerInfoLabel;
    private Label createServerErrorLabel;
    private HBox createServerConfirmButtons;
    private Button createServerCancelButton;
    private Button createServerConfirmButton;

    private AnchorPane createClientSetup;
    private Label createClientLabel;
    private FlowPane createClientSettingsPane;
    private Label createClientIpAddressLabel;
    private TextField createClientIpAddressTextField;
    private Label createClientPortLabel;
    private TextField createClientPortTextField;
    private Label createClientErrorLabel;
    private Label createClientInfoLabel;
    private HBox createClientConfirmButtons;
    private Button createClientCancelButton;
    private Button createClientConfirmButton;

    private ServerSocket serverSocket;
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

        //Create server screen
        createServerSetup = new AnchorPane();
        createServerSetup.getStyleClass().add("setupPane");
        createServerLabel = new Label("Host a game");
        createServerLabel.getStyleClass().add("titleLabel");

        createServerSettingsPane = new FlowPane();
        createServerSettingsPane.setOrientation(Orientation.VERTICAL);
        createServerSettingsPane.setVgap(4);
        createServerSelectSideLabel = new Label("Select your side: ");
        createServerSelectSideLabel.getStyleClass().add("inputLabel");
        createServerSelectSideComboBox = new ComboBox<>(FXCollections.observableArrayList("White", "Black", "Random"));
        createServerPortLabel = new Label("Port: ");
        createServerPortLabel.getStyleClass().add("inputLabel");
        createServerPortTextField = new TextField();
        createServerErrorLabel = new Label();
        createServerErrorLabel.getStyleClass().add("errorLabel");
        createServerInfoLabel = new Label();
        createServerInfoLabel.getStyleClass().add("infoLabel");
        createServerConfirmButton = new Button("Confirm");
        createServerCancelButton = new Button("Cancel");
        createServerConfirmButton.setMinSize(100, 40);
        createServerCancelButton.setMinSize(100, 40);
        createServerConfirmButton.setOnMouseClicked(this::onCreateServerConfirmButtonClicked);
        createServerCancelButton.setOnMouseClicked(this::onCreateServerCancelButtonClicked);
        createServerConfirmButtons = new HBox();
        createServerConfirmButtons.getChildren().addAll(createServerCancelButton, createServerConfirmButton);
        //createServerConfirmButtons.setOrientation(Orientation.HORIZONTAL);

        AnchorPane.setLeftAnchor(createServerSettingsPane, 5.0);
        AnchorPane.setTopAnchor(createServerSettingsPane, 80.0);
        createServerSettingsPane.getChildren().addAll(createServerSelectSideLabel,
                createServerSelectSideComboBox, createServerPortLabel,
                createServerPortTextField, createServerInfoLabel, createServerErrorLabel, createServerConfirmButtons);


        createServerSetup.getChildren().addAll(createServerLabel, createServerSettingsPane);
        createServerSetup.setMinWidth(LEFT_PANE_MIN_WIDTH);
        createServerSetup.setMaxWidth(LEFT_PANE_MAX_WIDTH);


        //Create client screen
        createClientSetup = new AnchorPane();
        createClientSetup.getStyleClass().add("setupPane");
        createClientLabel = new Label("Join a game");
        createClientLabel.getStyleClass().add("titleLabel");
        createClientSettingsPane = new FlowPane();
        createClientSettingsPane.setOrientation(Orientation.VERTICAL);
        createClientSettingsPane.setVgap(4);
        createClientPortLabel = new Label("Port: ");
        createClientPortLabel.getStyleClass().add("inputLabel");
        createClientPortTextField = new TextField();
        createClientIpAddressLabel = new Label("Ip address: ");
        createClientIpAddressLabel.getStyleClass().add("inputLabel");
        createClientIpAddressTextField = new TextField();
        createClientErrorLabel = new Label();
        createClientErrorLabel.getStyleClass().add("errorLabel");
        createClientInfoLabel = new Label();
        createClientInfoLabel.getStyleClass().add("infoLabel");
        createClientConfirmButton = new Button("Confirm");
        createClientCancelButton = new Button("Cancel");
        createClientConfirmButton.setMinSize(100, 40);
        createClientCancelButton.setMinSize(100, 40);
        createClientConfirmButton.setOnMouseClicked(this::onCreateClientConfirmButtonClicked);
        createClientCancelButton.setOnMouseClicked(this::onCreateClientCancelButtonClicked);
        createClientConfirmButtons = new HBox();
        createClientConfirmButtons.getChildren().addAll(createClientCancelButton, createClientConfirmButton);
        //createClientConfirmButtons.setOrientation(Orientation.HORIZONTAL);

        AnchorPane.setLeftAnchor(createClientSettingsPane, 5.0);
        AnchorPane.setTopAnchor(createClientSettingsPane, 80.0);
        createClientSettingsPane.getChildren().addAll(createClientIpAddressLabel, createClientIpAddressTextField,
                createClientPortLabel, createClientPortTextField, createClientErrorLabel, createClientInfoLabel,
                createClientConfirmButtons);


        createClientSetup.getChildren().addAll(createClientLabel, createClientSettingsPane);
        createClientSetup.setMinWidth(LEFT_PANE_MIN_WIDTH);
        createClientSetup.setMaxWidth(LEFT_PANE_MAX_WIDTH);


        //Play against computer screen
        playComputerSetup = new AnchorPane();
        playComputerSetup.getStyleClass().add("setupPane");

        playComputerLabel = new Label("Play Computer");
        playComputerLabel.getStyleClass().add("titleLabel");
        AnchorPane.setTopAnchor(playComputerLabel, 20.0);

        playComputerCancelButton = new Button("Cancel");
        playComputerCancelButton.getStyleClass().add("confirmButtons");
        playComputerCancelButton.setMinSize(100, 40);
        playComputerCancelButton.setOnMouseClicked(this::onPlayComputerCancelButtonClicked);
        playComputerConfirmButton = new Button("Play");
        playComputerConfirmButton.getStyleClass().add("confirmButtons");
        playComputerConfirmButton.setMinSize(100, 40);
        playComputerConfirmButton.setOnMouseClicked(this::onPlayComputerConfirmButtonClicked);
        playComputerConfirmButtons = new HBox();
        playComputerConfirmButtons.getChildren().addAll(playComputerCancelButton, playComputerConfirmButton);
        //playComputerConfirmButtons.setOrientation(Orientation.HORIZONTAL);

        playComputerSettingsPane = new FlowPane();
        playComputerSettingsPane.setOrientation(Orientation.VERTICAL);
        playComputerSettingsPane.setVgap(4);
        playComputerSelectSideLabel = new Label("Select your side: ");
        playComputerSelectSideLabel.getStyleClass().add("inputLabel");
        playComputerSelectSideComboBox = new ComboBox<>(FXCollections.observableArrayList("White", "Black", "Random"));
        playComputerDepthLabel = new Label("Select Stockfish depth: ");
        playComputerDepthLabel.getStyleClass().add("inputLabel");
        playComputerDepthTextField = new TextField();
        AnchorPane.setLeftAnchor(playComputerSettingsPane, 5.0);
        AnchorPane.setTopAnchor(playComputerSettingsPane, 80.0);
        playComputerErrorLabel = new Label();
        playComputerErrorLabel.getStyleClass().add("errorLabel");
        playComputerSettingsPane.getChildren().addAll(playComputerSelectSideLabel, playComputerSelectSideComboBox,
                playComputerDepthLabel, playComputerDepthTextField, playComputerErrorLabel, playComputerConfirmButtons);


        playComputerSetup.getChildren().addAll(playComputerLabel, playComputerSettingsPane);
        playComputerSetup.setMinWidth(LEFT_PANE_MIN_WIDTH);
        playComputerSetup.setMaxWidth(LEFT_PANE_MAX_WIDTH);

        //Buttons Pane
        leftButtonPane = new GridPane();
        leftButtonPane.getStyleClass().add("setupPane");
        playComputerButton = new Button("Play against\nthe computer");
        playComputerButton.setOnMouseClicked(this::onPlayComputerButtonClicked);
        playLocalButton = new Button("1v1");
        playLocalButton.setOnMouseClicked(this::onPlayLocalButtonClicked);
        createServerButton = new Button("Host a game");
        createServerButton.setOnMouseClicked(this::onCreateServerButtonClicked);
        createClientButton = new Button("Join a game");
        createClientButton.setOnMouseClicked(this::onCreateClientButtonClicked);
        leftButtonPane.setMinWidth(LEFT_PANE_MIN_WIDTH);
        leftButtonPane.setMaxWidth(LEFT_PANE_MAX_WIDTH);
        leftButtonPane.add(playComputerButton, 0, 0);
        leftButtonPane.add(playLocalButton, 1, 0);
        leftButtonPane.add(createServerButton, 0, 1);
        leftButtonPane.add(createClientButton, 1, 1);

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
        createServerButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        createClientButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);



        root.setLeft(leftButtonPane);
        root.setCenter(new BoardUI());

        //Making a window
        scene = new Scene(root, 800, 800, true);
        scene.getStylesheets().add("/style.css");
        scene.widthProperty().addListener((observable) -> adjustSize());
        scene.heightProperty().addListener((observable) -> adjustSize());
        adjustSize();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chess");
        primaryStage.setOnCloseRequest(event -> closeCurrentState());
        primaryStage.show();
    }

    private void adjustSize(){
        //root.getStyleClass().add("setupPane");
        ((Pane) root.getCenter()).setPrefWidth(scene.getHeight());
        ((Pane) root.getCenter()).setPrefHeight(scene.getHeight());
        ((Pane) root.getLeft()).setPrefWidth(scene.getWidth() - scene.getHeight());
    }

    private void setLeftPane(Pane leftPane){
        playComputerErrorLabel.setText("");
        createServerInfoLabel.setText("");
        createServerErrorLabel.setText("");
        createClientErrorLabel.setText("");
        createClientInfoLabel.setText("");
        root.setLeft(leftPane);
    }

    private void onCreateClientCancelButtonClicked(MouseEvent mouseEvent) {
        setLeftPane(leftButtonPane);
    }

    private void onCreateClientConfirmButtonClicked(MouseEvent mouseEvent) {
        boolean successful = true;
        String errorText = "";

        int port = parseInteger(createClientPortTextField.getText());
        String ipAddress = createClientIpAddressTextField.getText();
        if (port < 1024 || port > 65535){
            errorText += "Invalid port selected\n";
            successful = false;
        }
        if(!validIP(ipAddress)){
            successful = false;
            errorText += "Invalid IP address";
        }

        if(successful){
            ConnectTask connectTask = new ConnectTask(ipAddress, port);
            connectTask.setOnSucceeded(event -> changeState(new OnlineState(connectTask.getValue())));
            connectTask.setOnFailed(event -> Platform.runLater(() -> {
                createClientErrorLabel.setText("Failed to connect!");
                createClientInfoLabel.setText("");
            }));
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(connectTask);
            executorService.shutdown();
            createClientInfoLabel.setText("Attempting to connect...");
        }
        createClientErrorLabel.setText(errorText);
    }

    private void onCreateServerCancelButtonClicked(MouseEvent mouseEvent) {
        setLeftPane(leftButtonPane);
        if(serverSocket != null){
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCreateServerConfirmButtonClicked(MouseEvent mouseEvent) {
        boolean successful = true;
        String errorText = "";

        int port = parseInteger(createServerPortTextField.getText());
        Side side = null;
        if (port < 1024 || port > 65535){
            errorText += "Invalid port selected\n";
            successful = false;
        }
        if (createServerSelectSideComboBox.getSelectionModel().getSelectedItem() == null){
            errorText += "Side not selected";
            successful = false;
        } else if (createServerSelectSideComboBox.getSelectionModel().getSelectedItem().equals("White")){
            side = Side.WHITE;
        } else if (createServerSelectSideComboBox.getSelectionModel().getSelectedItem().equals("Black")){
            side = Side.BLACK;
        } else if (createServerSelectSideComboBox.getSelectionModel().getSelectedItem().equals("Random")){
            side = random.nextInt(0, 2) == 0 ? Side.WHITE : Side.BLACK;
        }

        if(successful){
            createServerErrorLabel.setText("");
            try {
                serverSocket = new ServerSocket(port);
                WaitForPlayerTask waitForPlayerTask = new WaitForPlayerTask(serverSocket);
                Side finalSide = side;
                waitForPlayerTask.setOnSucceeded(event ->
                    changeState(new OnlineState(serverSocket, waitForPlayerTask.getValue(), finalSide)));
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                executorService.execute(waitForPlayerTask);
                executorService.shutdown();
                createServerInfoLabel.setText("Waiting for a player!");
            } catch (BindException e){
                errorText += "The selected port is already in use!";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        createServerErrorLabel.setText(errorText);
    }

    private void onPlayComputerConfirmButtonClicked(MouseEvent mouseEvent) {
        boolean successful = true;
        String errorText = "";
        Side side = null;
        if (playComputerSelectSideComboBox.getSelectionModel().getSelectedItem() == null){
            errorText += "Side not selected\n";
            successful = false;
        } else if (playComputerSelectSideComboBox.getSelectionModel().getSelectedItem().equals("White")){
            side = Side.WHITE;
        } else if (playComputerSelectSideComboBox.getSelectionModel().getSelectedItem().equals("Black")){
            side = Side.BLACK;
        } else if (playComputerSelectSideComboBox.getSelectionModel().getSelectedItem().equals("Random")){
            side = random.nextInt(0, 2) == 0 ? Side.WHITE : Side.BLACK;
        }
        String depthText = playComputerDepthTextField.getText();
        int depth = parseInteger(depthText);
        if(depth < 1) {
            errorText += "The inputted depth is invalid";
            successful = false;
        }

        if(successful){
            changeState(new PlayerVsComputerState(side, depth));
            playComputerErrorLabel.setText("");
        } else {
            playComputerErrorLabel.setText(errorText);
        }
    }

    private void onPlayComputerCancelButtonClicked(MouseEvent mouseEvent) {
        setLeftPane(leftButtonPane);
    }

    private void onPlayComputerButtonClicked(MouseEvent mouseEvent){
        setLeftPane(playComputerSetup);
    }

    public void onPlayLocalButtonClicked(Event event){
        changeState(new PlayerVsPlayerState());
    }

    private void onCreateServerButtonClicked(MouseEvent mouseEvent) {
        setLeftPane(createServerSetup);
    }
    private void onCreateClientButtonClicked(MouseEvent mouseEvent){
        setLeftPane(createClientSetup);
    }

    private void changeState(Pane state){
        closeCurrentState();
        currentState = state;
        ((State)currentState).start();
        /*currentState.widthProperty().addListener((observable, oldValue, newValue) -> {
            double centerWidth = newValue.doubleValue();
            double paneWidth = ;
            ((Pane) root.getCenter()).setMinHeight(scene.getHeight());
            ((Pane) root.getLeft()).setPrefWidth(paneWidth);
        });*/
        setLeftPane(leftButtonPane);
        root.setCenter(state);
        adjustSize();
    }

    private void closeCurrentState(){
        if(currentState != null){
            ((State)currentState).close();
        }
    }

    private static int parseInteger(String string){
        int integer;
        if(string.matches("-?\\d+")){
            integer = Integer.parseInt(string);
        } else {
            integer = INTEGER_PARSE_ERROR;
        }
        return integer;
    }

    private static boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }
            if(ip.equals("localhost")){
                return true;
            }
            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            return !ip.endsWith(".");
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
