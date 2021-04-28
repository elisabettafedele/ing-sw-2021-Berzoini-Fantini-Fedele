package messages.toClient;

import client.Client;
import client.View;

public interface MessageToClient {
    public void handleMessage(View view, Client client);
}
