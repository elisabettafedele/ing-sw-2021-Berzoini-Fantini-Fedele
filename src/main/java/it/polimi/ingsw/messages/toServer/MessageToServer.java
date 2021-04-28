package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Server;

public interface MessageToServer {
    public void handleMessage(Server server, ClientHandler clientHandler);
}
