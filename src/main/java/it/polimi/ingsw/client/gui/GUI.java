package it.polimi.ingsw.client.gui;
import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.common.FunctionInterface;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.ActionType;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.model.cards.Value;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GUI extends Application implements View {

    private Stage stage;
    private Client client;
    private FXMLLoader fxmlLoader;
    private SetupSceneController setupSceneController;


    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);

        });
        askConnectionParameters();
    }

    private void createMainScene(String pathOfFxmlFile, FunctionInterface functionInterface) {
        Platform.runLater(() -> {
            fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(pathOfFxmlFile));
            Scene scene;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
                scene = new Scene(new Label("Error loading the scene"));
            }
            stage.setScene(scene);
            stage.setResizable(false);
            functionInterface.executeFunction();
        });
    }
    private void askConnectionParameters(){
        resetControllers();
        createMainScene("/FXML/SetupScene.fxml", () -> {
            stage.setTitle("Maestri del Rinascimento");
            stage.setResizable(false);
            stage.show();
            setupSceneController = fxmlLoader.getController();
            setupSceneController.setGUI(this);
            // just for testing
            //if (mockingConnection) {
                //setupScreenController.mockSendConnect();
            //}

            //if (isLogged) setupScreenController.displayUserForm();
        });


    }



    private void resetControllers() {
        setupSceneController = null;
    }

    @Override
    public void displayGameModeRequest() {
            setupSceneController.selectGameMode();
    }

    @Override
    public void displayNicknameRequest(boolean isRetry, boolean alreadyTaken) {
        setupSceneController.displayNicknameRequest(isRetry,alreadyTaken);
    }

    @Override
    public void displayNumberOfPlayersRequest() {
        setupSceneController.displayNumberOfPlayersRequest();
    }

    @Override
    public void displayWaitingInTheLobbyMessage() {
        setupSceneController.displayWaitingInTheLobbyMessage();
    }

    @Override
    public void displayPlayersReadyToStartMessage(List<String> p) {
        setupSceneController.displayPlayersReadyToStartMessage(p);
    }

    @Override
    public void displayTimeoutExpiredMessage() {

    }

    @Override
    public void displayMarbleInsertionPositionRequest() {

    }

    @Override
    public void displayChooseWhiteMarbleConversionRequest(List<Resource> resources, int numberOfMarbles) {

    }

    @Override
    public void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion) {

    }
    @Override
    public void displayChooseStorageTypeRequest(Resource resource, List<String> availableDepots, boolean canDiscard, boolean canReorganize) {

    }

    @Override
    public void displayReorganizeDepotsRequest(List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResource){}


    @Override
    public void displayChooseLeaderCardsRequest(List<Integer> leaderCards){
        setupSceneController.displayLeaderCardsRequest(leaderCards, client);
    }

    @Override
    public void displaySelectCardRequest(List<Integer> leaderCards, boolean leaderORdevelopment) {

    }

    @Override
    public void loadLeaderCards(List<LightLeaderCard> leaderCards){
        MatchData.getInstance().setAllLeaderCards(leaderCards);
    }

    @Override

    public void displayChooseResourceTypeRequest(List<Resource> resourceTypes, int quantity) {
    }

    public void loadDevelopmentCards(List<LightDevelopmentCard> lightDevelopmentCards) {

    }

    @Override
    public void displayChooseProductionPowersRequest(Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources) {

    }
    @Override
    public void displayMessage(String message) {
        //TODO
    }

    @Override
    public void displayDepotStatus(List<Resource>[] warehouseDepots, List<Resource>[] strongboxDepots, List<List<Resource>> leaderDepots) {

    }

    @Override
    public void displaySelectStorageRequest(Resource resource, boolean isInWarehouse, boolean isInStrongbox, boolean isInLeaderDepot) {

    }

    @Override
    public void displayChooseActionRequest(Map<ActionType, Boolean> executableActions, boolean standardActionDone) {

    }


    @Override
    public void displaySelectDevelopmentCardSlotRequest(boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable) {

    }

    @Override
    public void updateFaithTrackInfo(String nickname, int steps){

    }

    public void setNicknames(String playerNickname, List<String> otherPlayersNicknames){
        MatchData.getInstance().setThisClient(playerNickname);
        for(String nickname : otherPlayersNicknames){
            MatchData.getInstance().addLightClient(nickname);
        }
    }


}
