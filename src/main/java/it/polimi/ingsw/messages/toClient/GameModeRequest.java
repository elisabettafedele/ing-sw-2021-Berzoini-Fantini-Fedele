package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

public class GameModeRequest implements MessageToClient{

    @Override
    public void handleMessage(VirtualView view) {
        view.displayGameModeRequest();
    }
}
