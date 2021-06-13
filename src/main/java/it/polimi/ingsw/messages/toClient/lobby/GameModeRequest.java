package it.polimi.ingsw.messages.toClient.lobby;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class GameModeRequest extends MessageToClient {

    public GameModeRequest() {
        super(true);
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayGameModeRequest();
    }

    public String toString(){
        return "asking the game mode";
    }
}
