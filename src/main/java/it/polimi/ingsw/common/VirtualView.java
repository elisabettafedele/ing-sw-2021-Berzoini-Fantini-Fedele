package it.polimi.ingsw.common;

import it.polimi.ingsw.enumerations.GameMode;

public interface VirtualView {
    //TO WRITE

    void displayGameModeRequest();
    void displayNicknameRequest(boolean isRetry, boolean alreadyTaken);
    void displayNumberOfPlayersRequest(boolean isRetry);
    void displayWaitingInTheLobbyMessage();
    void displayPlayersReadyToStartMessage();



    //TO READ
    GameMode getGameMode();
    String getNickname();
}
