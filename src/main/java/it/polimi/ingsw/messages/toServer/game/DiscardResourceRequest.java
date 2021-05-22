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
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has decided to discard one " + resource);
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
