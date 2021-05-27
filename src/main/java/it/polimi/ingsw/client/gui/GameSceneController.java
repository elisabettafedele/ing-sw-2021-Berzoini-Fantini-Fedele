package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.PopesTileState;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.messages.toServer.game.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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

import java.util.*;
import java.util.List;
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
    private VBox reorganizationVbox;
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


    List<String> players;
    int currentPlayerIndex;

    MatchData matchData;

    Map<ActionType,Boolean> executableActions;
    Map<ResourceStorageType,Node> storageNameToNodeMap;
    Map<String,Integer> marketArrowsNumMap;
    List<Resource> selectedResources;
    List<Integer> selectedLeaderCards;
    List<ResourceStorageType> reorganizeChosenDepots;

    boolean isYourTurn;
    // *********************************************************************  //
    //                        INITIALIZING FUNCTIONS                          //
    // *********************************************************************  //
    @FXML
    public void initialize() {
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
        storageNameToNodeMap.put(ResourceStorageType.LEADER_DEPOT,leftLeaderDepot.getParent());
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
        endTurnButton.setVisible(false);
        endTurnButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                endTurnButton.setVisible(false);
                mainLabelMessage.setText("WAIT YOUR TURN");
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
    }

    public void setGUI(GUI gui) {
        this.gui=gui;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    // *********************************************************************  //
    //                           UTILITY FUNCTIONS                            //
    // *********************************************************************  //

    private void createTooltip(Node node, String message){
        Tooltip tooltip = new Tooltip(message);
        tooltip.setShowDelay(Duration.seconds(1));
        tooltip.setHideDelay(Duration.seconds(0.5));
        Tooltip.install(node,tooltip);
    }

    private void glowNode(Node nodeToGlow,Color color){
        DropShadow borderGlow = new DropShadow();
        borderGlow.setColor(color);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        nodeToGlow.setEffect(borderGlow);
    }
    private void greyNode(Node nodeToGrey){
        ColorAdjust colorAdjust=new ColorAdjust();
        colorAdjust.setBrightness(0.4);
        nodeToGrey.setEffect(colorAdjust);
    }



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
    public void disableNextPreviousButtons() {
        greyNode(nextPlayerButton);
        nextPlayerButton.setDisable(true);
        greyNode(previousPlayerButton);
        previousPlayerButton.setDisable(true);
    }

    public void enableNextPreviousButtons() {
        nextPlayerButton.setEffect(null);
        nextPlayerButton.setDisable(false);
        previousPlayerButton.setEffect(null);
        previousPlayerButton.setDisable(false);
    }

    // *********************************************************************  //
    //                        UPDATING VIEW FUNCTIONS                         //
    // *********************************************************************  //

    public void updateView() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateDevelopmentCardGridView();
                updateMarketView();
                updateMainLabel();
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
        });

    }

    private void updateMainLabel() {
        mainLabelNames.setText("");
        mainLabelStats.setText("");
        mainLabelMessage.setFont(new Font(mainLabelNames.getFont().toString(),8));
        mainLabelStats.setFont(new Font(mainLabelNames.getFont().toString(),8));
        mainLabelNames.setFont(new Font(mainLabelNames.getFont().toString(),8));
        for(int i=0;i<players.size();i++){
            mainLabelNames.setText(mainLabelNames.getText() + players.get(i) + "\n");
            mainLabelStats.setText(mainLabelStats.getText() + "FT: " + matchData.getLightClientByNickname(players.get(i)).getFaithTrackPosition() + " VP: " + matchData.getLightClientByNickname(players.get(i)).getVictoryPoints() + "\n");
        }
    }

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

    private void updateFaithTrack() {
        for(Node ftBox : faithtrack.getChildren()){
            ftBox.setVisible(false);
        }
        int playerPos=matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getFaithTrackPosition();
        if(playerPos>24){
            playerPos=24;
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
                    leftLeaderCard.setEffect(null);
                    leftLeaderCard.setVisible(true);
                }
                if(i==1){
                    rightLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/front/" + leaderCards.get(i).getID() + ".png").toString()));
                    rightLeaderCard.setEffect(null);
                    rightLeaderCard.setVisible(true);
                }
                //if extra depot leaderCard, it shows the resources in it, if any
                if(leaderCards.get(i).getEffectType().equals("EXTRA_DEPOT")){
                    int quantity= matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getLeaderDepots().get(leaderCards.get(i));
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

    private void updateMarketView() {
        for(int row=0;row< 3 ;row++ ){
            for(int col=0; col< 4; col++){
                ((ImageView) marketGrid.getChildren().get(col+row*4)).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/marble_" + matchData.getMarketTray()[row][col].toString().toLowerCase() + ".png").toString()));
            }
        }
        slideMarble.setImage(new Image(GameSceneController.class.getResource("/img/punchboard/marble_" + matchData.getSlideMarble().toString().toLowerCase() + ".png").toString()));
    }

    private void updateDevelopmentCardGridView() {
        List<LightDevelopmentCard> developmentCardGrid =new ArrayList<>();
        for(Integer devCardId : matchData.getDevelopmentCardGrid()){
            developmentCardGrid.add(matchData.getDevelopmentCardByID(devCardId));
        }
        for(LightDevelopmentCard devCard : developmentCardGrid){
            int row = (Level.valueOf(devCard.getFlagLevel()).getValue() * - 1) + 2;
            int col = FlagColor.valueOf(devCard.getFlagColor()).getValue();
            ((ImageView) this.developmentCardGrid.getChildren().get(col+ this.developmentCardGrid.getColumnCount()*row)).setImage(new Image(GameSceneController.class.getResource("/img/Cards/DevelopmentCards/front/" + devCard.getID() + ".png").toString()));

        }
    }

    // *********************************************************************  //
    //                   TURN (SELECT ACTION) FUNCTIONS                       //
    // *********************************************************************  //

    public void displayChooseActionRequest(Map<ActionType, Boolean> executableActions, boolean standardActionDone) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                GameSceneController.this.executableActions =executableActions;
                isYourTurn=true;
                mainLabelMessage.setText("IT'S YOUR TURN!");
                endTurnButton.setVisible(standardActionDone);
                if(executableActions.keySet().isEmpty()){
                    endTurnButton.setVisible(true);
                }
                updateGlowingObjects();
            }
        });
    }

    private void updateGlowingObjects() {
        deactivateAllActionsGlowing();
        if(currentPlayerIndex==0&&isYourTurn){
            List<LightLeaderCard> leaderCards =new ArrayList<>();
            for(Integer lcID :matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getOwnedLeaderCards()){
                leaderCards.add(matchData.getLeaderCardByID(lcID));
            }
            for(ActionType actionType : executableActions.keySet()){
                if(executableActions.get(actionType)==true){
                    switch (actionType){
                        case TAKE_RESOURCE_FROM_MARKET:
                            activateGlowingAndSelectEventHandler(marketGrid,false,actionType);
                            break;
                        case ACTIVATE_PRODUCTION:
                            activateGlowingAndSelectEventHandler(activateProductionPane,false,actionType);
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

    private void deactivateGlowingAndSelectEventHandler(Node nodeToDeactivate, boolean backToGreyLeaderCard){
        nodeToDeactivate.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //createTooltip(nodeToDeactivate,null);
                mouseEvent.consume();
            }
        });
        nodeToDeactivate.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseEvent.consume();
            }
        });
        nodeToDeactivate.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseEvent.consume();
            }
        });
        nodeToDeactivate.setEffect(null);
        if(backToGreyLeaderCard){
            greyNode(nodeToDeactivate);
        }
    }



    private void activateGlowingAndSelectEventHandler(Node nodeToActivate, boolean backToGreyLeaderCard, ActionType actionType){
        nodeToActivate.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                glowNode(nodeToActivate,Color.CYAN);
                if(actionType.equals(ActionType.ACTIVATE_LEADER_CARD)){
                    createTooltip(nodeToActivate,actionType.toString().replace('_',' ') + "OR" + ActionType.DISCARD_LEADER_CARD.toString().replace('_',' ') );
                }
                else{
                    createTooltip(nodeToActivate,actionType.toString().replace('_',' '));
                }
                mouseEvent.consume();
            }
        });
        nodeToActivate.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                nodeToActivate.setEffect(null);
                if(backToGreyLeaderCard){
                    greyNode(nodeToActivate);
                    mouseEvent.consume();
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
                            actionEvent.consume();
                        }
                    });
                    discardLeaderCardButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent actionEvent) {
                            selectAction(ActionType.DISCARD_LEADER_CARD.toString());
                            activateLeaderCardButton.setVisible(false);
                            discardLeaderCardButton.setVisible(false);
                            actionEvent.consume();
                        }
                    });
                }
                else{
                      selectAction(actionType.toString());
                }
                deactivateAllActionsGlowing();
                mouseEvent.consume();
            }
        });
    }

    private void selectAction(String selectedAction) {
        disableNextPreviousButtons();
        client.sendMessageToServer(new ChooseActionResponse(ActionType.valueOf(selectedAction).getValue()));
    }


    // *********************************************************************  //
    //                             RESOURCE INSERTION                         //
    // *********************************************************************  //
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

    public void displayChooseStorageTypeRequest(Resource resource, HashMap<ResourceStorageType, Boolean> interactableDepots, boolean canDiscard, boolean canReorganize) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainLabelMessage.setText("IT'S YOUR TURN!");
                popupVbox.setVisible(true);
                discardButton.setVisible(canDiscard);
                discardButton.setManaged(canDiscard);
                discardButton.setDisable(!canDiscard);
                reorganizeButton.setVisible(canReorganize);
                reorganizeButton.setManaged(canReorganize);
                reorganizeButton.setDisable(!canReorganize);

                popupVbox.setVisible(true);
                ((Label) popupVbox.getChildren().get(0)).setText("Drag the resource into one of the glowing depots, if any");
                HBox resourcesHBox=(HBox) popupVbox.getChildren().get(1);
                highlightAndDrag(resourcesHBox.getChildren().get(0),resource.toString(), interactableDepots);
                discardButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        ((HBox) popupVbox.getChildren().get(1)).getChildren().remove(0);
                        resetStorageInsertion();
                        popupVbox.setVisible(false);
                        popupVbox.setManaged(false);
                        mainLabelMessage.setText("WAIT YOUR TURN");
                        client.sendMessageToServer(new DiscardResourceRequest(resource));
                    }
                });
                reorganizeButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        resetStorageInsertion();
                        popupVbox.setVisible(false);
                        popupVbox.setManaged(false);
                        mainLabelMessage.setText("WAIT YOUR TURN");
                        client.sendMessageToServer(new ReorganizeDepotRequest());
                    }
                });
            }
        });
    }

    private void highlightAndDrag(Node resourceToDrag, String resourceAsString, HashMap<ResourceStorageType, Boolean> interactableDepots) {
        glowNode(resourceToDrag,Color.CYAN);
        makeDraggable(resourceToDrag, resourceAsString);
        for(ResourceStorageType resourceStorageType: interactableDepots.keySet()){
            if(interactableDepots.get(resourceStorageType)){
                if (resourceStorageType.equals(ResourceStorageType.LEADER_DEPOT)){
                    int lcCounter=0;
                    for(LightLeaderCard lc: matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards().stream().map(x -> matchData.getLeaderCardByID(x)).collect(Collectors.toList())){
                        if(lc.getEffectType().equals("EXTRA_DEPOT")){
                            if(lcCounter == 0) {
                                glowNode(leftLeaderCard,Color.CYAN);
                                activateDraggableOver(leftLeaderDepot);
                            }
                            if(lcCounter==1){
                                glowNode(rightLeaderCard,Color.CYAN);
                                activateDraggableOver(rightLeaderDepot);
                            }
                        }
                        lcCounter++;
                    }
                }
                else {
                    if(resourceStorageType.equals(ResourceStorageType.WAREHOUSE)){
                        glowNode(storageNameToNodeMap.get(ResourceStorageType.WAREHOUSE_FIRST_DEPOT),Color.CYAN);
                        glowNode(storageNameToNodeMap.get(ResourceStorageType.WAREHOUSE_SECOND_DEPOT),Color.CYAN);
                        glowNode(storageNameToNodeMap.get(ResourceStorageType.WAREHOUSE_THIRD_DEPOT),Color.CYAN);
                    }
                    else{
                        glowNode(storageNameToNodeMap.get(resourceStorageType),Color.CYAN);
                    }
                    activateDraggableOver(storageNameToNodeMap.get(resourceStorageType));
                }

            }
        }
    }

    private void resetStorageInsertion() {
        for(ResourceStorageType resourceStorageType : storageNameToNodeMap.keySet()){
            if(resourceStorageType.equals(ResourceStorageType.LEADER_DEPOT)){
                deactivateDraggableOver(leftLeaderDepot);
                deactivateDraggableOver(rightLeaderDepot);
                if(leftLeaderCard.getEffect() instanceof DropShadow){
                    rightLeaderCard.setEffect(null);
                }
                if(rightLeaderCard.getEffect() instanceof DropShadow){
                    leftLeaderCard.setEffect(null);
                }
            }
            else{
                deactivateDraggableOver(storageNameToNodeMap.get(resourceStorageType));
                storageNameToNodeMap.get(resourceStorageType).setEffect(null);
            }
        }
    }

    private void makeDraggable(Node resourceToDrag, String resourceAsString) {
        resourceToDrag.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Dragboard db = resourceToDrag.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(resourceAsString);
                db.setContent(content);
                event.consume();
            }
        });
        resourceToDrag.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getTransferMode() == TransferMode.MOVE) {
                    ((HBox) resourceToDrag.getParent()).getChildren().remove(0);
                }

                event.consume();
            }
        });
    }

    private void activateDraggableOver(Node nodeDraggableOver) {
        nodeDraggableOver.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != nodeDraggableOver &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            }
        });
        nodeDraggableOver.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != nodeDraggableOver &&
                        event.getDragboard().hasString()) {
                    glowNode(nodeDraggableOver,Color.ORANGE);
                }
                event.consume();
            }
        });
        nodeDraggableOver.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                glowNode(nodeDraggableOver,Color.CYAN);
                event.consume();
            }
        });
        nodeDraggableOver.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (event.getDragboard().hasString()) {
                    addResource(nodeDraggableOver,db.getString());
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    private void deactivateDraggableOver(Node nodeDraggableOver) {
        nodeDraggableOver.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.consume();
            }
        });
        nodeDraggableOver.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.consume();
            }
        });
        nodeDraggableOver.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.consume();
            }
        });
        nodeDraggableOver.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                event.consume();
            }
        });
    }

    private void addResource(Node nodeDraggableOver, String resourceToAddAsString) {
        String storageSelected= new String();
        for(ResourceStorageType rst: storageNameToNodeMap.keySet()){
            if(storageNameToNodeMap.get(rst).equals(nodeDraggableOver)){
                storageSelected=rst.toString();
            }
        }
        resetStorageInsertion();
        ((Label)popupVbox.getChildren().get(0)).setText("Waiting the other players, the game will start \nas soon as they all be ready...");
        client.sendMessageToServer(new ChooseStorageTypeResponse(Resource.valueOf(resourceToAddAsString),storageSelected,discardButton.isVisible(),reorganizeButton.isVisible()));
        popupVbox.setVisible(false);
        popupVbox.setManaged(false);
        mainLabelMessage.setText("WAIT YOUR TURN");
    }


    // *********************************************************************  //
    //                        INITIAL GAME FUNCTIONS                          //
    // *********************************************************************  //

    public void displayLeaderCardsRequest(List<Integer> leaderCards, Client client) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                popupVbox.setVisible(true);
                //set Reorganize button invisible
                reorganizeButton.setVisible(false);
                reorganizeButton.setManaged(false);
                //turn DiscardButton into ConfirmSelectionButton
                Button confirmSelectionButton=discardButton;
                confirmSelectionButton.setText("Confirm selection");
                confirmSelectionButton.setDisable(true);
                confirmSelectionButton.setVisible(true);

                HBox selectionHBox=((HBox)popupVbox.getChildren().get(1));
                ((Label) popupVbox.getChildren().get(0)).setText("Choose two out of the four following Leader cards:");
                selectedResources=new ArrayList<>();
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
                List<ImageView> resourcesImages= buildResources(quantity,selectedResourcesBooleans,confirmSelectionButton);
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

    private List<ImageView> buildResources(int quantity, boolean[] selectedResourcesBooleans, Button confirmSelectionButton) {
        List<Resource> resources= Resource.realValues();
        List<ImageView> resourcesImages=new ArrayList<>();
        AtomicInteger resCounter= new AtomicInteger();
        for(int ii=0; ii<quantity;ii++){
            resources.forEach(resource->{
                ImageView resourceImage= new ImageView( new Image(SetupSceneController.class.getResource("/img/punchboard/" + resource.toString().toLowerCase(Locale.ROOT) + ".png").toString()));
                resourceImage.setFitHeight(40);
                resourceImage.setPreserveRatio(true);
                resourceImage.setSmooth(true);
                resourceImage.setId(resCounter.toString());
                resourceImage.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {

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
                });
                resourcesImages.add(resourceImage);
                resCounter.getAndIncrement();
            });
        }

        return resourcesImages;
    }


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
                    mainLabelMessage.setText("WAIT YOUR TURN");
                    label.setText("Waiting the other players, the game will start \nas soon as they all be ready...");
                    popupVbox.setVisible(false);
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

    public void displayMarbleInsertionPositionRequest() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //TODO handle white marbles conversion
                List<Node> arrows=new ArrayList<>();
                ((Pane)((Pane) marketGrid.getParent()).getChildren().get(1)).getChildren().stream().forEach(node->arrows.add(node));
                ((Pane)((Pane) marketGrid.getParent()).getChildren().get(2)).getChildren().stream().forEach(node->arrows.add(node));;
                for(Node arrow : arrows){
                    glowNode(arrow,Color.CYAN);
                    arrow.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            client.sendMessageToServer(new MarbleInsertionPositionResponse(marketArrowsNumMap.get(arrow.getId())));
                            arrows.forEach(arrow->deactivateGlowingAndSelectEventHandler(arrow,false));
                            mouseEvent.consume();
                        }
                    });
                    arrow.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            glowNode(arrow,Color.CORAL);
                            mouseEvent.consume();
                        }
                    });
                    arrow.setOnMouseExited(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            glowNode(arrow,Color.CYAN);
                            mouseEvent.consume();
                        }
                    });
                }
            }
        });
    }

    public void displayReorganizeDepotsRequest(List<String> depots, boolean failure, List<Resource> availableLeaderResource) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ((HBox)reorganizationVbox.getChildren().get(1)).setVisible(true);
                ((HBox)reorganizationVbox.getChildren().get(1)).setManaged(true);
                reorganizeChosenDepots=new ArrayList<>();
                Label messageLabel=(Label)reorganizationVbox.getChildren().get(0);
                Button moveButton = (Button)((HBox)reorganizationVbox.getChildren().get(1)).getChildren().get(0);
                Button swapButton = (Button)((HBox)reorganizationVbox.getChildren().get(1)).getChildren().get(1);
                mainLabelMessage.setText("IT'S YOUR TURN!");//TODO change it to wait your turn only when turn actually ends(do it after "who's turn" message implementation
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
                        ((HBox)reorganizationVbox.getChildren().get(1)).setVisible(false);
                        ((HBox)reorganizationVbox.getChildren().get(1)).setManaged(false);
                        messageLabel.setText("Select from which depot you want to move resources");
                    }
                });
                swapButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        sourceAndTargetDepotsSelection(false,depots,availableLeaderResource,true);
                        ((HBox)reorganizationVbox.getChildren().get(1)).setVisible(false);
                        ((HBox)reorganizationVbox.getChildren().get(1)).setManaged(false);
                        messageLabel.setText("Select the first depot to swap resources");
                    }
                });
            }
        });

    }

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

    private void depotChoiceGlowAndClick(Node depotNode, boolean moveORswap, List<String> depots, List<Resource> availableLeaderResource, boolean firstChoice, boolean leader) {
        glowNode(depotNode,Color.CYAN);
        depotNode.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                createTooltip(depotNode,"CLICK TO SELECT");
                mouseEvent.consume();
            }
        });
        depotNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                glowNode(depotNode,Color.CORAL);
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
                        mouseEvent.consume();
                    }
                });
                depotNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        mouseEvent.consume();
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

    public void displaySelectCardRequest(List<Integer> cardIDs, boolean leaderORdevelopment) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(leaderORdevelopment){
                    int counter=0;
                    for(Integer lcId: matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards()){
                        if(cardIDs.contains(lcId)){
                            if(counter==0) selectAndGlowCard(leftLeaderCard,lcId);
                            if(counter==1) selectAndGlowCard(rightLeaderCard,lcId);
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
                            selectAndGlowCard((ImageView) GameSceneController.this.developmentCardGrid.getChildren().get(col+ GameSceneController.this.developmentCardGrid.getColumnCount()*row),devCardToBuyId);
                        }
                    }
                }
            }
        });

    }

    private void selectAndGlowCard(Node node, Integer cardId) {
        glowNode(node,Color.CYAN);
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                createTooltip(node,"SELECT CARD");
                mouseEvent.consume();
            }
        });
        node.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                deactivateGlowingAndSelectEventHandler(node,false);
                client.sendMessageToServer(new SelectCardResponse(cardId));
                mouseEvent.consume();
            }
        });
    }

    public void displaySelectDevelopmentCardSlotRequest(boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                List<Node> nodeList= new ArrayList<>();
                if(firstSlotAvailable)nodeList.add(firstSlot);
                if(secondSlotAvailable)nodeList.add(secondSlot);
                if(thirdSlotAvailable)nodeList.add(thirdSlot);
                glowNode(activateProductionPane.getChildren().get(1),Color.CYAN);
                glowNode(activateProductionPane.getChildren().get(2),Color.CYAN);
                glowNode(activateProductionPane.getChildren().get(3),Color.CYAN);
                for(Node node : nodeList){
                    node.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            createTooltip(node,"CLICK TO SELECT THIS SLOT");
                            mouseEvent.consume();
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
                            mouseEvent.consume();
                        }
                    });
                }
            }
        });
    }

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
                ((HBox)popupVbox.getChildren().get(1)).getChildren().add(resourceImage);
                Map<Node,ResourceStorageType> nodeAndStorageType= new HashMap<>();
                if(isInLeaderDepot){
                    int counter=0;
                    for(Integer lcId: matchData.getLightClientByNickname(players.get(0)).getOwnedLeaderCards()){
                        if(matchData.getLightClientByNickname(players.get(0)).leaderCardIsActive(lcId)&&matchData.getLeaderCardByID(lcId).getEffectType().equals(EffectType.EXTRA_DEPOT.toString())){
                            if(counter==0) nodeAndStorageType.put(leftLeaderCard,ResourceStorageType.LEADER_DEPOT);
                            if(counter==1) nodeAndStorageType.put(rightLeaderCard,ResourceStorageType.LEADER_DEPOT);
                        }
                        counter++;
                    }
                }
                if(isInWarehouse){
                    nodeAndStorageType.put(warehouse_first_depot,ResourceStorageType.WAREHOUSE);
                    nodeAndStorageType.put(warehouse_second_depot,ResourceStorageType.WAREHOUSE);
                    nodeAndStorageType.put(warehouse_third_depot,ResourceStorageType.WAREHOUSE);
                }
                if(isInStrongbox){
                    nodeAndStorageType.put(((Pane)strongbox.getParent()).getChildren().get(1),ResourceStorageType.STRONGBOX);
                }
                for(Node node : nodeAndStorageType.keySet()){
                    glowNode(node,Color.CYAN);
                    node.setOnMouseEntered(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            createTooltip(node,"CLICK TO CHOOSE THIS STORAGE");
                            mouseEvent.consume();
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
                            mouseEvent.consume();
                        }
                    });
                }
            }
        });

    }

    /*   Use this to avoid Thread exception


    Platform.runLater(new Runnable() {
        @Override
        public void run() {
            // Update UI here.
         }
    });


 */

}
