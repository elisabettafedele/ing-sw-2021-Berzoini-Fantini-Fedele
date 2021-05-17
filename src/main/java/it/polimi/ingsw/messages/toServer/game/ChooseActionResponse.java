package it.polimi.ingsw.messages.toServer.game;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.common.ServerInterface;
import it.polimi.ingsw.messages.toServer.MessageToServer;

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
