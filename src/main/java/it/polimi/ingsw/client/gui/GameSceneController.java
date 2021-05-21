package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.LightClient;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Marble;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private Label mainLabel;

    List<String> players;
    int currentPlayerIndex;

    MatchData matchData;



    @FXML
    public void initialize() {
        matchData=MatchData.getInstance();
        currentPlayerIndex=0;
        List<Node> nodeChildren= rightPane.getChildren();
        for(Node node : nodeChildren){
            hideNodeChildren(((Pane) node).getChildren());
        }
        strongbox.setVisible(true);
        faithtrack.getChildren().get(0).setVisible(true);
        rightLeaderDepot.getChildren().get(0).setVisible(false);
        rightLeaderDepot.getChildren().get(1).setVisible(false);
        leftLeaderDepot.getChildren().get(0).setVisible(false);
        leftLeaderDepot.getChildren().get(1).setVisible(false);
        players=matchData.getAllNicknames();
        updateDevelopmentCardGridView(matchData.getDevelopmentCardGrid());
        updateLeaderCardsView(matchData.getLightClientByNickname(players.get(currentPlayerIndex)).getOwnedLeaderCards());
        //updateMarketView(matchData.getMarketTray());
    }

    private void updateLeaderCardsView(List<Integer> leaderCardsId) {
        List<LightLeaderCard> leaderCards =new ArrayList<>();
        for(Integer lcID :leaderCardsId){
            leaderCards.add(matchData.getLeaderCardByID(lcID));
        }
        for(int i=0; i<leaderCards.size(); i++){
            //if active, it shows the LeaderCard
            if(matchData.getLightClientByNickname(players.get(currentPlayerIndex)).leaderCardIsActive(leaderCards.get(i).getID())){
                if(i==0){
                    leftLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/front/" + leaderCards.get(i).getID() + ".png").toString()));
                    leftLeaderCard.setEffect(null);
                }
                if(i==1){
                    rightLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/front/" + leaderCards.get(i).getID() + ".png").toString()));
                    rightLeaderCard.setEffect(null);
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
                           else{
                               leftLeaderDepot.getChildren().get(ii).setVisible(false);
                           }
                       }
                       leftLeaderDepot.setVisible(true);
                   }
                    if(i==1){
                        for(int ii=0;ii<rightLeaderDepot.getChildren().size();ii++){
                            if(ii<quantity){
                                ((ImageView) rightLeaderDepot.getChildren().get(ii)).setImage(new Image(GameSceneController.class.getResource("/img/punchboard/" + leaderCards.get(i).getEffectDescription().get(0).toLowerCase() + ".png").toString()));
                                rightLeaderDepot.getChildren().get(ii).setVisible(true);
                            }
                            else{
                                rightLeaderDepot.getChildren().get(ii).setVisible(false);
                            }
                        }
                        rightLeaderDepot.setVisible(true);
                    }
                }
                //if not extra depot LeaderCard, it hides LeaderDepots
                else if(!leaderCards.get(i).getEffectType().equals("EXTRA_DEPOT")){
                    if(i==0){
                        leftLeaderDepot.setVisible(false);
                    }
                    if(i==1){
                        rightLeaderDepot.setVisible(false);
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
                    }
                    if(i==1){
                        rightLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/front/" + leaderCards.get(i).getID() + ".png").toString()));
                        ColorAdjust colorAdjust=new ColorAdjust();
                        colorAdjust.setBrightness(0.4);
                        rightLeaderCard.setEffect(colorAdjust);
                    }
                }
                //if player is not watching its leaderCards, he can only see the back of inactive cards
                else{
                    if(i==0){
                        leftLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/back/leaderCardsBack.png").toString()));
                        leftLeaderCard.setEffect(null);
                    }
                    if(i==1){
                        rightLeaderCard.setImage(new Image(GameSceneController.class.getResource("/img/Cards/LeaderCards/back/leaderCardsBack.png").toString()));
                        rightLeaderCard.setEffect(null);
                    }
                }
                //hide discarded cards
                for(int ii=2 ; ii>leaderCards.size();ii--){
                    if((ii-1)==1){
                        rightLeaderCard.setVisible(false);
                    }
                    if((ii-1)==0){
                        leftLeaderCard.setVisible(false);
                    }
                }
            }
        }
    }




    private void updateMarketView(Marble[][] marketTray) {
        for(int row=0;row< 3 ;row++ ){
            for(int col=0; col< 4; col++){
                ImageView marbleImage= new ImageView(new Image(GameSceneController.class.getResource("/img/punchboard/marble_" + marketTray[row][col].toString().toLowerCase() + ".png").toString()));

            }
        }

    }

    private void updateDevelopmentCardGridView(List<Integer> developmentCardGridId) {
        List<LightDevelopmentCard> developmentCardGrid =new ArrayList<>();
        for(Integer devCardId :developmentCardGridId){
            developmentCardGrid.add(matchData.getDevelopmentCardByID(devCardId));
        }
        for(LightDevelopmentCard devCard : developmentCardGrid){
            int row = (Level.valueOf(devCard.getFlagLevel()).getValue() * - 1) + 2;
            int col = FlagColor.valueOf(devCard.getFlagColor()).getValue();
            ((ImageView) this.developmentCardGrid.getChildren().get(col+ this.developmentCardGrid.getColumnCount()*row)).setImage(new Image(GameSceneController.class.getResource("/img/Cards/DevelopmentCards/front/" + devCard.getID() + ".png").toString()));

        }
    }

    private void moveMarkerInFaithTrack(int moveOffset) {

    }

    private void hideNodeChildren(List<Node> nodeChildren) {
        for(Node node:nodeChildren){
            node.setVisible(false);
        }
    }

    public void setGUI(GUI gui) {
        this.gui=gui;
    }

    public void setClient(Client client) {
        this.client = client;
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
