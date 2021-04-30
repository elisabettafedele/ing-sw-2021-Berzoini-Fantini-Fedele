package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.common.ClientInterface;
import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toServer.MarbleInsertionPositionResponse;

public class MarbleInsertionPositionRequest implements MessageToClient{
    boolean isRetry;
    public MarbleInsertionPositionRequest(boolean isRetry) {
        this.isRetry = isRetry;
    }

    @Override
    public void handleMessage(VirtualView view, ClientInterface client) {
        //TODO view
        client.sendMessageToServer(new MarbleInsertionPositionResponse(InputParser.getInt("The position must be an integer index")));
    }
}
