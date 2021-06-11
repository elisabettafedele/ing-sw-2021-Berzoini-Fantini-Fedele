package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.client.cli.graphical.Colour;
import it.polimi.ingsw.client.cli.graphical.GraphicalLogo;
import it.polimi.ingsw.client.cli.graphical.Screen;
import it.polimi.ingsw.client.cli.graphical.TokenDescriptors;
import it.polimi.ingsw.client.cli.specificCLI.*;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.client.utilities.UtilityProduction;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.messages.toClient.matchData.TurnMessage;
import it.polimi.ingsw.messages.toClient.matchData.MatchDataMessage;
import it.polimi.ingsw.model.cards.Value;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map;
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
        boolean error = true;
        boolean firstTry = true;
        GraphicalLogo.printLogo();
        MatchData.getInstance().setView(this);
        while (error) {
            client = LobbyCLI.askConnectionParameters(this, firstTry);
            try {
                client.start();
                error = false;
            } catch (IOException e) {
                firstTry = false;
            }
        }
    }

    @Override
    public void displayTimeoutExpiredMessage() {
        System.out.println("Timeout expired");
        //boolean reconnect = getBoolean("Timeout expired, do you want to reconnect? y | n");
        client.closeSocket();
        // if (reconnect)
        // client.start();
    }

    private void analyzeOutOfTurnMessage(String input) {
        if (input != null && input.contains("-pb") && (MatchData.getInstance().getThisClientNickname().contains(input.substring(4)) || MatchData.getInstance().getOtherClientsNicknames().contains(input.substring(4)))) {
            MatchData.getInstance().setCurrentViewNickname(input.substring(4));
            displayStandardView();
        }

    }

    private void outOfTurnInput(){
        while (!myTurn.get() && client.isConnected()) {
            String line = InputParser.getLine();
            if (line == null)
                return;
            analyzeOutOfTurnMessage(line);
        }
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // *********************************************************************  //
    //                           END GAME  & FA                               //
    // *********************************************************************  //

    @Override
    public void displayResults(Map<String, Integer> results, boolean readyForAnotherGame) {
        int i = 1;
        List<String> winners= new ArrayList<>();
        if (results.size() == 1){
            int points = -1;
            for (String name : results.keySet())
                points = results.get(name);
            displayResults(points);
        }
        else {
            int max =0;
            for (String name : results.keySet()) {
                System.out.println((results.keySet().size() > 1 ? (i++ + ". ") : "") + name + ": " + results.get(name) + " victory points");
                if(max<results.get(name)){
                    max=results.get(name);
                    winners.clear();
                    winners.add(name);
                }
                if(max==results.get(name)){
                    winners.add(name);
                }
            }
            System.out.print(winners.size()>1? "WINNERS: ":"WINNER : ");
            for(String winner:winners){
                System.out.print( "!! "+ winner + " !!");
            }
        }
        if (readyForAnotherGame)
            System.out.println("\nYou can now start another game!");
        else {
            client.closeSocket();
            //TODO askReconnect();
        }
    }

    @Override
    public void displayResults(int victoryPoints) {
        if (victoryPoints == -1)
            System.out.println("You lost against Lorenzo il Magnifico!");
        else
            System.out.println("You won with " + victoryPoints + " victory points!! \nCongratulations");
        client.closeSocket();
        //TODO askReconnect();
    }
/*
    public void askReconnect(){
        System.out.println("Do you want to play another game? y | n");
        while(true) {
            String wantToReconnect = InputParser.getLine();
            if (wantToReconnect == null)
                return;
            if (wantToReconnect.equals("y")) {
                try {
                    client.reconnect();
                } catch (IOException e) {
                    System.out.println(Colour.ANSI_BRIGHT_RED.getCode() + "We are sorry to inform you that the server is not available anymore\nTry again later" + Colour.ANSI_RESET);
                }
            }
            else if (wantToReconnect.equals("n"))
                return;
            else {
                System.out.println("Invalid choice: type y to start a new game or n to exit");
            }
        }
    }
*/
    @Override
    public void displayDisconnection(String nickname, boolean setUp, boolean gameCancelled) {
        System.out.println(Colour.ANSI_BRIGHT_GREEN.getCode() + "We are sorry to inform you that " + nickname + " has left the game." );
        System.out.println("The game" + (gameCancelled? " has been cancelled." : " will go on skipping the turns of that player.") + Colour.ANSI_RESET);
        if (gameCancelled){
            System.out.println(Colour.ANSI_BRIGHT_GREEN.getCode() + "You have been reconnected to the main lobby...\nBe ready to start another game. A game will start as soon as enough players will be ready\n" + Colour.ANSI_RESET);
        }
    }

    @Override
    public void displayWelcomeBackMessage(String nickname, boolean gameFinished) {
        System.out.println("Welcome back " + nickname + (gameFinished ? "!\nThe game you were playing in is finished, we are loading the results for you..." : ".\nYou have to finish an old game, we are logging you in the room..."));
    }

    @Override
    public void handleCloseConnection(boolean wasConnected) {
        if (!wasConnected)
            System.out.println(Colour.ANSI_BRIGHT_CYAN.getCode() + "The server is not reachable at the moment. Try again later." + Colour.ANSI_RESET);
        else
            System.out.println(Colour.ANSI_BRIGHT_GREEN.getCode() + "Connection closed" + Colour.ANSI_RESET);
        if (inputObserverOutOfTurn != null && inputObserverOutOfTurn.isAlive())
            inputObserverOutOfTurn.interrupt();
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
    //                          SINGLE PLAYER                                 //
    // *********************************************************************  //

    @Override
    public void displayLorenzoAction(int id) {
        System.out.println(Colour.ANSI_BLUE.getCode() + TokenDescriptors.valueOf(id).getDescription() + Colour.ANSI_RESET);
        //System.out.println("Lorenzo used token " + id);
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
        UtilityProduction.initialize(this, client, availableProductionPowers, availableResources);
        //ProductionCLI.displayChooseProductionPowersRequest(client, availableProductionPowers, availableResources);
    }

    @Override
    public void displayProductionCardYouCanSelect(List<Integer> IDs, List<Value> basicProduction){
        Screen.getInstance().displayCardSelection(IDs, basicProduction);
    }

    @Override
    public void displayChooseProduction(List<Integer> availableProductionIDs, Map<Resource, Integer> availableResources, boolean addORremove ) {
        if(availableProductionIDs.size() == 0){
            System.out.println("You can't choose any other production at the moment. ");
            chooseNextProductionAction();
            return;
        }
        if(addORremove)
            System.out.print("Insert the number of the production you want to activate: ");
        else
            System.out.print("Insert the number of the production you want to eliminate: ");
        Integer selection = InputParser.getInt(
                "Error: the ID provided is not available. Provide a valid ID: ", CLI.conditionOnInteger(availableProductionIDs));
        if (selection == null)
            return;
        if(addORremove){
            if(selection == 0){
                createBasicProduction(availableResources);
            }else if(selection >= 61) {
                chooseLeaderCardProductionPower(availableResources, selection);
            }else{
                UtilityProduction.addProductionPower(selection);
            }
        }
        else{
            UtilityProduction.removeProduction(selection);
        }
    }

    private void chooseLeaderCardProductionPower(Map<Resource, Integer> availableResources, int id) {
        //TODO: merge this duplicates lines that are also in createBasicProduction
        List<Resource> realValues = Resource.realValues();
        System.out.println("Choose the resource you want to produce");
        for(int k = 0; k < realValues.size(); k++){
            System.out.printf("%d. " + realValues.get(k) +"\n", k+1);
        }
        //Selecting the desired resource
        Integer selection = InputParser.getInt(
                "Error: the given number is not present in the list. Provide a valid number",
                CLI.conditionOnIntegerRange(1, realValues.size()));

        UtilityProduction.manageLeaderProductionPower(realValues.get(selection - 1), id);
    }

    private void createBasicProduction(Map<Resource, Integer> availableResources){
        List<Resource> usableResources = new ArrayList<Resource>();
        List<Resource> chosenResources = new ArrayList<Resource>();
        //Saving in usableResources which Resource has a quantity > 0
        for(Map.Entry<Resource, Integer> entry : availableResources.entrySet()){
            if(entry.getValue() > 0){
                usableResources.add(entry.getKey());
            }
        }
        if(usableResources.size() > 0){
            for(int j = 0; j < 2; j++){
                System.out.print("Choose the");
                if(j == 0){
                    System.out.print(" first");
                }else{
                    System.out.print(" second");
                }
                System.out.println(" resource to be used in the basic production power");
                //Displaying the usableResources for the basic production power
                for(int i = 0; i < usableResources.size(); i++) {
                    System.out.printf("%d. " + usableResources.get(i) + " \n", i+1);
                }
                //Selecting the resource to be used for the basic production power
                Integer selection = InputParser.getInt(
                        "Error: the given number is not present in the list. Provide a valid number",
                        CLI.conditionOnIntegerRange(1, usableResources.size()));
                //Adding the chosen resource to the chosenResources List
                if (selection == null)
                    return;
                chosenResources.add(usableResources.get(selection - 1));
                //If that Resource type had quantity equal to 1 it is removed from the usableResources list
                if(availableResources.get(usableResources.get(selection - 1)) <= 1){
                    usableResources.remove(usableResources.get(selection-1));
                }
            }

            //displaying the resources that can be produced
            List<Resource> realValues = Resource.realValues();
            System.out.println("Choose the resource you want to produce");
            for(int k = 0; k < realValues.size(); k++){
                System.out.printf("%d. " + realValues.get(k) +"\n", k+1);
            }
            //Selecting the desired resource
            Integer selection = InputParser.getInt(
                    "Error: the given number is not present in the list. Provide a valid number",
                    CLI.conditionOnIntegerRange(1, realValues.size()));
            if (selection == null)
                return;
            chosenResources.add(realValues.get(selection - 1));
            UtilityProduction.manageBasicProductionPower(chosenResources);
        }

    }

    public void displayCurrentSelectedProductions(Set<Integer> productionIDs, List<Value> basicProduction){
        System.out.println("Your current selections are:");
        List<Integer> IDs = new ArrayList<>();
        IDs.addAll(productionIDs);
        Screen.getInstance().displayCardSelection(IDs, basicProduction);
    }

    @Override
    public void chooseNextProductionAction() {
        System.out.printf("What do you want to do:\n1. Select another production\n" +
                "2. Remove an already chosen production\n3. Confirm your list of production(s)\n");
        Integer selection = InputParser.getInt(
                "Error: the ID provided is not available. Provide a valid ID", CLI.conditionOnIntegerRange(1, 3));
        if (selection == null)
            return;
        if(selection == 3){
            UtilityProduction.confirmChoices();
        }else if(selection == 2){
            UtilityProduction.chooseProductionToRemove();
        }else{
            UtilityProduction.displayAvailableProductions();
        }
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
                if (!MatchData.getInstance().getOtherClientsNicknames().isEmpty())
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

    public void setNicknames(String playerNickname, List<String> otherPlayersNicknames){
        MatchData.getInstance().setThisClient(playerNickname);
        MatchData.getInstance().resetOtherClients();
        for(String nickname : otherPlayersNicknames){
            MatchData.getInstance().addLightClient(nickname);
        }

        if (otherPlayersNicknames.size() == 0){
            MatchData.getInstance().setGameMode(GameMode.SINGLE_PLAYER);
        }else{
            MatchData.getInstance().setGameMode(GameMode.MULTI_PLAYER);
        }
    }

    @Override
    public void loadDevelopmentCardGrid(List<Integer> availableCardsIds) {
        MatchData.getInstance().loadDevelopmentCardGrid(availableCardsIds);
    }

    @Override
    public void displayStandardView() {
        Screen.getInstance().setClientToDisplay(MatchData.getInstance().getCurrentViewNickname());
        Screen.getInstance().displayStandardView();
    }

    @Override
    public void setIsReloading(boolean reloading){
        MatchData.getInstance().setReloading(reloading);
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
