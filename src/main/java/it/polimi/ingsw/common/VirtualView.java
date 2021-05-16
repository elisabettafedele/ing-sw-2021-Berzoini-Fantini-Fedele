package it.polimi.ingsw.common;


import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.enumerations.ActionType;
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

    //SETUP
    void loadLeaderCards(List<LightLeaderCard> leaderCards);
    void displayChooseResourceTypeRequest(List<Resource> resourceTypes, int quantity);

    void loadDevelopmentCards(List<LightDevelopmentCard> lightDevelopmentCards);
    void displayMessage(String message);



    //Update MatchDataInfo
    void updateFaithTrackInfo(String nickname, int steps);
    void setNicknames(String playerNickname, List<String> otherPlayersNicknames);


    void displayDepotStatus(List<Resource>[] warehouseDepots, List<Resource>[] strongboxDepots, List<List<Resource>> leaderDepots);


    //END
    void displayResults(Map<String, Integer> results);
    void displayResults(int victoryPoints);

}
