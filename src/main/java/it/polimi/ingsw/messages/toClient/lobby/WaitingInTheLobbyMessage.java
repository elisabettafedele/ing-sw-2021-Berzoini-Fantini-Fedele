package it.polimi.ingsw.messages.toClient.lobby;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class WaitingInTheLobbyMessage implements MessageToClient {

    @Override
    public void handleMessage(VirtualView view) {
        view.displayWaitingInTheLobbyMessage();
    }

    public String toString(){
        return "sending waiting in the lobby message";
    }
}
