package it.polimi.ingsw.common;

import it.polimi.ingsw.Server.ClientHandler;

public interface ServerInterface {
    void handleNicknameChoice(ClientHandler connection);
    void NewGameManager();
    void setNumberOfPlayersForNextGame(int numberOfPlayersForNextGame);
}
