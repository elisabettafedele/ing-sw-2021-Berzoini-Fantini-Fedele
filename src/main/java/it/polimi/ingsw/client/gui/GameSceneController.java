package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.util.List;

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
    private TextArea textArea;




    @FXML
    public void initialize() {
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
}
