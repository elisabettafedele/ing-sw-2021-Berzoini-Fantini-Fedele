package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;

public class SwapWarehouseDepotsRequest implements MessageToServer{
    private String originDepot;
    private String destinationDepot;

    public SwapWarehouseDepotsRequest(String originDepot, String destinationDepot) {
        this.originDepot = originDepot;
        this.destinationDepot = destinationDepot;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {

    }
}
