package it.polimi.ingsw.common;


import it.polimi.ingsw.enumerations.ActionType;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toClient.matchData.MatchDataMessage;
import it.polimi.ingsw.model.cards.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ViewInterface {
    //LOBBY

    /**
     * Method to display the request of the game mode
     */
    void displayGameModeRequest();

    /**
     * Method to ask the nickname
     * @param isRetry true if it's not the first time that the nickname is asked
     * @param alreadyTaken true if someone else has already this nickname
     */
    void displayNicknameRequest(boolean isRetry, boolean alreadyTaken);

    /**
     * Method to ask the desired number of players for next match
     */
    void displayNumberOfPlayersRequest();

    /**
     * Method to notify the waiting before starting the match
     */
    void displayWaitingInTheLobbyMessage();

    /**
     * Method to notify all the players are ready to play
     * @param nicknames
     */
    void displayPlayersReadyToStartMessage(List<String> nicknames);
    //CONNECTION

    /**
     * Method to notify that the time to send a response is over
     */
    void displayTimeoutExpiredMessage();
    //ACTIONS

    /**
     * Method to ask which action the player wants to perform
     * @param executableActions all the executable action
     * @param standardActionDone true if a standard action has already been performed
     */
    void displayChooseActionRequest(Map<ActionType, Boolean> executableActions, boolean standardActionDone);

    /**
     * Method to ask where to insert the slide marble in the market
     */
    void displayMarbleInsertionPositionRequest();

    //TODO: marbles parameter
    /**
     * Method to ask which converison effect to use
     * @param marbles TODO
     * @param numberOfMarbles the quantity of marbles to be converted
     */
    void displayChooseWhiteMarbleConversionRequest(List<Resource> marbles, int numberOfMarbles);

    /**
     * Method to display the marbles taken from the market
     * @param marblesTaken the marbles taken from the market
     * @param needToChooseConversion true if a white marble effect is active
     */
    void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion);

    /**
     * Method to choose the storage for a resource
     * @param resource the resource to store
     * @param availableDepots the depots available for that resource
     * @param canDiscard true if the resource can be discarded
     * @param canReorganize true if the reorganization is possible
     */
    void displayChooseStorageTypeRequest(Resource resource, List<String> availableDepots, boolean canDiscard, boolean canReorganize);

    /**
     * Method to manage the reorganization
     * @param depots the depots where a reorganization can be performed
     * @param first true if it's the first attempt
     * @param failure true if a previous reorganization failed
     * @param availableLeaderResources the resources present in leader depots, if any
     */
    void displayReorganizeDepotsRequest(List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResources);

    /**
     * Method to choose leader cards
     * @param leaderCards the leader cards from which the player has to choose
     */
    void displayChooseLeaderCardsRequest(List<Integer> leaderCards);

    /**
     * Method to perform the selection of a card
     * @param CardsIDs the ids of the cards
     * @param leaderORdevelopment true if the cards are leader cards
     */
    void displaySelectCardRequest(List<Integer> CardsIDs, boolean leaderORdevelopment);

    /**
     * Method to manage the choice of production powers
     * @param availableProductionPowers the available production powers
     * @param availableResources the available resources of the player
     */
    void displayChooseProductionPowersRequest(Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources);

    /**
     * Method to ask in which slot a card has to be stored
     * @param firstSlotAvailable true if the first slot is available
     * @param secondSlotAvailable true if the second slot is available
     * @param thirdSlotAvailable true if the third slot is available
     */
    void displaySelectDevelopmentCardSlotRequest(boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable);

    /**
     * Metod to ask from which depots a resource has to be taken
     * @param resource the resource to remove
     * @param isInWarehouse true if the resource is present in the warehouse
     * @param isInStrongbox true if the resource is present in the strongbox
     * @param isInLeaderDepot true if the resource is present in the leader depot
     */
    void displaySelectStorageRequest(Resource resource, boolean isInWarehouse, boolean isInStrongbox, boolean isInLeaderDepot);

    /**
     * Method to store the resources
     * @param resourcesToStore list of resources to store
     */
    void displayResourcesToStore(List<Resource> resourcesToStore);

    /**
     * Method to visualize the action made by lorenzo
     * @param id
     */
    void displayLorenzoAction(int id);

    /**
     * Method to display the production card selectable
     * @param IDs the available productions
     * @param basicProduction the basic production power
     */
    void displayProductionCardYouCanSelect(List<Integer> IDs, List<Value> basicProduction);

    /**
     * Method to display the already seected production powers
     * @param availableProductionIDs the id of the productions
     * @param availableResources the available resources of the player
     * @param addORremove true if the player wants to add a production, false if he wants to remove it
     */
    void displayChooseProduction(List<Integer> availableProductionIDs, Map<Resource, Integer> availableResources, boolean addORremove);

    /**
     * Method to display the already selected productions
     * @param productionIDs the ids of the cards
     * @param basicProduction the basic production power
     */
    void displayCurrentSelectedProductions(Set<Integer> productionIDs, List<Value> basicProduction);

    /**
     * Method to ask the player if he wants to confirm the productions chosen, if he want to remove one or more
     * productions already chosen or if he wants to select another production
     */
    void chooseNextProductionAction();


    //SETUP

    /**
     * Method to load all the leader cards into match data
     * @param leaderCards the leader cards to load
     */
    void loadLeaderCards(List<LightLeaderCard> leaderCards);

    /**
     * Method to ask the initial resource/s type
     * @param resourceTypes the resource tho choose from
     * @param quantity the quantity to choose
     */
    void displayChooseResourceTypeRequest(List<Resource> resourceTypes, int quantity);

    /**
     * Method to load all the development cards into match data
     * @param lightDevelopmentCards the development cards to load
     */
    void loadDevelopmentCards(List<LightDevelopmentCard> lightDevelopmentCards);

    /**
     * Metod to display a generic text message
     * @param message
     */
    void displayMessage(String message);

    /**
     * Method to load the development card grid into match data
     * @param availableCardsIds the visible card ids of the grid
     */
    void loadDevelopmentCardGrid(List<Integer> availableCardsIds);

    //Update MatchDataInfo

    /**
     * Method to set the players nicknames
     * @param playerNickname the nickname of the client receiving the message
     * @param otherPlayersNicknames the nicknames of the other players
     */
    void setNicknames(String playerNickname, List<String> otherPlayersNicknames);

    /**
     * Method to analyze all the incoming {@link MatchDataMessage}
     * @param message the message received
     */
    void update(MatchDataMessage message);

    /**
     * Method to display the CLI view
     */
    void displayStandardView();

    /**
     * Method to set that a massive reloading of information is going on
     * @param reloading true if the reloading is going on
     */
    void setIsReloading(boolean reloading);

    //END

    /**
     * Method to display results at the end of the match
     * @param results a map with name and points of each player
     * @param readyForAnotherGame true if the players are ready to play again
     */
    void displayResults(Map<String, Integer> results, boolean readyForAnotherGame);

    /**
     * Method to display the results in single player mode
     * @param victoryPoints the score of the player
     */
    void displayResults(int victoryPoints);

    //DISCONNECTIONS

    /**
     * Method to inform a disconnection
     * @param nickname the nickname of the player
     * @param setUp //true if disconnection was in setup phase, false otherwise
     * @param gameCancelled true if the game has been cancelled
     */
    void displayDisconnection(String nickname, boolean setUp, boolean gameCancelled);

    /**
     * Method to welcome back a player after a reconnection
     * @param nickname the nickname of the player reconnected
     * @param gameFinished true if the game has finished before the reconnection of the player
     */
    void displayWelcomeBackMessage(String nickname, boolean gameFinished);


    void handleCloseConnection(boolean wasConnected);


}
