package it.polimi.ingsw.messages.toClient.lobby;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class NumberOfPlayersRequest extends MessageToClient {


    public NumberOfPlayersRequest() {
        super(true);
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.displayNumberOfPlayersRequest();
    }

    public String toString(){
        return "asking the desired number of players";
    }
}
