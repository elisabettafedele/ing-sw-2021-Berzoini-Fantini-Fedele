package it.polimi.ingsw.common;


import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Value;

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
    void displayChooseWhiteMarbleConversionRequest(List<Resource> marbles, int numberOfMarbles);
    void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion);
    void displayChooseStorageTypeRequest(Resource resource, List<String> availableDepots, boolean setUpPhase);
    void displayReorganizeDepotsRequest(List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResources);
    //SETUP
    void displayChooseLeaderCardsRequest(List<Integer> leaderCards);
    void displaySelectCardRequest(List<Integer> CardsIDs, boolean leaderORdevelopment);
    void loadLeaderCards(List<LeaderCard> leaderCards);
    void displayChooseResourceTypeRequest(List<Resource> resourceTypes, int quantity);
    void loadDevelopmentCards(Map<Integer, List<String>> lightDevelopmentCards);
    void displayChooseProductionPowersRequest(Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources);
    void displaySelectDevelopmentCardSlotRequest(boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable);
    void displayMessage(String message);

    void displaySelectStorageRequest(Resource resource, boolean isInWarehouse, boolean isInStrongbox, boolean isInLeaderDepot);
}
