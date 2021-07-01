package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toServer.MessageToServer;

import java.util.List;
import java.util.stream.Collectors;

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
        return "received chosen white marble conversion: " + resources.stream().map(x -> Marble.valueOf(x.getValue())).collect(Collectors.toList());
    }

}
