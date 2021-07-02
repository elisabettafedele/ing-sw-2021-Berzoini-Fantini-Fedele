package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.PopesTileState;
import it.polimi.ingsw.client.utilities.UtilityProduction;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.messages.toServer.game.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameSceneController {
    private GUI gui=null;
    private Client client=null;

    @FXML
    private AnchorPane rightPane;
    @FXML
    private AnchorPane leftPane;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Pane warehouse;
    @FXML
    private Pane warehouse_first_depot;
    @FXML
    private Pane warehouse_second_depot;
    @FXML
    private Pane warehouse_third_depot;
    @FXML
    private Pane faithtrack;
    @FXML
    private Pane firstSlot;
    @FXML
    private Pane secondSlot;
    @FXML
    private Pane thirdSlot;
    @FXML
    private Pane activateProductionPane;
    @FXML
    private Pane popeTiles;
    @FXML
    private Pane leftLeaderDepot;
    @FXML
    private Pane rightLeaderDepot;
    @FXML
    private GridPane strongbox;
    @FXML
    private GridPane developmentCardGrid;
    @FXML
    private GridPane marketGrid;

    @FXML
    private ImageView leftLeaderCard;
    @FXML
    private ImageView rightLeaderCard;
    @FXML
    private ImageView previousPlayerButton;
    @FXML
    private ImageView nextPlayerButton;
    @FXML
    private ImageView basicProduction;
    @FXML
    private ImageView slideMarble;
    @FXML
    private Label mainLabelNames;
    @FXML
    private Label mainLabelStats;
    @FXML
    private Label mainLabelMessage;
    @FXML
    private Label currentPBNicknameLabel;
    @FXML
    private VBox popupVbox;
    @FXML
    private VBox lorenzoVbox;
    @FXML
    private VBox reorganizationVbox;
    @FXML
    private VBox productionVbox;
    @FXML
    private VBox importantMessagesVbox;
    @FXML
    private HBox buttonsHbox;
    @FXML
    private Button reorganizeButton;
    @FXML
    private Button discardButton;
    @FXML
    private Button activateLeaderCardButton;
    @FXML
    private Button discardLeaderCardButton;
    @FXML
    private Button endTurnButton;

    Color colorToGlow= Color.CYAN;
    Color colorToAlternateGlow= Color.CORAL;

    List<String> players;
    int currentPlayerIndex;

    MatchData matchData;

    Map<ActionType,Boolean> executableActions;
    Map<ResourceStorageType,Node> storageNameToNodeMap;
    Map<String,Integer> marketArrowsNumMap;
    List<Resource> selectedResources;
    List<Integer> selectedLeaderCards;
    List<ResourceStorageType> reorganizeChosenDepots;
    List<Node> selectedProductions;
    String currentPlayer;

    boolean connectionClosedByClient = false;
    boolean isYourTurn;
    // *********************************************************************  //
    //                        INITIALIZING FUNCTIONS                          //
    // *********************************************************************  //

    /**
     * Initializes all controller's variables, buttons' default event handlers, choices hashmaps and
     */
    @FXML
    public void initialize() {
        selectedResources=new ArrayList<>();
        currentPlayer=new String();
        matchData=MatchData.getInstance();
        players=matchData.getAllNicknames();
        currentPlayerIndex=0;
        isYourTurn =false;
        executableActions = new HashMap<>();
        final boolean debug=false;
        executableActions.put(ActionType.TAKE_RESOURCE_FROM_MARKET,debug);
        executableActions.put(ActionType.BUY_DEVELOPMENT_CARD,debug);
        executableActions.put(ActionType.ACTIVATE_PRODUCTION,debug);
        executableActions.put(ActionType.ACTIVATE_LEADER_CARD,debug);
        executableActions.put(ActionType.DISCARD_LEADER_CARD,debug);
        storageNameToNodeMap=new HashMap<>();
        storageNameToNodeMap.put(ResourceStorageType.WAREHOUSE_FIRST_DEPOT,warehouse.getChildren().get(3));
        storageNameToNodeMap.put(ResourceStorageType.WAREHOUSE_SECOND_DEPOT,warehouse.getChildren().get(4));
        storageNameToNodeMap.put(ResourceStorageType.WAREHOUSE_THIRD_DEPOT,warehouse.getChildren().get(5));
        storageNameToNodeMap.put(ResourceStorageType.WAREHOUSE,warehouse);
        storageNameToNodeMap.put(ResourceStorageType.STRONGBOX,((Pane)strongbox.getParent()).getChildren().get(1));
        storageNameToNodeMap.put(ResourceStorageType.LEADER_DEPOT,rightLeaderCard);
        marketArrowsNumMap=new HashMap<>();
        marketArrowsNumMap.put("one",1);
        marketArrowsNumMap.put("two",2);
        marketArrowsNumMap.put("three",3);
        marketArrowsNumMap.put("four",4);
        marketArrowsNumMap.put("five",5);
        marketArrowsNumMap.put("six",6);
        marketArrowsNumMap.put("seven",7);
        previousPlayerButton.setDisable(true);
        greyNode(previousPlayerButton);
        nextPlayerButton.setDisable(true);
        greyNode(nextPlayerButton);
        activateLeaderCardButton.setVisible(false);
        discardLeaderCardButton.setVisible(false);
        if(players.size()>1) mainLabelMessage.setText("Wait for other players");
        else{
            updateMainLabel();
            nextPlayerButton.setVisible(false);
            nextPlayerButton.setManaged(false);
            previousPlayerButton.setVisible(false);
            previousPlayerButton.setManaged(false);
            currentPBNicknameLabel.setVisible(false);
            currentPBNicknameLabel.setManaged(false);
        }
        endTurnButton.setVisible(false);
        endTurnButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                deactivateAllActionsGlowing();
                endTurnButton.setVisible(false);
                isYourTurn=false;
                client.sendMessageToServer(new EndTurnRequest());
            }
        });
        updateView();
        leftLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/back/leaderCardsBack.png").toString()));
        rightLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/back/leaderCardsBack.png").toString()));
        reorganizationVbox.setVisible(false);
        //set behaviour of endReorganizationButton
        ((Button)((HBox)reorganizationVbox.getChildren().get(1)).getChildren().get(2)).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                reorganizationVbox.setVisible(false);
                client.sendMessageToServer(new NotifyEndDepotsReorganization());
            }
        });
        //set behaviour of productionVbox buttons
        selectedProductions=new ArrayList<>();
        productionVbox.setVisible(false);
        ((Button)((HBox)productionVbox.getChildren().get(2)).getChildren().get(0)).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                UtilityProduction.displayAvailableProductions();
            }
        });
        ((Button)((HBox)productionVbox.getChildren().get(2)).getChildren().get(1)).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                UtilityProduction.chooseProductionToRemove();
            }
        });
        ((Button)((HBox)productionVbox.getChildren().get(2)).getChildren().get(2)).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                UtilityProduction.confirmChoices();
                productionVbox.setVisible(false);
                for(int i=1;i<4;i++){
                    ((Pane)basicProduction.getParent()).getChildren().get(i).setVisible(false);
                }
               for(Node node : selectedProductions){
                   node.setEffect(null);
               }
               ((Pane)leftLeaderCard.getParent()).getChildren().get(6).setVisible(false);
                ((Pane)leftLeaderCard.getParent()).getChildren().get(7).setVisible(false);
            }
        });
        lorenzoVbox.setVisible(false);
        ((Button)lorenzoVbox.getChildren().get(2)).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                lorenzoVbox.setVisible(false);
            }
        });
        importantMessagesVbox.setManaged(false);
        importantMessagesVbox.setVisible(false);
        ((Button)importantMessagesVbox.getChildren().get(1)).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                importantMessagesVbox.setManaged(false);
                importantMessagesVbox.setVisible(false);
                leftPane.setDisable(false);
                rightPane.setDisable(false);
            }
        });

    }

    public void setGUI(GUI gui) {
        this.gui=gui;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setCurrentPlayer(String nickname) {
        currentPlayer=nickname;
        if(!(currentPlayer.equals(players.get(0)))){
            isYourTurn=false;
        }
    }
    // *********************************************************************  //
    //                           UTILITY FUNCTIONS                            //
    // *********************************************************************  //

    /**
     * Installs a Tooltip on given node, meaning that when user hovers his mouse on it
     * the given message is displayed
     * @param node node to install the tooltip on
     * @param message message to be displayed
     */
    private void createTooltip(Node node, String message){
        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDelay(Duration.seconds(1));
        tooltip.setHideDelay(Duration.seconds(0.5));
        Tooltip.install(node,tooltip);
    }

    /**
     * Applies a glowing effect on given node
     * @param nodeToGlow node that will glow
     * @param color glowing color
     */
    private void glowNode(Node nodeToGlow,Color color){
        DropShadow borderGlow = new DropShadow();
        int depth = 40;
        borderGlow.setColor(color);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        nodeToGlow.setEffect(borderGlow);
    }

    /**
     * Turns given node colors to a more grey scale of colors (lower brightness), giving it a 'disabled appearance'
     * @param nodeToGrey node to be greyed
     */
    private void greyNode(Node nodeToGrey){
        ColorAdjust colorAdjust=new ColorAdjust();
        colorAdjust.setBrightness(0.4);
        nodeToGrey.setEffect(colorAdjust);
    }

    /**
     * Sets the current viewed player to the next on players list, then
     * updates all view's component to display his personal boards and all
     * other player's info, basically seeing what he's seeing. It also disable action
     * selection if it's your turn but you're not viewing your personal board
     */
    @FXML
    public void onNextPlayerButtonPressed(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if((currentPlayerIndex+1)==players.size()){
                    currentPlayerIndex=0;
                }
                else{
                    currentPlayerIndex++;
                }
                updateGlowingObjects();
                updateView();
            }
        });
    }

    /**
     * Sets the current viewed player to the previous on players list, then
     * updates all view's component to display his personal boards and all
     * other player's info, basically seeing what he's seeing. It also disable action
     * selection if it's your turn but you're not viewing your personal board
     */
    @FXML
    public void onPreviousPlayerButtonPressed(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(currentPlayerIndex==0){
                    currentPlayerIndex=players.size()-1;
                }
                else{
                    currentPlayerIndex--;
                }
                updateGlowingObjects();
                updateView();
            }
        });
    }

    /**
     * disables the ability to look at other players' personal boards while you're performing an action
     */
    public void disableNextPreviousButtons() {
        greyNode(nextPlayerButton);
        nextPlayerButton.setDisable(true);
        greyNode(previousPlayerButton);
        previousPlayerButton.setDisable(true);
    }

    /**
     * enables the ability to look at other players' personal boards
     */
    public void enableNextPreviousButtons() {
        nextPlayerButton.setEffect(null);
        nextPlayerButton.setDisable(false);
        previousPlayerButton.setEffect(null);
        previousPlayerButton.setDisable(false);
    }

    // *********************************************************************  //
    //                        UPDATING VIEW FUNCTIONS                         //
    // *********************************************************************  //

    /**
     * It calls all minor updating view functions, reading {@link MatchData} for possible changes and updating
     * the whole view
     */
    public void updateView() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                players=matchData.getAllNicknames();
                if (!matchData.isReloading()) {
                    updateMainLabel();
                    updateDevelopmentCardGridView();
                    updateMarketView();
                    updateFaithTrack();
                    updateWarehouseAndStrongboxView();
                    updateDevelopmentCardsSlots();
                    updateLeaderCardsView();
                    if(currentPlayerIndex==0){
                        currentPBNicknameLabel.setText("You");
                    }
                    else{
                        currentPBNicknameLabel.setText(players.get(currentPlayerIndex));
                    }
                }
            }
        });

    }

    /**
     * based on {@link MatchData} info, it displays players' names, faith track positions and earned victory points,
     * as well as which player is playing the turn.
     */
    public void updateMainLabel() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainLabelNames.setText("");
                mainLabelStats.setText("");
                mainLabelMessage.setFont(new Font(mainLabelNames.getFont().toString(),8));
                mainLabelStats.setFont(new Font(mainLabelNames.getFont().toString(),8));
                mainLabelNames.setFont(new Font(mainLabelNames.getFont().toString(),8));
                for(int i=0;i<players.size();i++){
                    mainLabelNames.setText(mainLabelNames.getText() + players.get(i) + "\n");
                    mainLabelStats.setText(mainLabelStats.getText() + "FT: " + matchData.getLightClientByNickname(players.get(i)).getFaithTrackPosition() + " VP: " + matchData.getLightClientByNickname(players.get(i)).getVictoryPoints() + "\n");
                }
                if(players.size()>1){
                    if(currentPlayer.equals(players.get(0)))mainLabelMessage.setText("It's your turn!");
                    else mainLabelMessage.setText("It's " + currentPlayer + "'s turn");
                    if(currentPlayer.isEmpty()) mainLabelMessage.setText("Wait the other players");
                }
                else{
                    mainLabelNames.setText(mainLabelNames.getText() + "Lorenzo\n");
                    mainLabelStats.setText(mainLabelStats.getText() + "FT: " + matchData.getBlackCrossPosition()+ "\n");
                }
            }
        });
    }

    /**
     * based on {@link MatchData} info, it displays the correct development cards owned by the player corresponding to currentPlayerIndex
     * (the player whose personal board and other info are being displayed, may it be you or another player)
     */
    private void updateDevelopmentCardsSlots() {
        basicProduction.setVisible(true);
        firstSlot.getChildren().forEach(node -> node.setVisible(false));
        secondSlot.getChildren().forEach(node -> node.setVisible(false));
        thirdSlot.getChildren().forEach(node -> node.setVisible(false));
        Stack<Integer>[] developmentCardSlots= matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getDevelopmentCardSlots();
        int cardCounter=0;
        for(Node cardOfSlot : firstSlot.getChildren()){
            if(cardCounter<developmentCardSlots[0].size()){
                ((ImageView) cardOfSlot).setImage(new Image(GameSceneController.class.getResource("/img/Cards/DevelopmentCards/front/" + developmentCardSlots[0].get(cardCounter) + ".png").toString()));
                cardOfSlot.setVisible(true);
            }
            cardCounter++;
        }
        cardCounter=0;
        for(Node cardOfSlot : secondSlot.getChildren()){
            if(cardCounter<developmentCardSlots[1].size()){
                ((ImageView) cardOfSlot).setImage(new Image(GameSceneController.class.getResource("/img/Cards/DevelopmentCards/front/" + developmentCardSlots[1].get(cardCounter) + ".png").toString()));
                cardOfSlot.setVisible(true);
            }
            cardCounter++;
        }
        cardCounter=0;
        for(Node cardOfSlot : thirdSlot.getChildren()){
            if(cardCounter<developmentCardSlots[2].size()){
                ((ImageView) cardOfSlot).setImage(new Image(GameSceneController.class.getResource("/img/Cards/DevelopmentCards/front/" + developmentCardSlots[2].get(cardCounter) + ".png").toString()));
                cardOfSlot.setVisible(true);
            }
            cardCounter++;
        }
    }

    /**
     * based on {@link MatchData} info, it displays the redCross on the correct position in the FaithTrack of the player corresponding to currentPlayerIndex
     * (the player whose personal board and other info are being displayed, may it be you or another player). It also shows all taken/missed Pope's Tiles.
     */
    private void updateFaithTrack() {
        int playerPos=matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getFaithTrackPosition();
        if(playerPos>24){
            playerPos=24;
        }
        for(Node ftBox : faithtrack.getChildren()){
            ftBox.setVisible(false);
        }
        if(players.size()==1){
            for(Node ftBox : faithtrack.getChildren()){
                ((ImageView)ftBox).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/faithSign.png").toString()));
            }
            int blackCrossPos= matchData.getBlackCrossPosition();
            if(blackCrossPos>24) blackCrossPos=24;
            ((ImageView)faithtrack.getChildren().get(blackCrossPos)).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/blackCross.png").toString()));
            if(playerPos==blackCrossPos)((ImageView)faithtrack.getChildren().get(blackCrossPos)).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/faithAndBlackCross.png").toString()));
            faithtrack.getChildren().get(blackCrossPos).setVisible(true);
        }
        faithtrack.getChildren().get(playerPos).setVisible(true);
        int tileCounter=0;
        for(Node popeTile: popeTiles.getChildren()){
            PopesTileState popesTileState=matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getPopesTileStates()[tileCounter];
            if(popesTileState.equals(PopesTileState.NOT_REACHED)){
                popeTile.setVisible(false);
            }
            if(popesTileState.equals(PopesTileState.TAKEN)){
                ((ImageView) popeTile).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/vrs" + (tileCounter+1) + "yes.png").toString()));
                popeTile.setVisible(true);
            }
            if(popesTileState.equals(PopesTileState.NOT_TAKEN)){
                ((ImageView) popeTile).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/vrs" + (tileCounter+1) + "no.png").toString()));
                popeTile.setVisible(true);
            }
            tileCounter++;
        }
    }

    /**
     * based on {@link MatchData} info, it displays the resources in warehouse and in strongbox of the player corresponding to currentPlayerIndex
     * (the player whose personal board and other info are being displayed, may it be you or another player)
     */
    private void updateWarehouseAndStrongboxView() {
        int[] strongbox=matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getStrongbox();
        List<Resource>[] warehouse =matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getWarehouse();
        for(int i=0;i<strongbox.length;i++){
            //text elements in strongbox grid come after 4 image elements
            ((Text) this.strongbox.getChildren().get(i+4)).setText("x"+strongbox[i]);
        }
        for(int i=0; i< warehouse.length;i++){
            int boxCounter=0;
            for(Node warehouseDepotBox: ((Pane) this.warehouse.getChildren().get(i)).getChildren()){
                warehouseDepotBox.setVisible(false);
                if(boxCounter<warehouse[i].size()){
                    ((ImageView) warehouseDepotBox).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/" + warehouse[i].get(boxCounter).toString().toLowerCase() + ".png").toString()));
                    warehouseDepotBox.setVisible(true);
                }
                boxCounter++;
            }
        }
    }

    /**
     * based on {@link MatchData} info, it displays the Leader Cards of the player if user is looking at his own personal
     * board,with grey LeaderCards for those Loader Cards which are still disabled. If user is looking at someone else's
     * personal board, only activated Leader Cards are shown, while others are turned on their back.
     */
    private void updateLeaderCardsView() {
        List<LightLeaderCard> leaderCards =new ArrayList<>();
        for(Integer lcID :matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getOwnedLeaderCards()){
            leaderCards.add(matchData.getLeaderCardByID(lcID));
        }
        //initializes visibilities
        rightLeaderCard.setVisible(false);
        leftLeaderCard.setVisible(false);
        rightLeaderDepot.setVisible(true);
        leftLeaderDepot.setVisible(true);
        for(Node resource : leftLeaderDepot.getChildren()){
            resource.setVisible(false);
        }
        for(Node resource : rightLeaderDepot.getChildren()){
            resource.setVisible(false);
        }

        for(int i=0; i<leaderCards.size(); i++){
            //if active, it shows the LeaderCard
            if(matchData.getLightClientByNickname(players.get(currentPlayerIndex)).leaderCardIsActive(leaderCards.get(i).getID())){
                if(i==0){
                    leftLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/front/" + leaderCards.get(i).getID() + ".png").toString()));
                    if(! (leftLeaderCard.getEffect() instanceof DropShadow)) leftLeaderCard.setEffect(null);
                    leftLeaderCard.setVisible(true);
                }
                if(i==1){
                    rightLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/front/" + leaderCards.get(i).getID() + ".png").toString()));
                    if(! (rightLeaderCard.getEffect() instanceof DropShadow))rightLeaderCard.setEffect(null);
                    rightLeaderCard.setVisible(true);
                }
                //if extra depot leaderCard, it shows the resources in it, if any
                if(leaderCards.get(i).getEffectType().equals("EXTRA_DEPOT")){
                    int quantity= matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getLeaderDepots().get(leaderCards.get(i).getID());
                    if(i==0){
                        for(int ii=0;ii<leftLeaderDepot.getChildren().size();ii++){
                            if(ii<quantity){
                                ((ImageView) leftLeaderDepot.getChildren().get(ii)).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/" + leaderCards.get(i).getEffectDescription().get(0).toLowerCase() + ".png").toString()));
                                leftLeaderDepot.getChildren().get(ii).setVisible(true);
                            }
                        }
                    }
                    if(i==1){
                        for(int ii=0;ii<rightLeaderDepot.getChildren().size();ii++) {
                            if (ii < quantity) {
                                ((ImageView) rightLeaderDepot.getChildren().get(ii)).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/" + leaderCards.get(i).getEffectDescription().get(0).toLowerCase() + ".png").toString()));
                                rightLeaderDepot.getChildren().get(ii).setVisible(true);
                            }
                        }
                    }
                }
            }
            //if card is inactive
            else{
                //if player is watching its leaderCards he can see the inactive ones too
                if(currentPlayerIndex==0 && leaderCards.size()!=4){
                    if(i==0){
                        leftLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/front/" + leaderCards.get(i).getID() + ".png").toString()));
                        greyNode(leftLeaderCard);
                        leftLeaderCard.setVisible(true);
                    }
                    if(i==1){
                        rightLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/front/" + leaderCards.get(i).getID() + ".png").toString()));
                        greyNode(rightLeaderCard);
                        rightLeaderCard.setVisible(true);
                    }
                }
                //if player is not watching its leaderCards, he can only see the back of inactive cards
                else{
                    if(i==0){
                        leftLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/back/leaderCardsBack.png").toString()));
                        leftLeaderCard.setEffect(null);
                        leftLeaderCard.setVisible(true);
                    }
                    if(i==1){
                        rightLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/back/leaderCardsBack.png").toString()));
                        rightLeaderCard.setEffect(null);
                        rightLeaderCard.setVisible(true);
                    }
                }

            }
        }
    }

    /**
     * based on {@link MatchData} info, it displays Market's current marbles' layout
     */
    private void updateMarketView() {
        if(matchData.getMarketTray()!=null){
            for(int row=0;row< 3 ;row++ ){
                for(int col=0; col< 4; col++){
                    ((ImageView) marketGrid.getChildren().get(col+row*4)).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/marble_" + matchData.getMarketTray()[row][col].toString().toLowerCase() + ".png").toString()));
                }
            }
            slideMarble.setImage(new Image(GameSceneController.class.getResource("/img/punchboard/marble_" + matchData.getSlideMarble().toString().toLowerCase() + ".png").toString()));
        }
    }

    /**
     * based on {@link MatchData} info, it displays DevelopmentCardGrid's current cards' layout
     */
    private void updateDevelopmentCardGridView() {
        List<LightDevelopmentCard> developmentCardGrid =new ArrayList<>();
        List<Integer> gridCardsIDs = matchData.getDevelopmentCardGrid();
        this.developmentCardGrid.getChildren().forEach(x->x.setVisible(false));
        if (gridCardsIDs != null) {
            for(Integer devCardId : gridCardsIDs){
                developmentCardGrid.add(matchData.getDevelopmentCardByID(devCardId));
            }
            for(LightDevelopmentCard devCard : developmentCardGrid){
                int row = (Level.valueOf(devCard.getFlagLevel()).getValue() * - 1) + 2;
                int col = FlagColor.valueOf(devCard.getFlagColor()).getValue();
                ((ImageView) this.developmentCardGrid.getChildren().get(col+ this.developmentCardGrid.getColumnCount()*row)).setImage(new Image(GameSceneController.class.getResource("/img/Cards/DevelopmentCards/front/" + devCard.getID() + ".png").toString()));
                this.developmentCardGrid.getChildren().get(col+ this.developmentCardGrid.getColumnCount()*row).setVisible(true);
            }
        }

    }

    // *********************************************************************  //
    //                   TURN (SELECT ACTION) FUNCTIONS                       //
    // *********************************************************************  //

    /**
     * It enables next and previous buttons (to see other players Personal Boards), and makes clickable parts of the interface
     * corresponding to doable actions,  glowing them when you hover your mouse cursor over them.
     * @param executableActions Map of action types and booleans, equals to true if player can do that action, false otherwise
     * @param standardActionDone true if player has already done a standard action and can only discard/activate his leader cards or end his turn
     */
    public void displayChooseActionRequest(Map<ActionType, Boolean> executableActions, boolean standardActionDone) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GameSceneController.this.executableActions =executableActions;
                isYourTurn=true;
                endTurnButton.setVisible(standardActionDone);
                if(executableActions.keySet().isEmpty()){
                    endTurnButton.setVisible(true);
                }
                updateGlowingObjects();
                enableNextPreviousButtons();
            }
        });
    }

    /**
     * updates all view's glowing objects and connect relative event handlers only on elements (nodes) that should
     * be clicked (selectable actions). If it is not player's turn or he's viewing another player's personal board,
     * all nodes event handlers and glowing are deactivated
     */
    private void updateGlowingObjects() {
        deactivateAllActionsGlowing();
        if(currentPlayerIndex==0&&isYourTurn){
            List<LightLeaderCard> leaderCards =new ArrayList<>();
            for(Integer lcID :matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getOwnedLeaderCards()){
                leaderCards.add(matchData.getLeaderCardByID(lcID));
            }
            for(ActionType actionType : executableActions.keySet()){
                if(executableActions.get(actionType)){
                    switch (actionType){
                        case TAKE_RESOURCE_FROM_MARKET:
                            activateGlowingAndSelectEventHandler(marketGrid,false,actionType);
                            break;
                        case ACTIVATE_PRODUCTION:
                            activateGlowingAndSelectEventHandler(activateProductionPane,false,actionType);
                            int counter=0;
                            for(LightLeaderCard lc : leaderCards){
                                if(matchData.getLightClientByNickname(players.get(currentPlayerIndex)).leaderCardIsActive(lc.getID())&& lc.getEffectType().equals(EffectType.PRODUCTION.toString())){
                                    activateGlowingAndSelectEventHandler(((Pane)leftLeaderCard.getParent()).getChildren().get(counter*2),false,actionType);
                                }
                                counter++;
                            }
                            break;
                        case BUY_DEVELOPMENT_CARD:
                            activateGlowingAndSelectEventHandler(developmentCardGrid,false,actionType);
                            break;
                        case ACTIVATE_LEADER_CARD:
                            int i=0;
                            for(LightLeaderCard lc : leaderCards){
                                if(!matchData.getLightClientByNickname(players.get(currentPlayerIndex)).leaderCardIsActive(lc.getID())){
                                    activateGlowingAndSelectEventHandler(((Pane)leftLeaderCard.getParent()).getChildren().get(i*2),true,actionType);
                                }
                                i++;
                            }
                        case DISCARD_LEADER_CARD:
                            int ii=0;
                            if(!executableActions.get(ActionType.ACTIVATE_LEADER_CARD)){
                                for(LightLeaderCard lc : leaderCards){
                                    if(!matchData.getLightClientByNickname(players.get(currentPlayerIndex)).leaderCardIsActive(lc.getID())){
                                        activateGlowingAndSelectEventHandler(((Pane)leftLeaderCard.getParent()).getChildren().get(ii*2),true,actionType);
                                    }
                                    ii++;
                                }
                            }
                    }
                }
            }
        }
    }

    /**
     * deactivates glowing and event handler from all nodes
     */
    private void deactivateAllActionsGlowing(){
        deactivateGlowingAndSelectEventHandler(developmentCardGrid,false);
        if(currentPlayerIndex==0){
            //list of booleans, true if card is active or discarded, false if inactive. If inactive, it makes the card grey
            List<Boolean> leaderCardsBooleans= new ArrayList<>();
            leaderCardsBooleans=matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getOwnedLeaderCards().stream().map(lcId->matchData.getLightClientByNickname(players.get(currentPlayerIndex)).leaderCardIsActive(lcId)).collect(Collectors.toList());
            for(int i=2;i>leaderCardsBooleans.size();i--){
                leaderCardsBooleans.add(true);
            }
            if(leaderCardsBooleans.size()>0)deactivateGlowingAndSelectEventHandler(leftLeaderCard,!leaderCardsBooleans.get(0));
            if(leaderCardsBooleans.size()>1)deactivateGlowingAndSelectEventHandler(rightLeaderCard,!leaderCardsBooleans.get(1));
        }
        else{
            deactivateGlowingAndSelectEventHandler(leftLeaderCard,false);
            deactivateGlowingAndSelectEventHandler(rightLeaderCard,false);
        }
        deactivateGlowingAndSelectEventHandler(activateProductionPane,false);
        deactivateGlowingAndSelectEventHandler(marketGrid,false);
    }

    /**
     * Deactivates selected node's event handlers and glowing effect
     * @param nodeToDeactivate node to deactivate
     * @param backToGreyLeaderCard true if node is a leaderCard that is not active and should get back to grey effect.
     */
    private void deactivateGlowingAndSelectEventHandler(Node nodeToDeactivate, boolean backToGreyLeaderCard){
        nodeToDeactivate.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //Overwrites any possible tooltip, removing them, then removes this one too, leaving the node with no tooltips.
                Tooltip tooltip= new Tooltip("Overwriting Tooltip");
                Tooltip.install(nodeToDeactivate, tooltip);
                Tooltip.uninstall(nodeToDeactivate, tooltip);
            }
        });
        nodeToDeactivate.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });
        nodeToDeactivate.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });
        nodeToDeactivate.setEffect(null);
        if(backToGreyLeaderCard){
            greyNode(nodeToDeactivate);
        }
    }


    /**
     * activates selected node's event handlers and glowing effect. Node glows when mouse cursor is on it, and shows a tooltip
     * of the action performed if clicked. This means the node becomes also clickable
     * @param nodeToActivate node to activate
     * @param backToGreyLeaderCard true if node is a leaderCard that is not active and should get back to grey effect when mouse cursor moves out of node
     * @param actionType type of the action chosen if node is clicked
     */
    private void activateGlowingAndSelectEventHandler(Node nodeToActivate, boolean backToGreyLeaderCard, ActionType actionType){
        nodeToActivate.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                glowNode(nodeToActivate,colorToGlow);
                if(actionType.equals(ActionType.ACTIVATE_LEADER_CARD)){
                    createTooltip(nodeToActivate,actionType.toString().replace('_',' ') + "OR" + ActionType.DISCARD_LEADER_CARD.toString().replace('_',' ') );
                }
                else{
                    createTooltip(nodeToActivate,actionType.toString().replace('_',' '));
                }
            }
        });
        nodeToActivate.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                nodeToActivate.setEffect(null);
                if(backToGreyLeaderCard){
                    greyNode(nodeToActivate);
                }
            }
        });
        nodeToActivate.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(actionType.equals(ActionType.ACTIVATE_LEADER_CARD)){
                    activateLeaderCardButton.setVisible(true);
                    discardLeaderCardButton.setVisible(true);
                    activateLeaderCardButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            selectAction(ActionType.ACTIVATE_LEADER_CARD.toString());
                            activateLeaderCardButton.setVisible(false);
                            discardLeaderCardButton.setVisible(false);
                        }
                    });
                    discardLeaderCardButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            selectAction(ActionType.DISCARD_LEADER_CARD.toString());
                            activateLeaderCardButton.setVisible(false);
                            discardLeaderCardButton.setVisible(false);
                        }
                    });
                }
                else{
                      selectAction(actionType.toString());
                }
                deactivateAllActionsGlowing();
            }
        });
    }

    /**
     * Sends to server the selected action
     * @param selectedAction action selected as string
     */
    private void selectAction(String selectedAction) {
        disableNextPreviousButtons();
        client.sendMessageToServer(new ChooseActionResponse(ActionType.valueOf(selectedAction).getValue()));
    }


    // *********************************************************************  //
    //                             RESOURCE INSERTION                         //
    // *********************************************************************  //

    /**
     * Displays the panel containing the resources the user needs to chose where to store.
     * It also displays buttons to choose to discard the resource or reorganize the warehouse
     * @param resources list of resources the user needs to chose where to store
     */
    public void displayNotifyResourcesToStore(List<Resource> resources) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                popupVbox.setManaged(true);
                popupVbox.setVisible(true);
                discardButton.setManaged(true);
                reorganizeButton.setManaged(true);
                discardButton.setVisible(true);
                reorganizeButton.setVisible(true);
                discardButton.setText("Discard");
                HBox resourcesHBox=(HBox) popupVbox.getChildren().get(1);
                for(Resource resource: resources){
                    ImageView resourceImage= new ImageView( new Image(SetupSceneController.class.getResource("/img/punchboard/" + resource.toString().toLowerCase(Locale.ROOT) + ".png").toString()));
                    resourceImage.setPreserveRatio(true);
                    resourceImage.setFitHeight(((ImageView)warehouse_first_depot.getChildren().get(0)).getFitHeight());
                    resourcesHBox.getChildren().add(resourceImage);
                }
            }
        });
    }

    /**
     * Displays, resource per resource, the depots where the resource can be put. It also glows and assigns event handlers
     * to depots and the resource to handle the resource dragged into the depot.
     * @param resource resource to store in depots
     * @param interactableDepots depots where you can put  the resource in (if you can put it in it, value is true, false otherwise)
     * @param canDiscard true if resource can be discarded
     * @param canReorganize true if reorganization possibility should be given to player
     */
    public void displayChooseStorageTypeRequest(Resource resource, HashMap<ResourceStorageType, Boolean> interactableDepots, boolean canDiscard, boolean canReorganize) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                popupVbox.setVisible(true);
                discardButton.setVisible(canDiscard);
                discardButton.setManaged(canDiscard);
                discardButton.setDisable(!canDiscard);
                reorganizeButton.setVisible(canReorganize);
                reorganizeButton.setManaged(canReorganize);
                reorganizeButton.setDisable(!canReorganize);

                popupVbox.setVisible(true);
                ((Label) popupVbox.getChildren().get(0)).setText("Drag the resource into one of \nthe glowing depots, if any");
                HBox resourcesHBox=(HBox) popupVbox.getChildren().get(1);
                highlightAndDrag(resourcesHBox.getChildren().get(0),resource.toString(), interactableDepots);
                discardButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        ((HBox) popupVbox.getChildren().get(1)).getChildren().remove(0);
                        resetStorageInsertion();
                        popupVbox.setVisible(false);
                        popupVbox.setManaged(false);
                        client.sendMessageToServer(new DiscardResourceRequest(resource));
                    }
                });
                reorganizeButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        resetStorageInsertion();
                        popupVbox.setVisible(false);
                        popupVbox.setManaged(false);
                        client.sendMessageToServer(new ReorganizeDepotRequest());
                    }
                });
            }
        });
    }

    /**
     * Glow depots and resource and connects event handlers to all resource and depots nodes.
     * @param resourceToDrag resource to be dragged
     * @param resourceAsString resource as string
     * @param interactableDepots depots where you can drag the resource
     */
    private void highlightAndDrag(Node resourceToDrag, String resourceAsString, HashMap<ResourceStorageType, Boolean> interactableDepots) {
        glowNode(resourceToDrag,colorToGlow);
        makeDraggable(resourceToDrag, resourceAsString);
        for(ResourceStorageType resourceStorageType: interactableDepots.keySet()){
            if(interactableDepots.get(resourceStorageType)){
                if (resourceStorageType.equals(ResourceStorageType.LEADER_DEPOT)){
                    int lcCounter=0;
                    for(LightLeaderCard lc: matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards().stream().map(x -> matchData.getLeaderCardByID(x)).collect(Collectors.toList())){
                        if(lc.getEffectType().equals("EXTRA_DEPOT")&&lc.getEffectDescription().get(0).equals(resourceAsString)){
                            if(lcCounter == 0) {
                                glowNode(leftLeaderCard,colorToGlow);
                                ((DropShadow)leftLeaderCard.getEffect()).setHeight(70);
                                ((DropShadow)leftLeaderCard.getEffect()).setWidth(70);
                                activateDraggableOver(leftLeaderDepot);
                            }
                            if(lcCounter==1){
                                glowNode(rightLeaderCard,colorToGlow);
                                ((DropShadow)rightLeaderCard.getEffect()).setHeight(70);
                                ((DropShadow)rightLeaderCard.getEffect()).setWidth(70);
                                activateDraggableOver(rightLeaderDepot);
                            }
                        }
                        lcCounter++;
                    }
                }
                else {
                    if(resourceStorageType.equals(ResourceStorageType.WAREHOUSE)){
                        glowNode(storageNameToNodeMap.get(ResourceStorageType.WAREHOUSE_FIRST_DEPOT),colorToGlow);
                        glowNode(storageNameToNodeMap.get(ResourceStorageType.WAREHOUSE_SECOND_DEPOT),colorToGlow);
                        glowNode(storageNameToNodeMap.get(ResourceStorageType.WAREHOUSE_THIRD_DEPOT),colorToGlow);
                    }
                    else{
                        glowNode(storageNameToNodeMap.get(resourceStorageType),colorToGlow);
                    }
                    activateDraggableOver(storageNameToNodeMap.get(resourceStorageType));
                }

            }
        }
    }

    /**
     * deactivates all event handlers on depots, activated during resource insertion
     */
    private void resetStorageInsertion() {
        for(ResourceStorageType resourceStorageType : storageNameToNodeMap.keySet()){
            if(resourceStorageType.equals(ResourceStorageType.LEADER_DEPOT)){
                deactivateDraggableOver(leftLeaderDepot);
                deactivateDraggableOver(rightLeaderDepot);
                if(leftLeaderCard.getEffect() instanceof DropShadow){
                    leftLeaderCard.setEffect(null);
                }
                if(rightLeaderCard.getEffect() instanceof DropShadow){
                    rightLeaderCard.setEffect(null);
                }
            }
            else{
                deactivateDraggableOver(storageNameToNodeMap.get(resourceStorageType));
                storageNameToNodeMap.get(resourceStorageType).setEffect(null);
            }
        }
    }

    /**
     * Connects event handlers to given resource's node, making it draggable
     * @param resourceToDrag resource to be dragged
     * @param resourceAsString resource as string
     */
    private void makeDraggable(Node resourceToDrag, String resourceAsString) {
        resourceToDrag.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Dragboard db = resourceToDrag.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(resourceAsString);
                db.setContent(content);
            }
        });
        resourceToDrag.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getTransferMode() == TransferMode.MOVE) {
                    ((HBox) resourceToDrag.getParent()).getChildren().remove(0);
                }
            }
        });
    }

    /**
     * Makes selected node accepting dragged resources. When the user passes with the resource over it,
     * a visual prompt is given to make the user understand he can leave the mouse button. Resource will then be accepted
     * @param nodeDraggableOver node to be draggable over
     */
    private void activateDraggableOver(Node nodeDraggableOver) {
        nodeDraggableOver.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != nodeDraggableOver &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
        });
        nodeDraggableOver.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != nodeDraggableOver &&
                        event.getDragboard().hasString()) {
                    if(nodeDraggableOver.equals(leftLeaderDepot)){
                        glowNode(leftLeaderCard,colorToAlternateGlow);
                    }
                    else if(nodeDraggableOver.equals(rightLeaderDepot)){
                        glowNode(rightLeaderCard,colorToAlternateGlow);
                    }
                    else{
                        glowNode(nodeDraggableOver,colorToAlternateGlow);
                    }
                }
            }
        });
        nodeDraggableOver.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if(nodeDraggableOver.equals(leftLeaderDepot)){
                    glowNode(leftLeaderCard,colorToGlow);
                }
                else if(nodeDraggableOver.equals(rightLeaderDepot)){
                    glowNode(rightLeaderCard,colorToGlow);
                }
                else{
                    glowNode(nodeDraggableOver,colorToGlow);
                }
            }
        });
        nodeDraggableOver.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (event.getDragboard().hasString()) {
                    if(nodeDraggableOver.equals(leftLeaderDepot)){
                        addResource(rightLeaderCard,db.getString());//because storageNameToNodeMap only contains rightLeaderCard
                    }
                    else if(nodeDraggableOver.equals(rightLeaderDepot)){
                        addResource(rightLeaderCard,db.getString());
                    }
                    else{
                        addResource(nodeDraggableOver,db.getString());
                    }

                    success = true;
                }
                event.setDropCompleted(success);
            }
        });
    }

    /**
     * deactivates event handlers, disabling the possibility to drag resources over it
     * @param nodeDraggableOver node to deactivate
     */
    private void deactivateDraggableOver(Node nodeDraggableOver) {
        nodeDraggableOver.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
            }
        });
        nodeDraggableOver.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
            }
        });
        nodeDraggableOver.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
            }
        });
        nodeDraggableOver.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
            }
        });
    }

    /**
     * sends to Server the resource to add and in what  depots it has to be added
     * @param nodeDraggableOver the depot in which to put the resource
     * @param resourceToAddAsString resource to be added to the depot
     */
    private void addResource(Node nodeDraggableOver, String resourceToAddAsString) {
        String storageSelected= new String();
        for(ResourceStorageType rst: storageNameToNodeMap.keySet()){
            if(storageNameToNodeMap.get(rst).equals(nodeDraggableOver)||nodeDraggableOver.equals(leftLeaderCard)){
                storageSelected=rst.toString();
            }
        }
        resetStorageInsertion();
        ((Label)popupVbox.getChildren().get(0)).setText("Waiting the other players, the game will start \nas soon as they all be ready...");
        client.sendMessageToServer(new ChooseStorageTypeResponse(Resource.valueOf(resourceToAddAsString),storageSelected,discardButton.isVisible(),reorganizeButton.isVisible()));
        popupVbox.setVisible(false);
        popupVbox.setManaged(false);
    }


    // *********************************************************************  //
    //                        INITIAL GAME FUNCTIONS                          //
    // *********************************************************************  //

    /**
     * Displays the four leader cards of which the user should choose only two, then click on the confirm button.
     * It displays the panel and connect the click event handler to the button
     * @param leaderCards  leader Cards to be chosen
     * @param client client
     */
    public void displayLeaderCardsRequest(List<Integer> leaderCards, Client client) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                popupVbox.setPrefHeight(350);
                popupVbox.setPrefWidth(650);
                popupVbox.setVisible(true);
                //set Reorganize button invisible
                reorganizeButton.setVisible(false);
                reorganizeButton.setManaged(false);
                //turn DiscardButton into ConfirmSelectionButton
                Button confirmSelectionButton=discardButton;
                confirmSelectionButton.setText("Confirm");
                confirmSelectionButton.setDisable(true);
                confirmSelectionButton.setVisible(true);

                HBox selectionHBox=((HBox)popupVbox.getChildren().get(1));
                ((Label) popupVbox.getChildren().get(0)).setText("Choose two out of the four following Leader cards:");
                selectedLeaderCards =new ArrayList<>();

                HashMap<Integer, ImageView> leaderCardsMap= buildCards(leaderCards,confirmSelectionButton);
                for(ImageView lcImage: leaderCardsMap.values()){
                    selectionHBox.getChildren().add(lcImage);
                }
                confirmSelectionButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        handleConfirmSelectionButton(confirmSelectionButton,selectionHBox,((Label) popupVbox.getChildren().get(0)),leaderCards);
                    }
                });
            }

        });
    }

    /**
     * Instantiates all ImageView for each Card to be viewed
     * @param leaderCards cards to be built
     * @param confirmSelectionButton button on which connect event handler to confirm  cards choice
     * @return HashMap of leader cards id and relative ImageVIew
     */
    private HashMap<Integer, ImageView> buildCards(List<Integer> leaderCards, Button confirmSelectionButton) {
        HashMap<Integer,ImageView> leaderCardsMap=new HashMap<>();
        leaderCards.forEach(lc->{
            ImageView lcImage= new ImageView( new Image(SetupSceneController.class.getResource("/img/Cards/LeaderCards/front/" + lc + ".png").toString()));
            lcImage.setFitHeight(200);
            lcImage.setPreserveRatio(true);
            lcImage.setSmooth(true);
            lcImage.getStyleClass().add("cards");
            lcImage.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                if(selectedLeaderCards.contains(lc)){
                    for(int i = 0; i< selectedLeaderCards.size(); i++){
                        if(selectedLeaderCards.get(i)==lc){
                            selectedLeaderCards.remove(i);
                            lcImage.setEffect(null);
                        }
                    }
                    confirmSelectionButton.setDisable(true);
                }else if (selectedLeaderCards.size()<2){
                    selectedLeaderCards.add(lc);
                    greyNode(lcImage);
                    if(selectedLeaderCards.size()==2){
                        confirmSelectionButton.setDisable(false);
                    }
                }
            });
            leaderCardsMap.put(lc,lcImage);
        });
        return leaderCardsMap;
    }

    /**
     * Displays a panel with a confirmation button and a matrix of resource from which the user should choose the given quantity
     * @param quantity how many resources the user should choose
     */
    public void displayChooseResourceTypeRequest( int quantity) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                popupVbox.setVisible(true);
                Button confirmSelectionButton= discardButton;
                confirmSelectionButton.setDisable(true);
                confirmSelectionButton.setVisible(true);
                boolean[] selectedResourcesBooleans=new boolean[4*quantity];
                GridPane gridResources= new GridPane();
                HBox selectionHBox=((HBox)popupVbox.getChildren().get(1));
                selectionHBox.getChildren().add(gridResources);
                ((Label) popupVbox.getChildren().get(0)).setText("Choose " + quantity + (quantity>1?" resources":" resource"));
                selectedResources.clear();
                List<ImageView> resourcesImages= buildResources(quantity,selectedResourcesBooleans,confirmSelectionButton,null);
                int resCounter=0;
                for(int row=0;row<quantity;row++){
                    for(int col=0;col<4;col++){
                        gridResources.add(resourcesImages.get(resCounter), col, row);
                        resCounter++;
                    }
                }
                gridResources.setVisible(true);
            }
        });
    }

    /**
     * Instantiates all ImageView for each resource to be chosen
     * @param quantity quantity of resources to be chosen
     * @param selectedResourcesBooleans list of booleans used inside event handlers to see if resource has been chosen or not
     * @param confirmSelectionButton button used to confirm selection
     * @param resourcesToAdd resources to be put inside choices matrix
     * @return List of resources ImageViews
     */
    private List<ImageView> buildResources(int quantity, boolean[] selectedResourcesBooleans, Button confirmSelectionButton , List<Resource> resourcesToAdd) {
        if(resourcesToAdd==null){
            resourcesToAdd= Resource.realValues();
        }
        List<ImageView> resourcesImages=new ArrayList<>();
        AtomicInteger resCounter= new AtomicInteger();
        for(int ii=0; ii<quantity;ii++){
            resourcesToAdd.forEach(resource->{
                ImageView resourceImage= new ImageView( new Image(SetupSceneController.class.getResource("/img/punchboard/" + resource.toString().toLowerCase(Locale.ROOT) + ".png").toString()));
                resourceImage.setFitHeight(40);
                resourceImage.setPreserveRatio(true);
                resourceImage.setSmooth(true);
                resourceImage.setId(resCounter.toString());
                resourceImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if(selectedResourcesBooleans[Integer.parseInt(resourceImage.getId())]){
                            selectedResources.remove(resource);
                            selectedResourcesBooleans[Integer.parseInt(resourceImage.getId())]=false;
                            resourceImage.setEffect(null);
                            confirmSelectionButton.setDisable(true);
                        }else if (selectedResources.size()<quantity){
                            selectedResources.add(resource);
                            selectedResourcesBooleans[Integer.parseInt(resourceImage.getId())]=true;
                            greyNode(resourceImage);
                            if(selectedResources.size()==quantity){
                                confirmSelectionButton.setDisable(false);
                            }
                        }
                    }
                });
                resourceImage.setDisable(false);
                resourcesImages.add(resourceImage);
                resCounter.getAndIncrement();
            });
        }

        return resourcesImages;
    }

    /**
     * Function that handles the click of previously chosen as Confirm Button, used when the users needs to choose more than one
     * leader card or resource. It collapses the choice panel if button is clicked. The button is clickable only if the right amount of
     * items has been chosen. When clicked, it sends the selection to the server.
     * @param confirmSelectionButton button to be used as confirmation button of the selection
     * @param selectionHBox HBox containing elements (ImageViews) to be selected
     * @param label label on which show text messages
     * @param leaderCards chosen leader cards ( empty list if it's a resources selection)
     */
    private void handleConfirmSelectionButton(Button confirmSelectionButton, HBox selectionHBox, Label label, List<Integer> leaderCards){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(selectedLeaderCards.size()==2){
                    selectedLeaderCards.stream().forEach(card->leaderCards.remove(card));
                    client.sendMessageToServer(new ChooseLeaderCardsResponse(leaderCards));
                    selectedLeaderCards =new ArrayList<>();
                    selectionHBox.getChildren().clear();
                    confirmSelectionButton.setVisible(false);
                    confirmSelectionButton.setDisable(true);
                    label.setText("Waiting the other players, the game will start \nas soon as they all be ready...");
                    popupVbox.setVisible(false);
                    popupVbox.setPrefHeight(189);
                    popupVbox.setPrefWidth(341);
                }
                if(selectedLeaderCards.size()==0&&selectedResources.size()>0){
                    client.sendMessageToServer(new ChooseResourceTypeResponse(selectedResources));
                    selectionHBox.getChildren().clear();
                    confirmSelectionButton.setVisible(false);
                    confirmSelectionButton.setText("Discard");
                    reorganizeButton.setManaged(true);
                    label.setText("Waiting the other players, the game will start \nas soon as they all be ready...");
                }
            }
        });


    }

    // *********************************************************************  //
    //                          ACTIONS FUNCTIONS                             //
    // *********************************************************************  //

    /**
     * It glows markets arrows and makes them clickable, to choose where to put the marble from the slide
     */
    public void displayMarbleInsertionPositionRequest() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<Node> arrows=new ArrayList<>();
                ((Pane)((Pane) marketGrid.getParent()).getChildren().get(1)).getChildren().stream().forEach(node->arrows.add(node));
                ((Pane)((Pane) marketGrid.getParent()).getChildren().get(2)).getChildren().stream().forEach(node->arrows.add(node));
                for(Node arrow : arrows){
                    glowNode(arrow,colorToGlow);
                    arrow.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            client.sendMessageToServer(new MarbleInsertionPositionResponse(marketArrowsNumMap.get(arrow.getId())));
                            arrows.forEach(arrow->deactivateGlowingAndSelectEventHandler(arrow,false));
                        }
                    });
                    arrow.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            glowNode(arrow,colorToAlternateGlow);
                        }
                    });
                    arrow.setOnMouseExited(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            glowNode(arrow,colorToGlow);
                        }
                    });
                }
            }
        });
    }

    /**
     * It displays a pane containing a matrix of resources that player should choose cause he has two white marble
     * conversion leader cards.
     * @param resources resources to choose from
     * @param numberOfMarbles how many resources to choose (number of white marbles taken from market)
     */
    public void displayChooseWhiteMarbleConversionRequest(List<Resource> resources, int numberOfMarbles) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                popupVbox.setVisible(true);
                popupVbox.setManaged(true);
                reorganizeButton.setVisible(false);
                reorganizeButton.setManaged(false);
                Button confirmSelectionButton= discardButton;
                confirmSelectionButton.setDisable(true);
                confirmSelectionButton.setVisible(true);
                confirmSelectionButton.setText("Confirm");
                boolean[] selectedResourcesBooleans=new boolean[resources.size()*numberOfMarbles];
                GridPane gridResources= new GridPane();
                HBox selectionHBox=((HBox)popupVbox.getChildren().get(1));
                selectionHBox.getChildren().add(gridResources);
                selectedResources.clear();
                ((Label) popupVbox.getChildren().get(0)).setText(numberOfMarbles + " white marbles can be converted into resources");
                List<ImageView> resourcesImages= buildResources(numberOfMarbles,selectedResourcesBooleans,confirmSelectionButton, resources);
                int resCounter=0;
                for(int row=0;row<numberOfMarbles;row++){
                    for(int col=0;col<2;col++){
                        gridResources.add(resourcesImages.get(resCounter), col, row);
                        resCounter++;
                    }
                }
                gridResources.setVisible(true);
                confirmSelectionButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        if(selectedResources.size()>0){
                            List<Resource> resourcesToSend = new ArrayList<>();
                            for (int i = 0; i < selectedResources.size(); i++)
                                resourcesToSend.add(selectedResources.get(i));
                            client.sendMessageToServer(new ChooseWhiteMarbleConversionResponse(resourcesToSend));
                            selectionHBox.getChildren().clear();
                            confirmSelectionButton.setVisible(false);
                            confirmSelectionButton.setText("Discard");
                            reorganizeButton.setManaged(true);
                            popupVbox.setVisible(false);
                        }
                        actionEvent.consume();
                    }
                });
            }
        });
    }


    /**
     * Displays a panel to choose what type of reorganization the user wants to do (swap or move) and a button to end the
     * reorganization
     * @param depots depots that can be reorganized
     * @param failure true if reorganization was illegal
     * @param availableLeaderResource resources from leader cards depots
     */
    public void displayReorganizeDepotsRequest(List<String> depots, boolean failure, List<Resource> availableLeaderResource) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                (reorganizationVbox.getChildren().get(1)).setVisible(true);
                (reorganizationVbox.getChildren().get(1)).setManaged(true);
                reorganizeChosenDepots=new ArrayList<>();
                Label messageLabel=(Label)reorganizationVbox.getChildren().get(0);
                Button moveButton = (Button)((HBox)reorganizationVbox.getChildren().get(1)).getChildren().get(0);
                Button swapButton = (Button)((HBox)reorganizationVbox.getChildren().get(1)).getChildren().get(1);
                popupVbox.setVisible(false);
                reorganizationVbox.setVisible(true);
                if(failure){
                    messageLabel.setText("Invalid reorganization: check the capacity \nand the type of the depots before reorganizing");
                }
                else{
                    messageLabel.setText("Press a button");
                }
                moveButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        sourceAndTargetDepotsSelection(true,depots,availableLeaderResource,true);
                        (reorganizationVbox.getChildren().get(1)).setVisible(false);
                        (reorganizationVbox.getChildren().get(1)).setManaged(false);
                        messageLabel.setText("Select from which depot you want to move resources");
                    }
                });
                swapButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        sourceAndTargetDepotsSelection(false,depots,availableLeaderResource,true);
                        (reorganizationVbox.getChildren().get(1)).setVisible(false);
                        (reorganizationVbox.getChildren().get(1)).setManaged(false);
                        messageLabel.setText("Select the first depot to swap resources");
                    }
                });
            }
        });

    }

    /**
     * Handles the choice of the from and to depot for the resource swapping/moving
     * @param moveORswap true if it's a move reorganization,false if swap
     * @param depots depots that can be reorganized
     * @param availableLeaderResource resources from leader cards depots
     * @param firstChoice true if it's the first choice ( user is choosing from where to move resources)
     */
    private void sourceAndTargetDepotsSelection(boolean moveORswap, List<String> depots, List<Resource> availableLeaderResource, boolean firstChoice) {
        if(!firstChoice){
            if(moveORswap){
                ((Label)reorganizationVbox.getChildren().get(0)).setText("Select to which depot you want to move resources");
            }
            else {
                ((Label)reorganizationVbox.getChildren().get(0)).setText("Select the second depot to swap resources");
            }
        }
        for(String depot : depots){
            Node depotNode = storageNameToNodeMap.get(ResourceStorageType.valueOf(depot));
            if(ResourceStorageType.valueOf(depot).equals(ResourceStorageType.LEADER_DEPOT)){
                int counter=0;
                for(Integer lcID : matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards()) {
                    if(matchData.getLightClientByNickname(players.get(0)).leaderCardIsActive(lcID)){
                        if(counter==0)depotChoiceGlowAndClick(leftLeaderCard,moveORswap,depots,availableLeaderResource,firstChoice,true);
                        if(counter==1)depotChoiceGlowAndClick(rightLeaderCard,moveORswap,depots,availableLeaderResource,firstChoice,true);
                        if(counter==2) {
                            depotChoiceGlowAndClick(leftLeaderCard,moveORswap,depots,availableLeaderResource,firstChoice,true);
                            depotChoiceGlowAndClick(rightLeaderCard,moveORswap,depots,availableLeaderResource,firstChoice,true);
                        }
                        counter++;
                    }
                    counter++;
                }
            }
            else{
                depotChoiceGlowAndClick(depotNode,moveORswap,depots,availableLeaderResource,firstChoice,false);
            }
        }
    }

    /**
     * connects event handlers to given Node to make it selectable for the reorganization
     * @param depotNode Node of the depot
     * @param moveORswap true if it's a move reorganization,false if swap
     * @param depots depots that can be reorganized
     * @param availableLeaderResource resources from leader cards depots
     * @param firstChoice true if it's the first choice ( user is choosing from where to move resources)
     * @param leader true if the depot is a leader depot
     */
    private void depotChoiceGlowAndClick(Node depotNode, boolean moveORswap, List<String> depots, List<Resource> availableLeaderResource, boolean firstChoice, boolean leader) {
        glowNode(depotNode,colorToGlow);
        depotNode.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                createTooltip(depotNode,"CLICK TO SELECT");
            }
        });
        depotNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                glowNode(depotNode,colorToAlternateGlow);
                if(leader){
                    reorganizeChosenDepots.add(ResourceStorageType.LEADER_DEPOT);
                    depots.remove(ResourceStorageType.LEADER_DEPOT.toString());
                }
                else{
                    for(ResourceStorageType resourceStorageType: ResourceStorageType.values()){
                        if(storageNameToNodeMap.get(resourceStorageType).equals(depotNode)){
                            reorganizeChosenDepots.add(resourceStorageType);
                            depots.remove(resourceStorageType.toString());
                        }
                    }
                }
                depotNode.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                    }
                });
                depotNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                    }
                });
                if(firstChoice){
                    sourceAndTargetDepotsSelection(moveORswap,depots,availableLeaderResource,false);
                }
                else{
                    for(ResourceStorageType resourceStorageType: ResourceStorageType.values()){
                        if(resourceStorageType.equals(ResourceStorageType.LEADER_DEPOT)){
                            int counter=0;
                            for(Integer lcID : matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards()) {
                                if(matchData.getLightClientByNickname(players.get(0)).leaderCardIsActive(lcID)){
                                    Node lc= (counter==0?leftLeaderCard:rightLeaderCard);
                                    deactivateGlowingAndSelectEventHandler(lc,false);
                                    }
                                counter++;
                            }
                        }
                        else{
                            deactivateGlowingAndSelectEventHandler(storageNameToNodeMap.get(resourceStorageType),false);
                        }
                    }
                    if(moveORswap){
                        ((Label)reorganizationVbox.getChildren().get(0)).setText("Select how many resources you want to move");
                        ChoiceBox choiceBox= new ChoiceBox();
                        choiceBox.getItems().add("1");
                        choiceBox.getItems().add("2");
                        choiceBox.getItems().add("3");
                        choiceBox.setValue("1");
                        Button button= new Button();
                        button.setText("Confirm");
                        button.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                if(depotNode.equals(leftLeaderCard)) {
                                    client.sendMessageToServer(new MoveResourcesRequest(reorganizeChosenDepots.get(0).toString(), reorganizeChosenDepots.get(1).toString(), Resource.valueOf(matchData.getLeaderCardByID(matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards().get(0)).getEffectDescription().get(0)), Integer.parseInt((String) choiceBox.getValue())));
                                }
                                if(depotNode.equals(rightLeaderCard)) {
                                    client.sendMessageToServer(new MoveResourcesRequest(reorganizeChosenDepots.get(0).toString(), reorganizeChosenDepots.get(1).toString(), Resource.valueOf(matchData.getLeaderCardByID(matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards().get(1)).getEffectDescription().get(0)), Integer.parseInt((String) choiceBox.getValue())));
                                }
                                else {
                                    client.sendMessageToServer(new MoveResourcesRequest(reorganizeChosenDepots.get(0).toString(), reorganizeChosenDepots.get(1).toString(), Resource.ANY, Integer.parseInt((String) choiceBox.getValue())));
                                }
                                reorganizationVbox.getChildren().remove(choiceBox);
                                reorganizationVbox.getChildren().remove(button);
                            }
                        });
                        reorganizationVbox.getChildren().add(choiceBox);
                        reorganizationVbox.getChildren().add(button);
                    }
                    else{
                        client.sendMessageToServer(new SwapWarehouseDepotsRequest(reorganizeChosenDepots.get(0).toString(), reorganizeChosenDepots.get(1).toString()));
                    }
                }
            }
        });
    }

    /**
     * GLows and makes clickable given cards
     * @param cardIDs card ids
     * @param leaderORdevelopment true if it is a selection of leader cards, false if it is of development cards
     */
    public void displaySelectCardRequest(List<Integer> cardIDs, boolean leaderORdevelopment) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(leaderORdevelopment){
                    int counter=0;
                    for(Integer lcId: matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards()){
                        if(cardIDs.contains(lcId)){
                            if(counter==0) selectAndGlowCard(leftLeaderCard,lcId,leaderORdevelopment,cardIDs);
                            if(counter==1) selectAndGlowCard(rightLeaderCard,lcId,leaderORdevelopment,cardIDs);
                        }
                        counter++;
                    }
                }
                else{
                    for(Integer devCardToBuyId : matchData.getDevelopmentCardGrid()){
                        if(cardIDs.contains(devCardToBuyId)){
                            LightDevelopmentCard lightDevelopmentCard= matchData.getDevelopmentCardByID(devCardToBuyId);
                            int row = (Level.valueOf(lightDevelopmentCard.getFlagLevel()).getValue() * - 1) + 2;
                            int col = FlagColor.valueOf(lightDevelopmentCard.getFlagColor()).getValue();
                            selectAndGlowCard((ImageView) GameSceneController.this.developmentCardGrid.getChildren().get(col+ GameSceneController.this.developmentCardGrid.getColumnCount()*row),devCardToBuyId, leaderORdevelopment,cardIDs);
                        }
                    }
                }
            }
        });

    }

    /**
     * Sends to server the id of the selected cards and deactivates glowing and event handlers of all other cards
     * @param node node of the Card  selected
     * @param cardId id of the selected card
     * @param leaderORdevelopment true if it is a selection of leader cards, false if it is of development cards
     * @param allCardsIDs Ids of all the cards that could have been selected
     */
    private void selectAndGlowCard(Node node, Integer cardId, Boolean leaderORdevelopment, List<Integer> allCardsIDs) {
        glowNode(node,colorToGlow);
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                createTooltip(node,"SELECT CARD");
            }
        });
        node.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(leaderORdevelopment){
                    deactivateGlowingAndSelectEventHandler(leftLeaderCard,false);
                    deactivateGlowingAndSelectEventHandler(rightLeaderCard,false);
                }
                else{
                    for(Integer devCardToBuyId : matchData.getDevelopmentCardGrid()){
                        if(allCardsIDs.contains(devCardToBuyId)){
                            LightDevelopmentCard lightDevelopmentCard= matchData.getDevelopmentCardByID(devCardToBuyId);
                            int row = (Level.valueOf(lightDevelopmentCard.getFlagLevel()).getValue() * - 1) + 2;
                            int col = FlagColor.valueOf(lightDevelopmentCard.getFlagColor()).getValue();
                            deactivateGlowingAndSelectEventHandler((ImageView) GameSceneController.this.developmentCardGrid.getChildren().get(col+ GameSceneController.this.developmentCardGrid.getColumnCount()*row),false); }
                    }
                }
                client.sendMessageToServer(new SelectCardResponse(cardId));
            }
        });
    }

    /**
     * Makes personal board's production's slots selectable( adds glowing and click event handlers)
     * @param firstSlotAvailable true if first slot is available to be selected
     * @param secondSlotAvailable true if second slot is available to be selected
     * @param thirdSlotAvailable true if third slot is available to be selected
     */
    public void displaySelectDevelopmentCardSlotRequest(boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<Node> nodeList= new ArrayList<>();
                if(firstSlotAvailable){
                    nodeList.add(firstSlot);
                    glowNode(activateProductionPane.getChildren().get(1),colorToGlow);
                }
                if(secondSlotAvailable){
                    nodeList.add(secondSlot);
                    glowNode(activateProductionPane.getChildren().get(2),colorToGlow);
                }
                if(thirdSlotAvailable){
                    nodeList.add(thirdSlot);
                    glowNode(activateProductionPane.getChildren().get(3),colorToGlow);
                }
                for(Node node : nodeList){
                    node.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            createTooltip(node,"CLICK TO SELECT THIS SLOT");
                        }
                    });
                    node.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            activateProductionPane.getChildren().get(1).setEffect(null);
                            activateProductionPane.getChildren().get(2).setEffect(null);
                            activateProductionPane.getChildren().get(3).setEffect(null);
                            deactivateGlowingAndSelectEventHandler(firstSlot,false);
                            deactivateGlowingAndSelectEventHandler(secondSlot,false);
                            deactivateGlowingAndSelectEventHandler(thirdSlot,false);
                            if(node.equals(firstSlot)) client.sendMessageToServer(new SelectDevelopmentCardSlotResponse(0));
                            if(node.equals(secondSlot)) client.sendMessageToServer(new SelectDevelopmentCardSlotResponse(1));
                            if(node.equals(thirdSlot)) client.sendMessageToServer(new SelectDevelopmentCardSlotResponse(2));
                        }
                    });
                }
            }
        });
    }

    /**
     * Displays a panel indicating the resource to be removed and glows (and assigns event handlers) all depots from which
     * the user can choose to remove the resource
     * @param resource resource to be removed
     * @param isInWarehouse true if the resource can be removed from warehouse
     * @param isInStrongbox true if the resource can be removed from strongbox
     * @param isInLeaderDepot true if the resource can be removed from leader card's depots
     */
    public void displaySelectStorageRequest(Resource resource, boolean isInWarehouse, boolean isInStrongbox, boolean isInLeaderDepot) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (isInLeaderDepot ^ isInStrongbox ^ isInWarehouse){
                    if (isInWarehouse)
                        client.sendMessageToServer(new SelectStorageResponse(resource, ResourceStorageType.WAREHOUSE));
                    else if (isInStrongbox)
                        client.sendMessageToServer(new SelectStorageResponse(resource, ResourceStorageType.STRONGBOX));
                    else
                        client.sendMessageToServer(new SelectStorageResponse(resource, ResourceStorageType.LEADER_DEPOT));
                    return;
                }
                popupVbox.setManaged(true);
                popupVbox.setVisible(true);
                discardButton.setVisible(false);
                reorganizeButton.setVisible(false);
                ((Label)popupVbox.getChildren().get(0)).setText("Where do you want to take this resource from?");
                ImageView resourceImage= new ImageView(new Image(SetupSceneController.class.getResource("/img/punchboard/" + resource.toString().toLowerCase(Locale.ROOT) + ".png").toString()));
                resourceImage.setPreserveRatio(true);
                resourceImage.setFitHeight(((ImageView)warehouse_first_depot.getChildren().get(0)).getFitHeight());
                ((HBox)popupVbox.getChildren().get(1)).getChildren().add(resourceImage);
                Map<Node,ResourceStorageType> nodeAndStorageType= new HashMap<>();
                if(isInLeaderDepot){
                    int counter=0;
                    for(Integer lcId: matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards()){
                        if(matchData.getLightClientByNickname(players.get(0)).leaderCardIsActive(lcId)&&matchData.getLeaderCardByID(lcId).getEffectType().equals(EffectType.EXTRA_DEPOT.toString())&&matchData.getLeaderCardByID(lcId).getEffectDescription().get(0).equals(resource.toString())){
                            if(counter==0){
                                nodeAndStorageType.put(leftLeaderCard,ResourceStorageType.LEADER_DEPOT);
                                nodeAndStorageType.put(leftLeaderDepot,ResourceStorageType.LEADER_DEPOT);
                            }
                            if(counter==1) {
                                nodeAndStorageType.put(rightLeaderCard,ResourceStorageType.LEADER_DEPOT);
                                nodeAndStorageType.put(rightLeaderDepot,ResourceStorageType.LEADER_DEPOT);
                            }
                        }
                        counter++;
                    }
                }
                if(isInWarehouse){
                    nodeAndStorageType.put(warehouse_first_depot,ResourceStorageType.WAREHOUSE);
                    nodeAndStorageType.put(warehouse_second_depot,ResourceStorageType.WAREHOUSE);
                    nodeAndStorageType.put(warehouse_third_depot,ResourceStorageType.WAREHOUSE);
                    nodeAndStorageType.put(warehouse,ResourceStorageType.WAREHOUSE);
                }
                if(isInStrongbox){
                    nodeAndStorageType.put(((Pane)strongbox.getParent()).getChildren().get(1),ResourceStorageType.STRONGBOX);
                }
                for(Node node : nodeAndStorageType.keySet()){
                    glowNode(node,colorToGlow);
                    node.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            createTooltip(node,"CLICK TO CHOOSE THIS STORAGE");
                        }
                    });
                    node.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            for(Node node : nodeAndStorageType.keySet()){
                                deactivateGlowingAndSelectEventHandler(node,false);
                            }
                            ((HBox)popupVbox.getChildren().get(1)).getChildren().clear();
                            popupVbox.setManaged(false);
                            popupVbox.setVisible(false);
                            client.sendMessageToServer(new SelectStorageResponse(resource, nodeAndStorageType.get(node)));
                        }
                    });
                }
            }
        });
    }

    /**
     * GLows and connects event handlers to all production cards selectable
     * @param iDs IDs of the selectable cards
     * @param availableResources resources available for basic production
     * @param addORremove true if the player has to choose which production to do,false if the player has to choose which production to undo
     */
    public void displayChooseProduction(List<Integer> iDs, Map<Resource, Integer> availableResources, boolean addORremove) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                productionVbox.setVisible(true);
                productionVbox.getChildren().get(1).setVisible(false);
                productionVbox.getChildren().get(2).setVisible(false);
                productionVbox.getChildren().get(1).setManaged(false);
                productionVbox.getChildren().get(2).setManaged(false);
                ((Label) productionVbox.getChildren().get(0)).setText("Select a production");

                Map<Node,Integer> nodeIntMap= new HashMap<>();
                if(iDs.contains(0)){
                    nodeIntMap.put(basicProduction,0);
                }
                int i=0;
                for(Stack<Integer> stack:matchData.getLightClientByNickname(players.get(0)).getDevelopmentCardSlots()){
                    if(!stack.empty()){
                        if(iDs.contains(stack.peek())) nodeIntMap.put(activateProductionPane.getChildren().get(i+4),stack.peek());
                    }
                    i++;
                }
                int counter=0;
                for(LightLeaderCard lc : matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards().stream().map(x->matchData.getLeaderCardByID(x)).collect(Collectors.toList())){
                    if(matchData.getLightClientByNickname(players.get(0)).leaderCardIsActive(lc.getID())&&lc.getEffectType().equals(EffectType.PRODUCTION.toString())&&iDs.contains(lc.getID())) {
                        if(counter==0) nodeIntMap.put(leftLeaderCard, lc.getID());
                        if(counter==1) nodeIntMap.put(rightLeaderCard, lc.getID());
                    }
                    counter++;
                }
                for(Node node : nodeIntMap.keySet()){
                    if(addORremove)glowNode(node,colorToGlow);
                    node.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            createTooltip(node,"CLICK TO SELECT THIS PRODUCTION");
                        }
                    });
                    node.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            for(Node toRemoveGlowNode: nodeIntMap.keySet()){
                                toRemoveGlowNode.setOnMouseEntered(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent mouseEvent) {
                                    }
                                });
                                toRemoveGlowNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                    @Override
                                    public void handle(MouseEvent mouseEvent) {
                                    }
                                });
                            }
                            if(addORremove){
                                for(Node toRemoveGlowNode: nodeIntMap.keySet()){
                                    toRemoveGlowNode.setEffect(null);
                                }
                                glowNode(node,Color.BLUEVIOLET);
                                selectedProductions.add(node);
                                if(nodeIntMap.get(node)==0){
                                    createBasicProduction(availableResources);
                                }
                                else if(nodeIntMap.get(node)>=61){
                                    chooseLeaderCardProductionPower(availableResources,nodeIntMap.get(node));
                                }
                                else{
                                    UtilityProduction.addProductionPower(nodeIntMap.get(node));
                                }
                            }
                            else{
                                selectedProductions.remove(node);
                                node.setEffect(null);
                                if(nodeIntMap.get(node)==0){
                                    for(int i=1;i<4;i++){
                                        ((Pane)basicProduction.getParent()).getChildren().get(i).setVisible(false);
                                    }
                                }
                                if(nodeIntMap.get(node)>=61){
                                    int i=0;
                                    for(Integer lcId : matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards()){
                                        if(nodeIntMap.get(node).equals(lcId)) break;
                                        i++;
                                    }
                                    ((Pane)leftLeaderCard.getParent()).getChildren().get(6+i).setVisible(false);
                                }
                                UtilityProduction.removeProduction(nodeIntMap.get(node));
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Displays a panel for the user to choose the personalized output resource of his leader card production
     * @param availableResources Resources available to be producted
     * @param id id of the leader card
     */
    private void chooseLeaderCardProductionPower(Map<Resource, Integer> availableResources, Integer id) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                HBox resourcesHBox = (HBox) productionVbox.getChildren().get(1);
                resourcesHBox.setVisible(true);
                resourcesHBox.setManaged(true);
                resourcesHBox.getChildren().clear();
                ((Label) productionVbox.getChildren().get(0)).setText("Select the leader's production's output.");
                for(Resource resource : Resource.realValues()){
                    ImageView resourceImage = new ImageView(new Image(SetupSceneController.class.getResource("/img/punchboard/" + resource.toString().toLowerCase(Locale.ROOT) + ".png").toString()));
                    resourceImage.setPreserveRatio(true);
                    resourceImage.setFitHeight(((ImageView)warehouse_first_depot.getChildren().get(0)).getFitHeight());
                    resourceImage.setId(resource.toString());
                    resourceImage.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            glowNode(resourceImage,colorToGlow);
                        }
                    });
                    resourceImage.setOnMouseExited(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            resourceImage.setEffect(null);
                        }
                    });
                    resourceImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            int i=0;
                            for(Integer lcId : matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards()){
                                if(id.equals(lcId)){
                                    break;
                                }
                                i++;
                            }
                            ((ImageView)((Pane)leftLeaderCard.getParent()).getChildren().get(6+i)).setImage((resourceImage).getImage());
                            ((Pane)leftLeaderCard.getParent()).getChildren().get(6+i).setVisible(true);
                            UtilityProduction.manageLeaderProductionPower(resource,id);
                            resourcesHBox.getChildren().clear();
                        }
                    });
                    resourcesHBox.getChildren().add(resourceImage);
                }
            }
        });
    }

    /**
     * Displays a panel for the user to choose the personalized input and output resources of his basic production
     * @param availableResources Resources available to be taken and producted
     */
    private void createBasicProduction(Map<Resource, Integer> availableResources){
        HBox resourcesHBox = (HBox) productionVbox.getChildren().get(1);
        resourcesHBox.setVisible(true);
        resourcesHBox.setManaged(true);
        resourcesHBox.getChildren().clear();
        ((Label) productionVbox.getChildren().get(0)).setText("Select the first basic production input.");
        List<Resource> usableResources = new ArrayList<Resource>();
        List<Resource> chosenResources = new ArrayList<Resource>();
        //Saving in usableResources which Resource has a quantity > 0
        for(Map.Entry<Resource, Integer> entry : availableResources.entrySet()){
            if(entry.getValue() > 0){
                usableResources.add(entry.getKey());
            }
        }
        if(usableResources.size() > 0){
            for(Resource resource : Resource.realValues()){
                ImageView resourceImage = new ImageView(new Image(SetupSceneController.class.getResource("/img/punchboard/" + resource.toString().toLowerCase(Locale.ROOT) + ".png").toString()));
                resourceImage.setPreserveRatio(true);
                resourceImage.setFitHeight(((ImageView)warehouse_first_depot.getChildren().get(0)).getFitHeight());
                resourceImage.setId(resource.toString());
                if(!usableResources.contains(resource)){
                    resourceImage.setVisible(false);
                    resourceImage.setManaged(false);
                }
                resourcesHBox.getChildren().add(resourceImage);
            }
            for(Node node : resourcesHBox.getChildren()){
                node.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        glowNode(node,colorToGlow);
                    }
                });
                node.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        node.setEffect(null);
                    }
                });
                node.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        ((ImageView)((Pane)basicProduction.getParent()).getChildren().get(1+chosenResources.size())).setImage(((ImageView) node).getImage());
                        ((ImageView)((Pane)basicProduction.getParent()).getChildren().get(1+chosenResources.size())).setVisible(true);
                        chosenResources.add(Resource.valueOf(node.getId().toUpperCase()));
                        if(chosenResources.size()==3){
                            UtilityProduction.manageBasicProductionPower(chosenResources);
                            resourcesHBox.getChildren().clear();
                        }
                        if(chosenResources.size()==2){
                            for(Node node : resourcesHBox.getChildren()){
                                node.setVisible(true);
                                node.setManaged(true);
                            }
                            ((Label) productionVbox.getChildren().get(0)).setText("Select the basic production output");
                        }
                        else{
                            ((Label) productionVbox.getChildren().get(0)).setText("Select the second basic production input.");
                            if(availableResources.get(Resource.valueOf(node.getId().toUpperCase()))==1){
                                node.setVisible(false);
                                node.setManaged(false);
                            }
                        }
                    }
                });
            }
        }

    }


    /**
     * Shows a message if no cards are selectable
     * @param iDs IDs of the development cards selectable
     */
    public void displayProductionCardYouCanSelect(List<Integer> iDs) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(iDs.size()==0){
                    productionVbox.setVisible(true);
                    productionVbox.getChildren().get(1).setVisible(false);
                    productionVbox.getChildren().get(2).setVisible(false);
                    productionVbox.getChildren().get(1).setManaged(false);
                    productionVbox.getChildren().get(2).setManaged(false);
                    ((Label) productionVbox.getChildren().get(0)).setText("You can't choose any other production at the moment. ");
                }
            }
        });

    }

    /**
     * Displays a panel with buttons to choose if adding another production, remove one or confirming current chosen productions
     */
    public void chooseNextProductionAction() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                productionVbox.setVisible(true);
                productionVbox.getChildren().get(1).setVisible(false);
                productionVbox.getChildren().get(2).setVisible(true);
                productionVbox.getChildren().get(1).setManaged(false);
                productionVbox.getChildren().get(2).setManaged(true);
                ((Label) productionVbox.getChildren().get(0)).setText("What do you want to do?");
            }

        });
    }

    // *********************************************************************  //
    //                      SINGLE PLAYER FUNCTIONS                           //
    // *********************************************************************  //


    /**
     * Displays a panel showing Lorenzo's used token
     * @param id id of lorenzo's action's token
     */
    public void displayLorenzoAction(int id) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((ImageView)lorenzoVbox.getChildren().get(1)).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/" + id + ".png").toString()));
                lorenzoVbox.setVisible(true);
            }
        });
    }

    // *********************************************************************  //
    //                                END GAME                                //
    // *********************************************************************  //

    /**
     * Displays a panel containing End Game results (names, points, winner/winners)
     * @param results Map of usernames->victory points
     * @param readyForAnotherGame true if the user can choose to start another game
     */
    public void displayResults(Map<String, Integer> results, boolean readyForAnotherGame) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                leftPane.setDisable(true);
                rightPane.setDisable(true);
                importantMessagesVbox.setManaged(true);
                importantMessagesVbox.setVisible(true);
                importantMessagesVbox.getChildren().get(1).setVisible(false);
                List<String> winners= new ArrayList<>();
                Label endMessage= ((Label) importantMessagesVbox.getChildren().get(0));
                int i = 1;
                if (results.size() == 1){
                    int points = -1;
                    for (String name : results.keySet())
                        points = results.get(name);
                    displayResults(points);
                }
                else {
                    endMessage.setText("");
                    int max=0;
                    for (String name : results.keySet()) {
                        endMessage.setText(endMessage.getText() + (results.keySet().size() > 1 ? (i++ + ". ") : "") + name + ": " + results.get(name) + " victory points\n" );
                        if(max==results.get(name)){
                            winners.add(name);
                        }
                        if(max<results.get(name)){
                            max=results.get(name);
                            winners.clear();
                            winners.add(name);
                        }
                    }
                    endMessage.setText(endMessage.getText() + (winners.size()>1? "WINNERS: ":"WINNER : ") );
                    for(String winner:winners){
                        endMessage.setText(endMessage.getText() + "!! "+ winner + " !!");
                    }
                }
                if (!readyForAnotherGame){
                    connectionClosedByClient=true;
                    client.closeSocket();
                }
                else
                    endMessage.setText(endMessage.getText() + "\n\nYou can now start another game!\n");
            }
        });
    }

    /**
     * Displays a panel containing End Game results for single player ( if you won/lost against Lorenzo Il Magnifico and
     * how many point's he has made)
     * @param victoryPoints player victory points
     */
    public void displayResults(int  victoryPoints) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                leftPane.setDisable(true);
                rightPane.setDisable(true);
                importantMessagesVbox.setManaged(true);
                importantMessagesVbox.setVisible(true);
                importantMessagesVbox.getChildren().get(1).setVisible(false);
                Label endMessage= ((Label) importantMessagesVbox.getChildren().get(0));
                if (victoryPoints == -1)
                    endMessage.setText("You lost against Lorenzo il Magnifico!");
                else
                    endMessage.setText("You won with " + victoryPoints + " victory points!! \nCongratulations");
                connectionClosedByClient=true;
                client.closeSocket();
            }
        });

    }

    // *********************************************************************  //
    //                      RESILIENCE TO DISCONNECTIONS                      //
    // *********************************************************************  //

    /**
     * Displays a panel containing a welcome back message for user who got disconnected
     * @param nickname user nickname
     * @param gameFinished true if game finished while user was disconnected
     */
    public void displayWelcomeBackMessage(String nickname, boolean gameFinished) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                importantMessagesVbox.setManaged(true);
                importantMessagesVbox.setVisible(true);
                importantMessagesVbox.getChildren().get(1).setVisible(true);
                Label message= (Label) importantMessagesVbox.getChildren().get(0);
                message.setText("Welcome back " + nickname + (gameFinished ? "!\nThe game you were playing in is finished,\n we are loading the results for you..." : ".\nYou have to finish an old game,\n we are logging you in the room..."));
            }
        });
    }

    /**
     * Displays a panel  message notifying that a user got disconnected
     * @param nickname user disconnected nickname
     * @param setUp true if game was in setup phase
     * @param gameCancelled true if game has been cancelled
     */
    public void displayDisconnection(String nickname, boolean setUp, boolean gameCancelled) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                importantMessagesVbox.setManaged(true);
                importantMessagesVbox.setVisible(true);
                importantMessagesVbox.getChildren().get(1).setVisible(!gameCancelled);
                Label message= (Label) importantMessagesVbox.getChildren().get(0);
                message.setText("We are sorry to inform you that " + nickname + " has left the game.\n The game" + (gameCancelled? " has been cancelled." : " will go on skipping the turns of that player.") + (gameCancelled? "\nYou have been reconnected to the main lobby...\nBe ready to start another game. \nA game will start as soon as enough players will be ready\n" : "") );
                if(gameCancelled){
                    leftPane.setDisable(true);
                    rightPane.setDisable(true);
                }
            }
        });

    }

    /**
     * Displays a panel  message notifying that server got disconnected. Contains a quit button to close the game
     * @param wasConnected true if the client was connected to server when server disconnected
     */
    public void handleCloseConnection(boolean wasConnected) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                importantMessagesVbox.setManaged(true);
                importantMessagesVbox.setVisible(true);
                if(!connectionClosedByClient){
                    if (!wasConnected)
                        ((Label)importantMessagesVbox.getChildren().get(0)).setText("The server is not reachable at the moment. Try again later.");
                    else
                        ((Label)importantMessagesVbox.getChildren().get(0)).setText("Server disconnected. Connection closed.");
                }
                importantMessagesVbox.getChildren().get(1).setVisible(true);
                ((Button)importantMessagesVbox.getChildren().get(1)).setText("Quit");
                ((Button)importantMessagesVbox.getChildren().get(1)).setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        Platform.exit();
                        System.exit(0);
                    }
                });
            }
        });
    }

    /**
     *  Displays a panel  message notifying that you have got disconnected for inactivity.Buttons is showed to choose if you
     *  want to reconnect (yes/no)
     */
    public void handleTimeoutExpired() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                importantMessagesVbox.setManaged(true);
                importantMessagesVbox.setVisible(true);
                ((Label) importantMessagesVbox.getChildren().get(0)).setText("Timeout expired, do you want to reconnect?");
                importantMessagesVbox.getChildren().get(1).setVisible(true);
                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER);
                hbox.setSpacing(10.0);
                importantMessagesVbox.getChildren().remove(1);
                Button noButton = new Button("No");
                noButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        client.closeSocket();
                        Platform.exit();
                        System.exit(0);
                    }
                });
                Button yesButton = new Button("Yes");
                yesButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        try {
                            client.killThreads();
                            client = new Client(client.getIPAddress(), client.getPort(), gui, client.getGameMode(), client.isNicknameValid() ? client.getNickname() : Optional.empty());
                            gui.setClient(client);
                            client.start();
                        } catch (IOException e) {

                        }
                    }
                });
                hbox.getChildren().add(noButton);
                hbox.getChildren().add(yesButton);
                importantMessagesVbox.getChildren().add(hbox);
            }
        });
    }
}
