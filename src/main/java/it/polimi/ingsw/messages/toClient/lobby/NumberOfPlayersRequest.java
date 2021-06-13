package it.polimi.ingsw.messages.toClient.lobby;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class NumberOfPlayersRequest extends MessageToClient {


    public NumberOfPlayersRequest() {
        super(true);
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayNumberOfPlayersRequest();
    }

    public String toString(){
        return "asking the desired number of players";
    }
}
