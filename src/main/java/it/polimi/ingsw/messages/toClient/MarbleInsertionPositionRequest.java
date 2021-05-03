package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.controller.actions.Action;

public class MarbleInsertionPositionRequest implements MessageToClient{
    boolean isRetry;
    Action action;
    public MarbleInsertionPositionRequest(Action action, boolean isRetry) {
        this.isRetry = isRetry;
        this.action = action;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayMarbleInsertionPositionRequest(action);
        //client.sendMessageToServer(new MarbleInsertionPositionResponse(action, InputParser.getInt("The position must be an integer index")));
    }
}
