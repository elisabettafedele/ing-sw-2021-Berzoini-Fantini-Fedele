package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Server;

import java.io.Serializable;

public interface MessageToServer extends Serializable {
    public void handleMessage(Server server, ClientHandler clientHandler);
}
