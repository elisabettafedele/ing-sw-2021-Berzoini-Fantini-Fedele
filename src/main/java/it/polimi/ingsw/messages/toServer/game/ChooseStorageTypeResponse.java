package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;

import javax.swing.*;
import java.util.logging.Level;

public class ChooseStorageTypeResponse implements MessageToServer {
    private Resource resource;
    private String storageType;
    private boolean canDiscard;
    private boolean canReorganize;

    public ChooseStorageTypeResponse(Resource resource, String storageType, boolean canDiscard, boolean canReorganize){
        this.resource = resource;
        this.storageType = storageType;
        this.canDiscard = canDiscard;
        this.canReorganize = canReorganize;
    }
    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has chosen to store the " + resource + " in " + storageType);
        if (!canDiscard) {
            if (clientHandler.getController() != null)
                clientHandler.getController().handleMessage(this, clientHandler);        }
        else
            clientHandler.getCurrentAction().handleMessage(this);
    }

    public Resource getResource() {
        return resource;
    }

    public String getStorageType() {
        return storageType;
    }

    public String toString(){
        return "received desired storage type: " + storageType;
    }

}
