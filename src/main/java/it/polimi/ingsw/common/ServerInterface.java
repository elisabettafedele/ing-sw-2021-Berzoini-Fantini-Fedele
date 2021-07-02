package it.polimi.ingsw.common;

//TODO: JavaDoc
public interface ServerInterface {
    void newGameManager();
    void setNumberOfPlayersForNextGame(ClientHandlerInterface clientHandler, int numberOfPlayersForNextGame);
}
