package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;

public class ChooseWhiteMarbleConversionResponse implements MessageToServer{
    private Resource resource;
    public ChooseWhiteMarbleConversionResponse(Resource resource){
        this.resource = resource;
    }

    public Resource getResource(){
        return resource;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
