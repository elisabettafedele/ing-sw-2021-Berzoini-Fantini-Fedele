package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.player.Player;

/**
 * Interface for the main methods that an action needs to implement
 */
public interface Action {
    /**
     * Execute the action, send the message to the client with the information and the requests to perform the action
     */
    void execute();

    /**
     * Check whether the player can execute the action or not
     * @return true if the action is executable
     */
    boolean isExecutable();

    /**
     * Handles the response from the client when, client-side, the action is completed
     * @param message the message with the choices made by the client
     */
    void handleMessage(MessageToServer message);

    /**
     * Reset the attributes stored from the previous message. Utility method to avoid instantiating a new action at the
     * beginning of each round
     * @param currentPlayer the player whose turn it is
     */
    void reset(Player currentPlayer);
}
