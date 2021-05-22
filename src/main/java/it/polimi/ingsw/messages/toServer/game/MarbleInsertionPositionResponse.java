package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;

import java.util.logging.Level;

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
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has chosen to insert a marble in the market in position " + insertionPosition);
        clientHandler.getCurrentAction().handleMessage(this);
    }
}
