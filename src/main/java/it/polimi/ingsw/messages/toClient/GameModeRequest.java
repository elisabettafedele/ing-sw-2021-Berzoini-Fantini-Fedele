package messages.toClient;

import client.Client;
import client.View;
import messages.toServer.GameModeResponse;

import java.io.Serializable;

public class GameModeRequest implements MessageToClient, Serializable {

    @Override
    public void handleMessage(View view, Client client) {
        view.displayGameModeRequest();
        client.sendMessageToServer(new GameModeResponse(view.getGameMode()));
    }
}
