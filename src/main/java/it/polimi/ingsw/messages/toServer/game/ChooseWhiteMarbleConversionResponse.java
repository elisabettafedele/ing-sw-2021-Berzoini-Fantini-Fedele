package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Resource;

import java.util.List;
import java.util.logging.Level;

public class ChooseWhiteMarbleConversionResponse implements MessageToServer {
    private List<Resource> resources;
    public ChooseWhiteMarbleConversionResponse(List<Resource> resources){
        this.resources = resources;
    }

    public List<Resource> getResource(){
        return resources;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }

    public String toString(){
        return "received chosen white marble conversion: " + Marble.valueOf(resources.get(0).getValue());
    }

}
