package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.Marble;

public class ChooseWhiteMarbleConversionResponse implements MessageToServer{
    private Marble marble;
    public ChooseWhiteMarbleConversionResponse(Marble marble){
        this.marble = marble;
    }

    public Marble getMarble(){
        return marble;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
