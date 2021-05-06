package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Resource;

import javax.swing.*;

public class ChooseStorageTypeResponse implements MessageToServer{
    private Resource resource;
    private String storageType;
    private boolean setUpPhase;

    public ChooseStorageTypeResponse(Resource resource, String storageType, boolean setUpPhase){
        this.resource = resource;
        this.storageType = storageType;
        this.setUpPhase = setUpPhase;
    }
    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        if (setUpPhase)
            clientHandler.getController().handleMessage(this,clientHandler);
        else
            clientHandler.getCurrentAction().handleMessage(this);
    }

    public Resource getResource() {
        return resource;
    }

    public String getStorageType() {
        return storageType;
    }
}
