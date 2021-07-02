package it.polimi.ingsw.common;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.GameMode;

import java.io.Serializable;

/**
 * Interface for {@link it.polimi.ingsw.server.ClientHandler}
 */
public interface ClientHandlerInterface {
    /**
     * Method used to send message to the client, through an object stream
     * @param message the message to be sent
     */
    void sendMessageToClient(Serializable message);
    GameMode getGameMode();
    String getNickname();
    ClientHandlerPhase getClientHandlerPhase();
    Action getCurrentAction() ;
    void setCurrentAction(Action currentAction);
    void setNickname(String nickname);
    void setClientHandlerPhase(ClientHandlerPhase clientHandlerPhase);
    void setGameMode(GameMode gameMode);
    void setGameStarted(boolean gameStarted);
    void setNumberOfPlayersForNextGame(int numberOfPlayersForNextGame);
    Controller getController();
    /**
     * Timer used to disconnect players who are too slow in sending their responses
     */
    void startTimer();
}
