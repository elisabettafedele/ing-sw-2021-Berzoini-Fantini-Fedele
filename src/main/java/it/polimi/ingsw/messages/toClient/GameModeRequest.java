package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.messages.toServer.GameModeResponse;

import java.io.Serializable;

public class GameModeRequest implements MessageToClient, Serializable {

    @Override
    public void handleMessage(View view, Client client) {
        view.displayGameModeRequest();
        client.sendMessageToServer(new GameModeResponse(view.getGameMode()));
    }
}
