package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;
import java.util.logging.Level;

public class ChooseWhiteMarbleConversionResponse implements MessageToServer{
    private List<Resource> resources;
    public ChooseWhiteMarbleConversionResponse(List<Resource> resources){
        this.resources = resources;
    }

    public List<Resource> getResource(){
        return resources;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + "that has chosen a white marble conversion");
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
