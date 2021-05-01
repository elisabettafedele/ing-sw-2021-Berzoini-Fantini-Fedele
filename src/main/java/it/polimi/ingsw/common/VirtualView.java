package it.polimi.ingsw.common;

import it.polimi.ingsw.enumerations.GameMode;

import java.util.List;

public interface VirtualView {
    //TO WRITE

    void displayGameModeRequest();
    void displayNicknameRequest(boolean isRetry, boolean alreadyTaken);
    void displayNumberOfPlayersRequest(boolean isRetry);
    void displayWaitingInTheLobbyMessage();
    void displayPlayersReadyToStartMessage(List<String> nicknames);
    void displayTimeoutExpiredMessage();
    void displayMarbleInsertionPositionRequest();



    //TO READ
    GameMode getGameMode();
    String getNickname();
}
