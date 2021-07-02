package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.server.ClientHandler;

/**
 * Common interface to all the phase of the game
 */
public interface GamePhase {
    /**
     * Method to start the phase
     * @param controller the {@link Controller} to manage actions and model
     */
    public void executePhase(Controller controller);

    /**
     * Method to manage the incoming messages
     * @param message the {@link MessageToServer} received by the {@link it.polimi.ingsw.client.Client}
     * @param clientHandler the connection to a specific {@link it.polimi.ingsw.client.Client}
     */
    public void handleMessage(MessageToServer message, ClientHandler clientHandler);
}
