package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;

public class NotifyEndRemoveResources implements MessageToServer {
    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) { }
}
