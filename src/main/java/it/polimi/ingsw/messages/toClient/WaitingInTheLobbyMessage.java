package messages.toClient;

import client.Client;
import client.View;

import java.io.Serializable;

public class WaitingInTheLobbyMessage implements MessageToClient, Serializable {

    @Override
    public void handleMessage(View view, Client client) {
        view.displayWaitingInTheLobbyMessage();
    }
}
