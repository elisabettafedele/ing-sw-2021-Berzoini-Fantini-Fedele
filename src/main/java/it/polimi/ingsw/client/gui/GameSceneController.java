package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.LightClient;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.PopesTileState;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

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
    private GridPane strongbox;
    @FXML
    private Pane faithtrack;
    @FXML
    private Pane firstSlot;
    @FXML
    private Pane secondSlot;
    @FXML
    private Pane thirdSlot;
    @FXML
    private Pane popeTiles;
    @FXML
    private GridPane developmentCardGrid;
    @FXML
    private GridPane marketGrid;
    @FXML
    private Pane leftLeaderDepot;
    @FXML
    private ImageView leftLeaderCard;
    @FXML
    private Pane rightLeaderDepot;
    @FXML
    private ImageView rightLeaderCard;
    @FXML
    private Label mainLabelNames;
    @FXML
    private Label mainLabelStats;
    @FXML
    private Label currentPBNicknameLabel;
    @FXML
    private ImageView previousPlayerButton;
    @FXML
    private ImageView nextPlayerButton;

    List<String> players;
    int currentPlayerIndex;

    MatchData matchData;



    @FXML
    public void initialize() {
        matchData=MatchData.getInstance();
        currentPlayerIndex=0;
        players=matchData.getAllNicknames();
        ColorAdjust colorAdjust=new ColorAdjust();
        colorAdjust.setBrightness(0.4);
        previousPlayerButton.setDisable(true);
        previousPlayerButton.setEffect(colorAdjust);
        nextPlayerButton.setDisable(true);
        nextPlayerButton.setEffect(colorAdjust);
        updateDevelopmentCardGridView();
        updateMarketView();
        updateMainLabel();
        updateView();
    }

    private void updateMainLabel() {
        mainLabelStats.setText("\n\n");
        mainLabelNames.setText("\n\n");
        for(int i=0;i<players.size();i++){
            mainLabelNames.setText(mainLabelNames.getText() + players.get(i) + "\n");
            mainLabelStats.setText(mainLabelStats.getText() + "FT: " + matchData.getLightClientByNickname(players.get(i)).getFaithTrackPosition() + " VP: " + matchData.getLightClientByNickname(players.get(i)).getVictoryPoints() + "\n");
        }
    }

    private void updateView() {
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

    private void updateDevelopmentCardsSlots() {
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
                if(currentPlayerIndex==0){
                    if(i==0){
                        leftLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/front/" + leaderCards.get(i).getID() + ".png").toString()));
                        ColorAdjust colorAdjust=new ColorAdjust();
                        colorAdjust.setBrightness(0.4);
                        leftLeaderCard.setEffect(colorAdjust);
                        leftLeaderCard.setVisible(true);
                    }
                    if(i==1){
                        rightLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/front/" + leaderCards.get(i).getID() + ".png").toString()));
                        ColorAdjust colorAdjust=new ColorAdjust();
                        colorAdjust.setBrightness(0.4);
                        rightLeaderCard.setEffect(colorAdjust);
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

    public void setGUI(GUI gui) {
        this.gui=gui;
    }

    public void setClient(Client client) {
        this.client = client;
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
                updateView();
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
