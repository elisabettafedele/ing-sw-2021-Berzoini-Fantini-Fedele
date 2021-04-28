package messages.toServer;

import Server.ClientHandler;
import Server.Server;

public interface MessageToServer {
    public void handleMessage(Server server, ClientHandler clientHandler);
}
