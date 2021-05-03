package it.polimi.ingsw.common;


import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.enumerations.Marble;

import java.util.List;

public interface VirtualView {
    //LOBBY
    void displayGameModeRequest();
    void displayNicknameRequest(boolean isRetry, boolean alreadyTaken);
    void displayNumberOfPlayersRequest(boolean isRetry);
    void displayWaitingInTheLobbyMessage();
    void displayPlayersReadyToStartMessage(List<String> nicknames);
    //CONNECTION
    void displayTimeoutExpiredMessage();
    //ACTIONS
    void displayMarbleInsertionPositionRequest(Action action);
    void displayChooseWhiteMarbleConversionRequest(List<Marble> marbles, int numberOfMarbles);
    void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion);
}
