package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.ClientInterface;
import it.polimi.ingsw.common.VirtualView;


public class PlayersReadyToStartMessage implements MessageToClient {

    @Override
    public void handleMessage(VirtualView view, ClientInterface client) {
        view.displayPlayersReadyToStartMessage();
    }
}
