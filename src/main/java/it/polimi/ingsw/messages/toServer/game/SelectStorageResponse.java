package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.messages.toServer.MessageToServer;

/**
 * Message to notify the server where the player wants to store a specific resource
 */
public class SelectStorageResponse implements MessageToServer {
    ResourceStorageType resourceStorageType;
    Resource resource;
    public SelectStorageResponse(Resource resource, ResourceStorageType resourceStorageType) {
        this.resourceStorageType=resourceStorageType;
        this.resource=resource;
    }

    public ResourceStorageType getResourceStorageType() {
        return resourceStorageType;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }

    public String toString(){
        return "has chosen to store the resource in " + resourceStorageType;
    }

}
