package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.common.ClientInterface;
import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toServer.TimeoutExpiredResponse;

public class TimeoutExpiredMessage implements MessageToClient{

    @Override
    public void handleMessage(VirtualView view, ClientInterface client) {
        String reconnectString;
        do {
            view.displayTimeoutExpiredMessage();
            reconnectString = InputParser.getLine();
        } while (!reconnectString.equalsIgnoreCase("y")  && !reconnectString.equalsIgnoreCase("n"));
        client.sendMessageToServer(new TimeoutExpiredResponse(reconnectString.equals("y")));
    }
}
