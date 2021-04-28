package client;

import enumerations.GameMode;

public interface View {
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
