package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.Value;
import it.polimi.ingsw.server.Server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ChooseProductionPowersResponse implements MessageToServer {

    List<Integer> productionPowersSelected;
    List<Value> basicProductionPower;

    private Map<Integer, List<Value>> selectedLeaderProductions;

    public List<Integer> getProductionPowersSelected() {
        return productionPowersSelected;
    }

    public List<Value> getBasicProductionPower() {
        return basicProductionPower;
    }

    public ChooseProductionPowersResponse(List<Integer> productionPowersSelected) {
        this.selectedLeaderProductions = new HashMap<>();
        this.productionPowersSelected = productionPowersSelected;
    }

    public ChooseProductionPowersResponse(List<Integer> productionPowersSelected, List<Value> basicProductionPower) {
        this.selectedLeaderProductions = new HashMap<>();
        this.productionPowersSelected = productionPowersSelected;
        this.basicProductionPower = basicProductionPower;
    }

    public List<Value> getSelectedLeaderProductions(int id) {
        return selectedLeaderProductions.get(id);
    }

    public void addLeaderProduction(int id, List<Value> leaderProduction){
        selectedLeaderProductions.put(id, leaderProduction);
    }


    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has chosen his production powers : " + productionPowersSelected);
        clientHandler.getCurrentAction().handleMessage(this);
    }

    public String toString(){
        return "received selected production " + (productionPowersSelected.size() > 1  ? "powers" : "power");
    }

}
