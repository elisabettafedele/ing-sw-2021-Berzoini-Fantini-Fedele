package it.polimi.ingsw.common;



public interface ServerInterface {
    void NewGameManager();
    void setNumberOfPlayersForNextGame(ClientHandlerInterface clientHandler, int numberOfPlayersForNextGame);
}
