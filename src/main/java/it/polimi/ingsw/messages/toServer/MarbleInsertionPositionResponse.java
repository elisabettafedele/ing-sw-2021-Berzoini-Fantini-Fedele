package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.controller.actions.TakeResourcesFromMarketAction;
import it.polimi.ingsw.messages.toClient.MarbleInsertionPositionRequest;

public class MarbleInsertionPositionResponse implements MessageToServer{
    private int insertionPosition;

    public MarbleInsertionPositionResponse(int insertionPosition){
        this.insertionPosition = insertionPosition;
    }

    public int getInsertionPosition(){
        return this.insertionPosition;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
