package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;

import java.util.logging.Level;

public class NotifyEndDepotsReorganization implements MessageToServer {
    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        clientHandler.getCurrentAction().handleMessage(this);
    }

    public String toString(){
        return "ended the reorganization of the depots";
    }

}
