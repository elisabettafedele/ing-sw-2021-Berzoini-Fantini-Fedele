package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.ClientHandler;

public interface GamePhase {
    public void executePhase(Controller controller);
    public void handleMessage(MessageToServer message, ClientHandler clientHandler);
}
