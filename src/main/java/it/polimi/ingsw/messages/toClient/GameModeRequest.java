package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.ClientInterface;
import it.polimi.ingsw.common.VirtualView;
//TODO: This MessageToClient needs a MessageToServer... Need to be corrected?
import it.polimi.ingsw.messages.toServer.GameModeResponse;

public class GameModeRequest implements MessageToClient{

    @Override
    public void handleMessage(VirtualView view, ClientInterface client) {
        view.displayGameModeRequest();
        client.sendMessageToServer(new GameModeResponse(view.getGameMode()));
    }
}
