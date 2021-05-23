package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.enumerations.ActionType;
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
        clientHandler.getController().handleMessage(this,clientHandler);
    }
}
