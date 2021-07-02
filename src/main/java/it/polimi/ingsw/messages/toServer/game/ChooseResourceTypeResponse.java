package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;
import java.util.logging.Level;

/**
 * Message to notify the server the type of the initial resource chosen
 */
public class ChooseResourceTypeResponse implements MessageToServer {

    private List<Resource> resources;

    public ChooseResourceTypeResponse(List<Resource> resources){
        this.resources = resources;
    }

    public List<Resource> getResources() {
        return resources;
    }
    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has chosen his resource types");
        if (clientHandler.getController() != null)
            clientHandler.getController().handleMessage(this, clientHandler);    }

    public String toString(){
        return "received chosen resource" + (resources.size() > 1 ? "s" : "") + " type: " + resources;
    }

}
