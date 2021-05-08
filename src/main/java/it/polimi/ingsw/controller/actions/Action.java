package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.player.Player;

public interface Action {
    void execute();
    boolean isExecutable();
    void handleMessage(MessageToServer message);
    void reset(Player currentPlayer);
}
