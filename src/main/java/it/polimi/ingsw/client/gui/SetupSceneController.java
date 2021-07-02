package it.polimi.ingsw.client.gui;


import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.utilities.Utils;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toServer.lobby.GameModeResponse;
import it.polimi.ingsw.messages.toServer.lobby.NicknameResponse;
import it.polimi.ingsw.messages.toServer.lobby.NumberOfPlayersResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.UnaryOperator;

public class SetupSceneController {
    List<Integer> selectedLeaderCard;
    List<Resource> selectedResources;
    boolean selectionStarted;

    private GUI gui=null;
    private Client client=null;

    private HashMap<Control, Boolean> validationMap = new HashMap<>();
    @FXML
    private VBox vBoxIPandPORT;
    @FXML
    private VBox vBoxGameMode;
    @FXML
    private VBox vBoxNickname;
    @FXML
    private VBox vBoxNumOfPlayers;
    @FXML
    private VBox vBoxWaiting;


    @FXML
    private Label nicknameInfoLabel;
    @FXML
    private Label lastLabel;

    @FXML
    private ChoiceBox numOfPlayersChoiceBox;


    @FXML
    private Button singlePlayerButton;
    @FXML
    private Button multiPlayerButton;
    @FXML
    private Button sendNicknameButton;
    @FXML
    private Button connectButton;
    @FXML
    private Button sendNumOfPlayerButton;
    @FXML
    private TextField ipTextField;

    @FXML
    private TextField portTextField;
    @FXML
    private TextField nicknameField;


