package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Server;
import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.controller.actions.TakeResourcesFromMarketAction;
import it.polimi.ingsw.messages.toClient.MarbleInsertionPositionRequest;

public class MarbleInsertionPositionResponse implements MessageToServer{
    Action action;
    private int insertionPosition;

    public MarbleInsertionPositionResponse(Action action, int insertionPosition){
        this.action = action;
        this.insertionPosition = insertionPosition;
    }

    private boolean isLegal(){
        return insertionPosition > 0 && insertionPosition < 8;
    }
    @Override
    public void handleMessage(Server server, ClientHandler clientHandler) {
        if (!isLegal())
            clientHandler.sendMessageToClient(new MarbleInsertionPositionRequest(action, true));
        else
            ((TakeResourcesFromMarketAction) action).handleInsertionPositionResponse(insertionPosition);
        //TODO hqndle the message
    }
}
