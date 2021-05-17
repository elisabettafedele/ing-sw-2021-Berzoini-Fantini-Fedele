package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;

public class MarbleInsertionPositionResponse implements MessageToServer {
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
