package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.messages.toServer.MessageToServer;

public interface Action {
    void execute(TurnController turnController);
    boolean isExecutable();
    void handleMessage(MessageToServer message);
}
