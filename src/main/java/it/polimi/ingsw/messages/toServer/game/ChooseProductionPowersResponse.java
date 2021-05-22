package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.Value;
import it.polimi.ingsw.server.Server;

import java.util.List;
import java.util.logging.Level;

public class ChooseProductionPowersResponse implements MessageToServer {

    List<Integer> productionPowersSelected;
    List<Value> basicProductionPower;

    public List<Integer> getProductionPowersSelected() {
        return productionPowersSelected;
    }

    public List<Value> getBasicProductionPower() {
        return basicProductionPower;
    }

    public ChooseProductionPowersResponse(List<Integer> productionPowersSelected) {
        this.productionPowersSelected = productionPowersSelected;
    }

    public ChooseProductionPowersResponse(List<Integer> productionPowersSelected, List<Value> basicProductionPower) {
        this.productionPowersSelected = productionPowersSelected;
        this.basicProductionPower = basicProductionPower;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has chosen his production powers : " + productionPowersSelected);
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
