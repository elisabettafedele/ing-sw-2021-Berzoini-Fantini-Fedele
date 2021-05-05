package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Resource;

import javax.swing.*;

public class ChooseStorageTypeResponse implements MessageToServer{
    private Resource resource;
    private String storageType;

    public ChooseStorageTypeResponse(Resource resource, String storageType){
        this.resource = resource;
        this.storageType = storageType;
    }
    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }

    public Resource getResource() {
        return resource;
    }

    public String getStorageType() {
        return storageType;
    }
}
