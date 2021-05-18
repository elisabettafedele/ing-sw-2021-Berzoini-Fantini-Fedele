package it.polimi.ingsw.messages.toClient.lobby;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class NumberOfPlayersRequest implements MessageToClient {


    @Override
    public void handleMessage(VirtualView view) {
        view.displayNumberOfPlayersRequest();
    }
}
