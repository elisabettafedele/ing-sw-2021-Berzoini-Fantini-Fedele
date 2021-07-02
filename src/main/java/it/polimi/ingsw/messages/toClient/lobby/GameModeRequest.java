package it.polimi.ingsw.messages.toClient.lobby;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class GameModeRequest extends MessageToClient {

    public GameModeRequest() {
        super(true);
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.displayGameModeRequest();
    }

    public String toString(){
        return "asking the game mode";
    }
}