    /**
     * Initializes scene's viewed Vbox and all controller's inner variables
     */
    @FXML
    public void initialize() {
        TextFormatter<Integer> textFormatter = new TextFormatter<>(integerFilter);
        portTextField.setTextFormatter(textFormatter);
        vBoxIPandPORT.setVisible(true);
        vBoxGameMode.setVisible(false);
        vBoxNickname.setVisible(false);
        vBoxNumOfPlayers.setVisible(false);
        vBoxWaiting.setVisible(false);
        selectedResources=new ArrayList<>();
        selectedLeaderCard=new ArrayList<>();
        selectionStarted=false;
    }
    UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.equals("") || newText.matches("([1-9][0-9]{0,4})")) {
            return change;
        }
        return null;
    };

    public void setGUI(GUI gui){
        this.gui=gui;
    }

    public void setClient(Client client){
        this.client=client;
    }

    /**
     * Event handler that instantiates Client
     * @param actionEvent pressed button event
     */
    @FXML
    public void handleConnectButton(ActionEvent actionEvent) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                client = new Client(ipTextField.getText(), Integer.parseInt(portTextField.getText()), gui);
                gui.setClient(client);
                try {
                    client.start();

                } catch (IOException e) {
                    ((Label) vBoxIPandPORT.getChildren().get(0)).setText("Incorrect parameters. Retry");
                }
            }
        });

    }

    /**
     * Event handler that sends nickname to Server
     * @param actionEvent pressed button event
     */
    @FXML
    public void handleSendNicknameButton(ActionEvent actionEvent) {
        client.setNickname(nicknameField.getText());
        client.sendMessageToServer(new NicknameResponse(nicknameField.getText()));
    }

    /**
     * Event handler that checks for ip to be correct
     * @param keyEvent changed textField event
     */
    @FXML
    public void ipChanged(KeyEvent keyEvent) {
        boolean hasInsertedValidIp = Utils.IPAddressIsValid(ipTextField.getText());
        validationMap.put(ipTextField, hasInsertedValidIp);
        validateConnectFields();
    }

    /**
     * Event handler that checks for port to be correct
     * @param keyEvent changed textField event
     */
    @FXML
    public void portChanged(KeyEvent keyEvent) {
        boolean hasInsertedValidPort=false;
        if(!portTextField.getText().equals("")) {
            hasInsertedValidPort = Utils.portIsValid(Integer.parseInt(portTextField.getText()));
        }

        validationMap.put(portTextField, hasInsertedValidPort);
        validateConnectFields();
    }

    /**
     * Checks if both port and ip are correct and enables Connect button if check is successful
     */
    private void validateConnectFields() {
        if (validationMap.values().stream().filter(valid -> valid.equals(Boolean.FALSE)).findFirst().orElse(true)) {
            connectButton.setDisable(false);
        } else {
            connectButton.setDisable(true);
        }
    }

    /**
     * Displays Game Mode selection's Vbox
     */
    public void selectGameMode(){
        Platform.runLater( new Runnable() {
            @Override
            public void run() {
                vBoxIPandPORT.setVisible(false);
                vBoxGameMode.setVisible(true);
                vBoxNickname.setVisible(false);
                vBoxNumOfPlayers.setVisible(false);
                vBoxWaiting.setVisible(false);
            }
        });

    }

    /**
     * Event handler that sends to Server selected game mode (single player)
     * @param actionEvent pressed button event
     */
    @FXML
    public void handleSingleplayerButton(ActionEvent actionEvent) {
        client.setGameMode(GameMode.SINGLE_PLAYER);
        client.sendMessageToServer(new GameModeResponse(GameMode.SINGLE_PLAYER));
    }

    /**
     * Event handler that sends to Server selected game mode (multi player)
     * @param actionEvent pressed button event
     */
    @FXML
    public void handleMultiplayerButton(ActionEvent actionEvent) {
        client.setGameMode(GameMode.MULTI_PLAYER);
        client.sendMessageToServer(new GameModeResponse(GameMode.MULTI_PLAYER));
    }

    /**
     * Displays Nickname request's Vbox
     * @param isRetry if user already put an incorrect nickname
     * @param alreadyTaken if user already put an already used nickname
     */
    public void displayNicknameRequest(Boolean isRetry, Boolean alreadyTaken){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBoxIPandPORT.setVisible(false);
                vBoxGameMode.setVisible(false);
                vBoxNickname.setVisible(true);
                vBoxNumOfPlayers.setVisible(false);
                vBoxWaiting.setVisible(false);
                if (!isRetry)
                    nicknameInfoLabel.setText("Insert your nickname");
                else if (isRetry && !alreadyTaken)
                    nicknameInfoLabel.setText("Your nickname was invalid, be sure to insert only valid characters (A-Z, a-z, 0-9)");
                else {
                    nicknameInfoLabel.setText("Your nickname has already been taken, insert another one");
                }
            }
        });
    }

    /**
     * Displays Number of players request's Vbox
     */
    public void displayNumberOfPlayersRequest(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBoxIPandPORT.setVisible(false);
                vBoxGameMode.setVisible(false);
                vBoxNickname.setVisible(false);
                vBoxNumOfPlayers.setVisible(true);
                vBoxWaiting.setVisible(false);
                numOfPlayersChoiceBox.getItems().add("2");
                numOfPlayersChoiceBox.getItems().add("3");
                numOfPlayersChoiceBox.getItems().add("4");
            }
        });
    }

    /**
     * Sends to server chosen number of players
     * @param actionEvent button pressed event
     */
    @FXML
    public void onNumOfPlayersChoiceBoxChosenButton(ActionEvent actionEvent) {
        String value =(String) numOfPlayersChoiceBox.getValue();
        client.sendMessageToServer(new NumberOfPlayersResponse(Integer.parseInt(value)));
    }

    /**
     * Displays Waiting in the lobby's Vbox
     */
    public void displayWaitingInTheLobbyMessage(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                    vBoxIPandPORT.setVisible(false);
                    vBoxGameMode.setVisible(false);
                    vBoxNickname.setVisible(false);
                    vBoxNumOfPlayers.setVisible(false);
                    vBoxWaiting.setVisible(true);
                    lastLabel.setText("Waiting in the lobby..");
            }
        });
    }

    /**
     * Displays Waiting in the lobby's Vbox, changing main message to ready players' message
     */
    public void displayPlayersReadyToStartMessage(List<String> players) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBoxIPandPORT.setVisible(false);
                vBoxGameMode.setVisible(false);
                vBoxNickname.setVisible(false);
                vBoxNumOfPlayers.setVisible(false);

                String playerNames;
                String delim = "\n";
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while (i < players.size() - 1)
                {
                    sb.append(players.get(i));
                    sb.append(delim);
                    i++;
                }
                sb.append(players.get(i));

                playerNames= sb.toString();
                lastLabel.setText("All the players are ready to start, players in game are:\n" + playerNames + "\n" );
                vBoxWaiting.setVisible(true);
                }

        });
    }

    /**
     * Displays a message warning the user that server is disconnected
     * @param wasConnected true if player was connected to server
     */
    public void handleCloseConnection(boolean wasConnected) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vBoxIPandPORT.setVisible(false);
                vBoxGameMode.setVisible(false);
                vBoxWaiting.setVisible(false);
                vBoxNumOfPlayers.setVisible(false);
                vBoxNickname.setVisible(true);
                vBoxNickname.getChildren().get(1).setVisible(false);
                vBoxNickname.getChildren().get(1).setManaged(false);
                if (!wasConnected)
                    ((Label)vBoxNickname.getChildren().get(0)).setText("The server is not reachable at the moment. Try again later.");
                else
                    ((Label)vBoxNickname.getChildren().get(0)).setText("Connection closed");
                ((Button)vBoxNickname.getChildren().get(2)).setText("Quit");
                ((Button)vBoxNickname.getChildren().get(2)).setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        Platform.exit();
                        System.exit(0);
                    }
                });
            }
        });
    }
}
