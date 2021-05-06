package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;

public class MoveResourcesRequest implements MessageToServer{
    private String originDepot;
    private String destinationDepot;
    private int quantity;

    public MoveResourcesRequest(String originDepot, String destinationDepot, int quantity) {
        this.originDepot = originDepot;
        this.destinationDepot = destinationDepot;
        this.quantity = quantity;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {

    }
}
