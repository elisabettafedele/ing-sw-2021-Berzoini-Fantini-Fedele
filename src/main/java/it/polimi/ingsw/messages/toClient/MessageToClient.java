package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.View;

public interface MessageToClient {
    public void handleMessage(View view, Client client);
}
