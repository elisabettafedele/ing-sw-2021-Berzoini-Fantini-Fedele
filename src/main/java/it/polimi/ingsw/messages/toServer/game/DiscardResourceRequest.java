package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;

import java.util.logging.Level;

public class DiscardResourceRequest implements MessageToServer {
    Resource resource;

    public DiscardResourceRequest(Resource resource){
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }

    public String toString(){
        return "asked to discard a resource";
    }

}
