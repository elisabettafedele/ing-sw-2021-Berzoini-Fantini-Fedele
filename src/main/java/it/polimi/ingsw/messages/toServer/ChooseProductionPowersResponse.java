package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.model.cards.Value;

import java.util.List;

public class ChooseProductionPowersResponse implements MessageToServer{

    List<Integer> productionPowersSelected;

    public List<Integer> getProductionPowersSelected() {
        return productionPowersSelected;
    }

    public ChooseProductionPowersResponse(List<Integer> productionPowersSelected) {
        this.productionPowersSelected = productionPowersSelected;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
