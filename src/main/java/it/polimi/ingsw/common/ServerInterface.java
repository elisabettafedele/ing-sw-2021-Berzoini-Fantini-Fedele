package it.polimi.ingsw.common;

//TODO
public interface ServerInterface {
    void newGameManager();
    void setNumberOfPlayersForNextGame(ClientHandlerInterface clientHandler, int numberOfPlayersForNextGame);
}
