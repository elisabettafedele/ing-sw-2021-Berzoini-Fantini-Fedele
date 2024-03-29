package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.client.utilities.UtilityProduction;
import it.polimi.ingsw.common.FunctionInterface;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.ActionType;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.messages.toClient.matchData.TurnMessage;
import it.polimi.ingsw.messages.toClient.matchData.MatchDataMessage;
import it.polimi.ingsw.messages.toServer.lobby.GameModeResponse;
import it.polimi.ingsw.messages.toServer.lobby.NicknameResponse;
import it.polimi.ingsw.model.cards.Value;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to manage the graphical user interface
 */
public class GUI extends Application implements View {

    private Stage stage;
    private Client client;
    private FXMLLoader fxmlLoader;
    private SetupSceneController setupSceneController;
    private GameSceneController gameSceneController;
    private boolean isUpdateActive;


    @Override
    public void start(Stage stage) throws Exception {
        isUpdateActive=true;
        this.stage = stage;
        stage.getIcons().add(new Image(GUI.class.getResource("/img/icon.png").toString()));
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);

        });
        MatchData.getInstance().setView(this);
        askConnectionParameters();
    }

    /**
     * It creates the scene in FXML file. It doesnt show it unless specified inside functionInterface's code.
     * @param pathOfFxmlFile FXML file path of the scene to be created
     * @param functionInterface the function to be called at the end of scene creation and assignation to stage.
     */
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

    /**
     * It creates and shows the Setup Scene as well as instantiating it's Setup Scene Controller
     */
    public void instantiateGameScene(){
        createMainScene("/FXML/GameScene.fxml", () -> {
            stage.setTitle("Masters of Renaissance");
            stage.setResizable(false);
            stage.show();
            gameSceneController = fxmlLoader.getController();
            gameSceneController.setGUI(this);
            gameSceneController.setClient(client);
        });
    }

    /**
     * It creates and shows the Game Scene as well as instantiating it's Game Scene Controller
     */
    private void instantiateSetupScene(){
        createMainScene("/FXML/SetupScene.fxml", () -> {
            stage.setTitle("Masters of Renaissance");
            stage.setResizable(false);
            stage.show();
            setupSceneController = fxmlLoader.getController();
            setupSceneController.setGUI(this);
        });
    }

    /**
     * Sets the client to use to send responses and answers to the server.
     * @param client client instance
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Resets all controller and prepares the Setup Scene instantiation, containing ip and port parameters' choice selection
     */
    private void askConnectionParameters(){
        resetControllers();
        instantiateSetupScene();
    }

    /**
     * resets scene controllers instances
     */
    private void resetControllers() {
        setupSceneController = null;
        gameSceneController=null;
    }

    @Override
    public void displayMessage(String message) {
        //empty
    }

    // *********************************************************************  //
    //                           END GAME  & FA                               //
    // *********************************************************************  //


    @Override
    public void displayResults(Map<String, Integer> results, boolean readyForAnotherGame) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameSceneController.displayResults(results,readyForAnotherGame);
            }
        });

    }

    @Override
    public void displayResults(int victoryPoints) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameSceneController.displayResults(victoryPoints);
            }
        });
    }

    @Override
    public void handleCloseConnection(boolean wasConnected) {
        if(setupSceneController != null) setupSceneController.handleCloseConnection(wasConnected);
        if(gameSceneController != null) gameSceneController.handleCloseConnection(wasConnected);
    }

    /**
     * Method to handle the expire of the timeout.
     * Asks to the client whether he wants to be reconnected.
     */
    @Override
    public void displayTimeoutExpiredMessage() {
        if(setupSceneController!=null){
            System.out.println("Timeout expired");
            client.closeSocket();
            Platform.exit();
            System.exit(0);
        }
        else{
            gameSceneController.handleTimeoutExpired();
        }
    }

    @Override
    public void displayDisconnection(String nickname, boolean setUp, boolean gameCancelled) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(gameSceneController!=null) {
                    gameSceneController.displayDisconnection(nickname, setUp, gameCancelled);
                }
            }
        });
    }

    @Override
    public void displayWelcomeBackMessage(String nickname, boolean gameFinished) {
        resetControllers();
        instantiateGameScene();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameSceneController.displayWelcomeBackMessage(nickname,gameFinished);
            }
        });

    }

    // *********************************************************************  //
    //                              LOBBY GUI                                 //
    // *********************************************************************  //


    @Override
    public void displayGameModeRequest() {
        if (client.getGameMode().isPresent()){
            client.sendMessageToServer(new GameModeResponse(client.getGameMode().get()));
            return;
        }
        if(setupSceneController!=null)setupSceneController.selectGameMode();
        else{
            resetControllers();
            instantiateSetupScene();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    setupSceneController.setClient(client);
                    setupSceneController.selectGameMode();
                }
            });
        }
    }

    @Override
    public void displayNicknameRequest(boolean isRetry, boolean alreadyTaken) {
        if (client.isNicknameValid() && client.getNickname().isPresent()){
            client.sendMessageToServer(new NicknameResponse(client.getNickname().get()));
            return;
        }
        if(setupSceneController!=null) setupSceneController.displayNicknameRequest(isRetry,alreadyTaken);
        else{
            resetControllers();
            instantiateSetupScene();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    setupSceneController.setClient(client);
                    setupSceneController.displayNicknameRequest(isRetry,alreadyTaken);
                }
            });
        }
    }

    @Override
    public void displayNumberOfPlayersRequest() {
        if(setupSceneController!=null)setupSceneController.displayNumberOfPlayersRequest();
        else{
            resetControllers();
            instantiateSetupScene();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    setupSceneController.setClient(client);
                    setupSceneController.displayNumberOfPlayersRequest();
                }
            });
        }
    }

    @Override
    public void displayWaitingInTheLobbyMessage() {
        if(setupSceneController!=null)setupSceneController.displayWaitingInTheLobbyMessage();
        else{
            resetControllers();
            instantiateSetupScene();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    setupSceneController.setClient(client);
                    setupSceneController.displayWaitingInTheLobbyMessage();
                }
            });
        }
    }

    @Override
    public void displayPlayersReadyToStartMessage(List<String> p) {
        if(setupSceneController!=null)setupSceneController.displayPlayersReadyToStartMessage(p);
    }


    // *********************************************************************  //
    //                             SETUP GUI                                  //
    // *********************************************************************  //

    @Override
    public void loadLeaderCards(List<LightLeaderCard> leaderCards){
        MatchData.getInstance().setAllLeaderCards(leaderCards);
    }

    @Override

    public void displayChooseResourceTypeRequest(List<Resource> resourceTypes, int quantity) {
        gameSceneController.displayChooseResourceTypeRequest(quantity);
    }

    @Override
    public void displayChooseLeaderCardsRequest(List<Integer> leaderCards){
        resetControllers();
        instantiateGameScene();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameSceneController.displayLeaderCardsRequest(leaderCards, client);
            }
        });
    }

    /**
     *
     * @param lightDevelopmentCards the development cards to load
     */
    public void loadDevelopmentCards(List<LightDevelopmentCard> lightDevelopmentCards) {
        MatchData.getInstance().setAllDevelopmentCards(lightDevelopmentCards);
    }

    // *********************************************************************  //
    //                          SINGLE PLAYER                                 //
    // *********************************************************************  //

    @Override
    public void displayLorenzoAction(int id) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameSceneController.displayLorenzoAction(id);
            }
        });
    }

    // *********************************************************************  //
    //                          CHOOSE ACTION GUI                             //
    // *********************************************************************  //

    @Override
    public void displayChooseActionRequest(Map<ActionType, Boolean> executableActions, boolean standardActionDone) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameSceneController.displayChooseActionRequest(executableActions,standardActionDone);
            }
        });
    }

    // *********************************************************************  //
    //                   TAKE RESOURCES FROM MARKET GUI                       //
    // *********************************************************************  //

    @Override
    public void displayMarbleInsertionPositionRequest() {
        gameSceneController.displayMarbleInsertionPositionRequest();
    }

    @Override
    public void displayChooseWhiteMarbleConversionRequest(List<Resource> resources, int numberOfMarbles) {
        gameSceneController.displayChooseWhiteMarbleConversionRequest(resources, numberOfMarbles);
    }

    @Override
    public void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion) {
        //empty
    }

    // *********************************************************************  //
    //                         ORGANIZE DEPOTS GUI                            //
    // *********************************************************************  //

    @Override
    public void displayChooseStorageTypeRequest(Resource resource, List<String> availableDepots, boolean canDiscard, boolean canReorganize) {
        HashMap<ResourceStorageType,Boolean> interactableDepots=new HashMap<>();
        for(ResourceStorageType resourceStorageType: ResourceStorageType.values()){
            interactableDepots.put(resourceStorageType,false);
        }
        for(String s: availableDepots){
            interactableDepots.put(ResourceStorageType.valueOf(s),true);
        }
        gameSceneController.displayChooseStorageTypeRequest(resource,interactableDepots,canDiscard,canReorganize);
    }

    @Override
    public void displayReorganizeDepotsRequest(List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResource){
        gameSceneController.displayReorganizeDepotsRequest(depots,failure,availableLeaderResource);
    }

    @Override
    public void displaySelectStorageRequest(Resource resource, boolean isInWarehouse, boolean isInStrongbox, boolean isInLeaderDepot) {
        gameSceneController.displaySelectStorageRequest(resource,isInWarehouse,isInStrongbox,isInLeaderDepot);
    }

    @Override
    public void displayResourcesToStore(List<Resource> resourcesToStore){
        gameSceneController.displayNotifyResourcesToStore(resourcesToStore);
    }

    // *********************************************************************  //
    //                            PRODUCTION GUI                              //
    // *********************************************************************  //

    @Override
    public void displayChooseProductionPowersRequest(Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources) {
        UtilityProduction.initialize(this, client, availableProductionPowers, availableResources);
    }

    public void displayProductionCardYouCanSelect(List<Integer> IDs, List<Value> basicProduction) {
        gameSceneController.displayProductionCardYouCanSelect(IDs);
    }

    @Override
    public void displayChooseProduction(List<Integer> availableProductionIDs, Map<Resource, Integer> availableResources, boolean addORremove) {
        if(availableProductionIDs.size() == 0){
            chooseNextProductionAction();
            return;
        }
        gameSceneController.displayChooseProduction(availableProductionIDs,availableResources,addORremove);
    }

    @Override
    public void displayCurrentSelectedProductions(Set<Integer> productionIDs, List<Value> basicProduction) {
        //empty
    }

    @Override
    public void chooseNextProductionAction() {
        gameSceneController.chooseNextProductionAction();
    }

    // *********************************************************************  //
    //                              CARDS GUI                                 //
    // *********************************************************************  //

    @Override
    public void displaySelectDevelopmentCardSlotRequest(boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable) {
        gameSceneController.displaySelectDevelopmentCardSlotRequest(firstSlotAvailable,secondSlotAvailable,thirdSlotAvailable);
    }

    @Override
    public void displaySelectCardRequest(List<Integer> cardIDs, boolean leaderORdevelopment) {
        gameSceneController.displaySelectCardRequest(cardIDs,leaderORdevelopment);
    }

    // *********************************************************************  //
    //                             MATCHDATA UPDATE                           //
    // *********************************************************************  //


    /**
     * Method to manage the MatchData messages from server, updating its content
     */
    @Override
    public void update(MatchDataMessage message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                MatchData.getInstance().update(message);
                if(gameSceneController!=null){
                    gameSceneController.updateView();
                    gameSceneController.updateMainLabel();
                    if(message instanceof TurnMessage){
                        gameSceneController.enableNextPreviousButtons();
                        gameSceneController.setCurrentPlayer(message.getNickname());
                    }
                }
            }
        });

    }

    public void setNicknames(String playerNickname, List<String> otherPlayersNicknames){
        MatchData.getInstance().setThisClient(playerNickname);
        MatchData.getInstance().resetOtherClients();
        for(String nickname : otherPlayersNicknames){
            MatchData.getInstance().addLightClient(nickname);
        }
    }

    @Override
    public void loadDevelopmentCardGrid(List<Integer> availableCardsIds) {
        MatchData.getInstance().loadDevelopmentCardGrid(availableCardsIds);
    }

    @Override
    public void displayStandardView() {
        if(gameSceneController!=null){
            gameSceneController.updateView();
        }
    }

    @Override
    public void setIsReloading(boolean reloading) {
        MatchData.getInstance().setReloading(reloading);
    }
}
