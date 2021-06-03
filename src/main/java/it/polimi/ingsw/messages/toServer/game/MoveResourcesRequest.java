package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;

import java.util.logging.Level;

public class MoveResourcesRequest implements MessageToServer {
    private String originDepot;
    private String destinationDepot;
    private Resource resource;
    private int quantity;

    public MoveResourcesRequest(String originDepot, String destinationDepot, Resource resource, int quantity) {
        this.originDepot = originDepot;
        this.destinationDepot = destinationDepot;
        this.resource = resource;
        this.quantity = quantity;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has requested to move " + quantity + " resources " + (resource==Resource.ANY ? "" : (" of type " + resource + " ")) + "from " + originDepot + " to " + destinationDepot);
        clientHandler.getCurrentAction().handleMessage(this);
    }


    public String getOriginDepot() {
        return originDepot;
    }

    public String getDestinationDepot() {
        return destinationDepot;
    }

    public int getQuantity() {
        return quantity;
    }

    public Resource getResource(){
        return resource;
    }

    public String toString(){
        return "asked to move resources from " + originDepot + " to " + destinationDepot;
    }

}
