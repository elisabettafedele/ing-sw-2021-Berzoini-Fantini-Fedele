package it.polimi.ingsw.common;



public interface ServerInterface {
    void newGameManager();
    void setNumberOfPlayersForNextGame(ClientHandlerInterface clientHandler, int numberOfPlayersForNextGame);
}
