package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.View;

import it.polimi.ingsw.client.cli.graphical.Colour;
import it.polimi.ingsw.client.cli.graphical.GraphicalLogo;
import it.polimi.ingsw.client.cli.graphical.Screen;
import it.polimi.ingsw.client.cli.specificCLI.*;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.messages.toClient.TurnMessage;
import it.polimi.ingsw.messages.toClient.matchData.MatchDataMessage;
import it.polimi.ingsw.model.cards.Value;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class CLI implements View {

    private Client client;
    private Thread inputObserverOutOfTurn;
    private AtomicBoolean myTurn = new AtomicBoolean(false);

    public static void main(String[] args) {
        CLI cli= new CLI();
        cli.init();
    }

    private void init(){
        GraphicalLogo.printLogo();
        MatchData.getInstance().setView(this);
        client = LobbyCLI.askConnectionParameters(this);
        client.start();
    }

    @Override
    public void displayTimeoutExpiredMessage() {
        System.out.println("Timeout expired");
        //boolean reconnect = getBoolean("Timeout expired, do you want to reconnect? y | n");
        client.closeSocket();
        // if (reconnect)
        // client.start();
    }

    @Override
    public void update(MatchDataMessage message) {
        MatchData.getInstance().update(message);
        if (message instanceof TurnMessage)
            update((TurnMessage) message);
        //TODO update of views
    }

    public void update(TurnMessage message){
        MatchData.getInstance().update(message);
        if (MatchData.getInstance().getThisClientNickname().equals(message.getNickname())) {
            MatchData.getInstance().setCurrentViewNickname(MatchData.getInstance().getThisClientNickname());
            System.out.println((message).isStarted() ? "It's your turn! " : "Turn ended ");
            myTurn.set(message.isStarted());
            if (message.isStarted()) {
                if (inputObserverOutOfTurn != null && inputObserverOutOfTurn.isAlive())
                    this.inputObserverOutOfTurn.interrupt();
            } else {
                this.inputObserverOutOfTurn = new Thread(this::outOfTurnInput);
                System.out.println("From now on you can use the command -pb to move to another player's view (EG. -pb betti shows you the view of the player named \"betti\")");
                inputObserverOutOfTurn.start();
            }
        } else {
            if (inputObserverOutOfTurn == null || !inputObserverOutOfTurn.isAlive()) {
                this.inputObserverOutOfTurn = new Thread(this::outOfTurnInput);
                inputObserverOutOfTurn.start();
            }
            System.out.println(message.getNickname() + ((message).isStarted() ? " started " : " ended ") + "his turn");
        }
    }

    private void analyzeOutOfTurnMessage(String input) {
        if (input != null && input.contains("-pb") && (MatchData.getInstance().getThisClientNickname().contains(input.substring(4)) || MatchData.getInstance().getOtherClientsNicknames().contains(input.substring(4)))) {
            MatchData.getInstance().setCurrentViewNickname(input.substring(4));
            displayStandardView();
        }

    }

    private void outOfTurnInput(){
        while (!myTurn.get()) {
            String line = InputParser.getLine();
            analyzeOutOfTurnMessage(line);
        }
    }

    @Override
    public void displayResults(Map<String, Integer> results, boolean readyForAnotherGame) {
        int i = 1;
        for (String name : results.keySet()){
            System.out.println((results.keySet().size() > 1 ? (i++ + ". ") : "")+ name + ": " + results.get(name) + " victory points");
        }
        if (!readyForAnotherGame)
            client.closeSocket();
        else
            System.out.println("You can now start another game!");
    }

    @Override
    public void displayResults(int victoryPoints) {
        System.out.println("Game over, you got " + victoryPoints + " victory points!");
        client.closeSocket();
    }

    @Override
    public void displayDisconnection(String nickname, boolean setUp, boolean gameCancelled) {
        System.out.println(Colour.ANSI_BRIGHT_GREEN.getCode() + "We are sorry to inform you that " + nickname + " has left the game." );
        System.out.println("The game" + (gameCancelled? " has been cancelled." : " will go on skipping the turns of that player."+ Colour.ANSI_RESET));
        if (setUp && gameCancelled){
            System.out.println(Colour.ANSI_BRIGHT_GREEN.getCode() + "You have been reconnected to the main lobby...\nBe ready to start another game!\n" + Colour.ANSI_RESET);
        }
        if (setUp && !gameCancelled)
            System.out.println("We are sorry, but since the game's size has been reduced, a part of the set up phase will be repeated. \nHowever, you will not be asked your leader cards' choices again");
    }

    @Override
    public void displayWelcomeBackMessage(String nickname, boolean gameFinished) {
        System.out.println("Welcome back " + nickname + (gameFinished ? "!\nThe game you were playing in is finished, we are loading the results for you..." : ".\nYou have to finish an old game, we are logging you in the room..."));
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void loadDevelopmentCardGrid(List<Integer> availableCardsIds) {
        MatchData.getInstance().loadDevelopmentCardGrid(availableCardsIds);
    }

    // *********************************************************************  //
    //                             LOBBY CLI                                  //
    // *********************************************************************  //


    @Override
    public void displayNicknameRequest(boolean isRetry, boolean alreadyTaken) {
        LobbyCLI.displayNicknameRequest(client, isRetry, alreadyTaken);
    }

    @Override
    public void displayGameModeRequest() {
        LobbyCLI.displayGameModeRequest(client);
    }

    @Override
    public void displayNumberOfPlayersRequest() {
        LobbyCLI.displayNumberOfPlayersRequest(client);
    }

    @Override
    public void displayWaitingInTheLobbyMessage() {
        LobbyCLI.displayWaitingInTheLobbyMessage();
    }

    @Override
    public void displayPlayersReadyToStartMessage(List<String> nicknames) {
        LobbyCLI.displayPlayersReadyToStartMessage(nicknames);
    }



    // *********************************************************************  //
    //                             SETUP CLI                                  //
    // *********************************************************************  //

    @Override
    public void loadLeaderCards(List<LightLeaderCard> leaderCards){
        MatchData.getInstance().setAllLeaderCards(leaderCards);
    }

    public void loadDevelopmentCards(List<LightDevelopmentCard> lightDevelopmentCards) {
        MatchData.getInstance().setAllDevelopmentCards(lightDevelopmentCards);
    }

    @Override
    public void displayChooseLeaderCardsRequest(List<Integer> leaderCardsIDs) {
        SetUpCLI.displayChooseLeaderCardsRequest(client, leaderCardsIDs);
    }

    @Override
    public void displayChooseResourceTypeRequest(List<Resource> resourceTypes, int quantity) {
        SetUpCLI.displayChooseResourceTypeRequest(client, resourceTypes, quantity);
    }

    // *********************************************************************  //
    //                          CHOOSE ACTION CLI                             //
    // *********************************************************************  //

    @Override
    public void displayChooseActionRequest(Map<ActionType, Boolean> executableActions, boolean standardActionDone) {
        myTurn.set(true);
        ChooseActionCLI.displayChooseActionRequest(client, executableActions, standardActionDone);
    }

    // *********************************************************************  //
    //                   TAKE RESOURCES FROM MARKET CLI                       //
    // *********************************************************************  //

    @Override
    public void displayMarbleInsertionPositionRequest() {
        TakeResourcesFromMarketCLI.displayMarbleInsertionPositionRequest(client);
    }

    @Override
    public void displayChooseWhiteMarbleConversionRequest(List<Resource> resources, int numberOfMarbles) {
        TakeResourcesFromMarketCLI.displayChooseWhiteMarbleConversionRequest(client, resources, numberOfMarbles);
    }

    @Override
    public void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion) {
        TakeResourcesFromMarketCLI.displayMarblesTaken(marblesTaken, needToChooseConversion);
    }

    // *********************************************************************  //
    //                         ORGANIZE DEPOTS CLI                            //
    // *********************************************************************  //

    @Override
    public void displayChooseStorageTypeRequest(Resource resource, List<String> availableDepots, boolean canDiscard, boolean canReorganize) {
        OrganizeDepotsCLI.displayChooseStorageTypeRequest(client, resource, availableDepots, canDiscard, canReorganize);
    }

    @Override
    public void displaySelectStorageRequest(Resource resource, boolean isInWarehouse, boolean isInStrongbox, boolean isInLeaderDepot) {
        OrganizeDepotsCLI.displaySelectStorageRequest(client, resource, isInWarehouse, isInStrongbox, isInLeaderDepot);
    }

    @Override
    public void displayResourcesToStore(List<Resource> resourcesToStore) {
        OrganizeDepotsCLI.displayResourcesToStore(resourcesToStore);
    }

    @Override
    public void displayReorganizeDepotsRequest(List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResources){
       OrganizeDepotsCLI.displayReorganizeDepotsRequest(client, depots, first, failure, availableLeaderResources);
    }

    // *********************************************************************  //
    //                            PRODUCTION CLI                              //
    // *********************************************************************  //

    @Override
    public void displayChooseProductionPowersRequest(Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources) {
        ProductionCLI.displayChooseProductionPowersRequest(client, availableProductionPowers, availableResources);
    }

    // *********************************************************************  //
    //                              CARDS CLI                                 //
    // *********************************************************************  //

    @Override
    public void displaySelectDevelopmentCardSlotRequest(boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable) {
        CardsCLI.displaySelectDevelopmentCardSlotRequest(client, firstSlotAvailable, secondSlotAvailable, thirdSlotAvailable);
    }

    @Override
    public void displaySelectCardRequest(List<Integer> cardsIDs,boolean leaderORdevelopment) {
        CardsCLI.displaySelectCardRequest(client, cardsIDs, leaderORdevelopment);
    }

    // *********************************************************************  //
    //                             MATCHDATA UPDATE                           //
    // *********************************************************************  //


    public void setNicknames(String playerNickname, List<String> otherPlayersNicknames){
        MatchData.getInstance().setThisClient(playerNickname);
        for(String nickname : otherPlayersNicknames){
            MatchData.getInstance().addLightClient(nickname);
        }
    }

    @Override
    public void displayStandardView() {
        Screen.getInstance().setClientToDisplay(MatchData.getInstance().getCurrentViewNickname());
        Screen.getInstance().displayStandardView();
    }

    // *********************************************************************  //
    //                               PREDICATES                               //
    // *********************************************************************  //

    public static Predicate<Integer> conditionOnIntegerRange(int min, int max){
        return p -> p >= min && p <= max;
    }

    public static Predicate<Integer> conditionOnInteger(List<Integer> list){
        return list::contains;
    }

    public static Predicate<String> conditionOnString(List<String> list){
        return list::contains;
    }
}
