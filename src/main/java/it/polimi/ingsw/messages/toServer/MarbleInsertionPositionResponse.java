package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.messages.toClient.MarbleInsertionPositionRequest;

public class MarbleInsertionPositionResponse implements MessageToServer{
    private int insertionPosition;

    public MarbleInsertionPositionResponse(int insertionPosition){
        this.insertionPosition = insertionPosition;
    }

    private boolean isLegal(){
        return insertionPosition > 0 && insertionPosition < 8;
    }
    @Override
    public void handleMessage(Server server, ClientHandler clientHandler) {
        if (!isLegal())
            clientHandler.sendMessageToClient(new MarbleInsertionPositionRequest(true));
        //TODO hqndle the message
    }
}
