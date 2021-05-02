package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;

import java.io.Serializable;

public interface MessageToServer extends Serializable {
    void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler);
}
