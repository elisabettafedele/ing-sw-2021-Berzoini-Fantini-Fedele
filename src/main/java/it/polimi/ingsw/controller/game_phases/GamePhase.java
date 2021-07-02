package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.ClientHandler;

public interface GamePhase {
    void executePhase(Controller controller);
    void handleMessage(MessageToServer message, ClientHandler clientHandler);
}
