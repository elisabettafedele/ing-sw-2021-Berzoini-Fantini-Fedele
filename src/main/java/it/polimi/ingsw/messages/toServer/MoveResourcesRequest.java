package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Resource;

public class MoveResourcesRequest implements MessageToServer{
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
}
