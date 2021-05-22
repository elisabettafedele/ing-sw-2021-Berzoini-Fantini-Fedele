package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.Server;

import java.util.logging.Level;

public class ChooseActionResponse implements MessageToServer {
    private int actionChosen;
    public ChooseActionResponse(int actionChosen) {
        this.actionChosen=actionChosen;
    }

    public int getActionChosen() {
        return actionChosen;
    }

    @Override
    public void handleMessage(ServerInterface server, ClientHandlerInterface clientHandler) {
        Server.SERVER_LOGGER.log(Level.INFO, "New message from " + clientHandler.getNickname() + " that has chosen his next action : " + actionChosen);
        clientHandler.getController().handleMessage(this,clientHandler);
    }
}
