package it.polimi.ingsw.common;


import it.polimi.ingsw.enumerations.ActionType;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toClient.matchData.MatchDataMessage;
import it.polimi.ingsw.model.cards.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface VirtualView {
    //LOBBY
    void displayGameModeRequest();
    void displayNicknameRequest(boolean isRetry, boolean alreadyTaken);
    void displayNumberOfPlayersRequest();
    void displayWaitingInTheLobbyMessage();
    void displayPlayersReadyToStartMessage(List<String> nicknames);
    //CONNECTION
    void displayTimeoutExpiredMessage();
    //ACTIONS
    void displayChooseActionRequest(Map<ActionType, Boolean> executableActions, boolean standardActionDone);
    void displayMarbleInsertionPositionRequest();
    void displayChooseWhiteMarbleConversionRequest(List<Resource> marbles, int numberOfMarbles);
    void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion);
    void displayChooseStorageTypeRequest(Resource resource, List<String> availableDepots, boolean canDiscard, boolean canReorganize);
    void displayReorganizeDepotsRequest(List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResources);
    void displayChooseLeaderCardsRequest(List<Integer> leaderCards);
    void displaySelectCardRequest(List<Integer> CardsIDs, boolean leaderORdevelopment);
    void displayChooseProductionPowersRequest(Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources);
    void displaySelectDevelopmentCardSlotRequest(boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable);
    void displaySelectStorageRequest(Resource resource, boolean isInWarehouse, boolean isInStrongbox, boolean isInLeaderDepot);
    void displayResourcesToStore(List<Resource> resourcesToStore);

    //SETUP
    void loadLeaderCards(List<LightLeaderCard> leaderCards);
    void displayChooseResourceTypeRequest(List<Resource> resourceTypes, int quantity);
    void loadDevelopmentCards(List<LightDevelopmentCard> lightDevelopmentCards);
    void displayMessage(String message);
    void loadDevelopmentCardGrid(List<Integer> availableCardsIds);

    //Update MatchDataInfo
    void setNicknames(String playerNickname, List<String> otherPlayersNicknames);
    void update(MatchDataMessage message);
    void displayStandardView();

    //END
    void displayResults(Map<String, Integer> results, boolean readyForAnotherGame);
    void displayResults(int victoryPoints);

    //DISCONNECTIONS
    void displayDisconnection(String nickname, boolean setUp, boolean gameCancelled);
    void displayWelcomeBackMessage(String nickname, boolean gameFinished);



}
