package it.polimi.ingsw.messages.toServer;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.Server.Server;

public class TimeoutExpiredResponse implements MessageToServer{
    private boolean reconnect;
    public TimeoutExpiredResponse(boolean reconnect){
        this.reconnect = reconnect;
    }

    @Override
    public void handleMessage(Server server, ClientHandler clientHandler) {
        if (reconnect)
            server.handleReconnection(clientHandler);
    }
}
