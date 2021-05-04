package it.polimi.ingsw.common;


import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.model.cards.LeaderCard;

import java.util.List;
import java.util.Map;

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
    //SETUP
    void displayChooseLeaderCardsRequest(List<Integer> leaderCards);
    void displaySelectLeaderCardRequest(List<Integer> leaderCards);
    void loadLeaderCards(List<LeaderCard> leaderCards);
    void loadDevelopmentCards(Map<Integer, List<String>> lightDevelopmentCards);
}
