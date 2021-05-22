package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;

import java.util.logging.Level;

public class SwapWarehouseDepotsRequest implements MessageToServer {
    private String originDepot;
    private String destinationDepot;

    public SwapWarehouseDepotsRequest(String originDepot, String destinationDepot) {
        this.originDepot = originDepot;
        this.destinationDepot = destinationDepot;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has requested to swap " + originDepot + " and " + destinationDepot);
        clientHandler.getCurrentAction().handleMessage(this);
    }


    public String getOriginDepot() {
        return originDepot;
    }

    public String getDestinationDepot() {
        return destinationDepot;
    }
}
