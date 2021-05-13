package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.messages.toServer.*;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Value;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.function.Predicate;

public class CLI implements View {

    private final int BASIC_PRODUCTION_POWER = 0;
    private Client client;

    public static void main(String[] args) {
        CLI cli= new CLI();
        cli.init();
    }

    private void init(){
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
    public void loadLeaderCards(List<LeaderCard> leaderCards){
        MatchData.getInstance().setAllLeaderCards(leaderCards);
    }

    public void loadDevelopmentCards(Map<Integer, List<String>> lightDevelopmentCards) {
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
    public void displayChooseStorageTypeRequest(Resource resource, List<String> availableDepots, boolean setUpPhase) {
        OrganizeDepotsCLI.displayChooseStorageTypeRequest(client, resource, availableDepots, setUpPhase);
    }
    @Override
    public void displayReorganizeDepotsRequest(List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResources){
       OrganizeDepotsCLI.displayReorganizeDepotsRequest(client, depots, first, failure, availableLeaderResources);
    }

    //TODO finish organization of CLI!!

    @Override
    public void displaySelectCardRequest(List<Integer> CardsIDs,boolean leaderORdevelopment) {
        System.out.println("Select a card:");
        for (Integer id : CardsIDs){
            if(leaderORdevelopment){
                System.out.printf("%d. %s \n", id, MatchData.getInstance().getLeaderCardByID(id));
            }
            else{ System.out.printf("%d. %s \n", id, MatchData.getInstance().getDevelopmentCardByID(id));
            }

        }
        System.out.print("Insert the ID of the card you want to select: ");
        Integer selection = InputParser.getInt("Error: the ID provided is not available. Provide a valid ID", conditionOnInteger(CardsIDs));
        client.sendMessageToServer( new SelectCardResponse(selection));
    }

    @Override
    public void displayChooseProductionPowersRequest(Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources) {
        boolean confirmed = false;
        boolean wantsToRemove = false;
        Map<Integer, List<Value>> selectedProductions = new HashMap<>();
        List<Value> actualChosenProduction = null;

        do{
            if(!wantsToRemove){
                List<Integer> IDs = displayAvailableProductions(availableProductionPowers, availableResources);
                if(IDs.size() > 0){
                    System.out.print("Insert the number of the production you want to activate: ");
                    Integer selection = InputParser.getInt(
                            "Error: the ID provided is not available. Provide a valid ID: ", conditionOnInteger(IDs));
                    if(selection == BASIC_PRODUCTION_POWER){
                        actualChosenProduction = manageBasicProductionPower(availableResources);
                    }else{
                        actualChosenProduction = availableProductionPowers.get(selection);
                    }
                    selectedProductions.put(selection, actualChosenProduction);
                    availableProductionPowers.remove(selection);
                    subtractResources(actualChosenProduction.get(0), availableResources);
                    //delete the production chosen from the available and save it in another list

                }else{
                    System.out.println("You don't have enough resources for any production, do you want to buy resources for 0.99€?");
                }
            }else{
                manageRemoveProduction(availableProductionPowers, selectedProductions, availableResources);
            }

            System.out.println("Your current selections are:");
            for(Map.Entry<Integer, List<Value>> entry : selectedProductions.entrySet()){
                System.out.println(entry.getKey() + ", " + entry.getValue());
            }
            System.out.printf("What do you want to do:\n1. Select another production\n" +
                    "2. Remove an already chosen production\n3. Confirm your list of production(s)\n");
            Integer selection = InputParser.getInt(
                    "Error: the ID provided is not available. Provide a valid ID", conditionOnIntegerRange(1, 3));
            if(selection == 3){
                confirmed = true;
            }else if(selection == 2){
                wantsToRemove = true;
            }else{
                wantsToRemove = false;
                confirmed = false; //Useless but leave it here for now
            }
        }while(!confirmed);
        List<Integer> productionPowersSelected= new ArrayList<>(selectedProductions.keySet());
        if(productionPowersSelected.contains(BASIC_PRODUCTION_POWER)){
            client.sendMessageToServer(new ChooseProductionPowersResponse(productionPowersSelected, selectedProductions.get(BASIC_PRODUCTION_POWER)));
        }
        client.sendMessageToServer(new ChooseProductionPowersResponse(productionPowersSelected)); //If the player confirms with zero selections don't increment the actionDone variable!!!
    }

    private void manageRemoveProduction(Map<Integer, List<Value>> availableProductionPowers,
                                        Map<Integer, List<Value>> selectedProductions,
                                        Map<Resource, Integer> availableResources) {
        List<Integer> selectedIDs = new ArrayList<>();
        System.out.println("Your current productions are:");
        for(Map.Entry<Integer, List<Value>> entry : selectedProductions.entrySet()){
            System.out.println(entry.getKey() + ", " + entry.getValue());
            selectedIDs.add(entry.getKey());
        }
        System.out.print("Select the production ID you want to remove: ");
        Integer selection = InputParser.getInt(
                "Error: the ID provided is not available. Provide a valid ID", conditionOnInteger(selectedIDs));
        //Re-adding the selected production to the available ones
        if(selection == BASIC_PRODUCTION_POWER){
            Map<Resource, Integer> cost = new HashMap<>();
            cost.put(Resource.ANY, 2);
            Map<Resource, Integer> output = new HashMap<>();
            output.put(Resource.ANY, 1);
            List<Value> basic_production = new ArrayList<>();
            try {
                basic_production.add(new Value(null, cost, 0));
                basic_production.add(new Value(null, output, 0));
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
            availableProductionPowers.put(BASIC_PRODUCTION_POWER, basic_production);
        }else{
            availableProductionPowers.put(selection, selectedProductions.get(selection));
        }

        Map<Resource, Integer> activationCost = null;
        try {
            activationCost = selectedProductions.get(selection).get(0).getResourceValue();
        } catch (ValueNotPresentException e) {
            e.printStackTrace();
        }
        //Removing the selected production from the chosen ones
        selectedProductions.remove(selection);
        //Re-adding the activation cost resources of the production removed to the available resources
        for(Map.Entry<Resource, Integer> entry : activationCost.entrySet()){
            availableResources.put(entry.getKey(), availableResources.get(entry.getKey()) + entry.getValue());
        }
    }

    private List<Value> manageBasicProductionPower(Map<Resource, Integer> availableResources) {
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
                        conditionOnIntegerRange(1, usableResources.size()));
                //Adding the chosen resource to the chosenResources List
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
                    conditionOnIntegerRange(1, realValues.size()));
            chosenResources.add(realValues.get(selection - 1));
        }else{
            System.out.println("You don't have enough resources for this production");
        }

        //TODO: do directly the chosenResource.add() above with the following variables.
        List<Value> production= new ArrayList<>();
        Map<Resource, Integer> productionCost = new HashMap<>();
        productionCost.put(chosenResources.get(0), 1);
        if(chosenResources.get(0).equals(chosenResources.get(1))){
            productionCost.put(chosenResources.get(0), 2);
        }else{
            productionCost.put(chosenResources.get(1), 1);
        }

        Map<Resource, Integer> productionOutput = new HashMap<>();
        productionOutput.put(chosenResources.get(2), 1);

        try {
            production.add(new Value(null, productionCost, 0));
            production.add(new Value(null, productionOutput, 0));
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }

        return production;
    }

    private void subtractResources(Value activationCost, Map<Resource, Integer> availableResources){
        Map<Resource, Integer> resourceToBeRemoved = null;
        try {
            resourceToBeRemoved = activationCost.getResourceValue();
        } catch (ValueNotPresentException e) {
            e.printStackTrace();
        }
        for(Map.Entry<Resource, Integer> entry : resourceToBeRemoved.entrySet()){
            availableResources.put(entry.getKey(), availableResources.get(entry.getKey()) - 1);
            /*if(availableResources.get(entry.getKey()) == 0){
                availableResources.remove(entry.getKey());
            }*/
        }
    }

    //check
    private List<Integer> displayAvailableProductions(Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources){
        List <Integer> availableProductionIDs = new ArrayList<>();
        for(Map.Entry<Integer, List<Value>> entry : availableProductionPowers.entrySet()){
            Map<Resource, Integer> activationCost = null;
            try {
                activationCost = entry.getValue().get(0).getResourceValue();
            } catch (ValueNotPresentException e) {
                //skip
            }
            if(hasResourcesForThisProduction(activationCost, availableResources) && entry.getKey() != 0){
                System.out.println(MatchData.getInstance().getDevelopmentCardByID(entry.getKey()).get(0));
                availableProductionIDs.add(entry.getKey());
            }
        }
        if(availableResources.values().stream().mapToInt(Integer::intValue).sum() >= 2){
            System.out.println("0. Basic Production Power: " + availableProductionPowers.get(0));
            availableProductionIDs.add(BASIC_PRODUCTION_POWER);
        }
        return availableProductionIDs;
    }

    private boolean hasResourcesForThisProduction(Map<Resource, Integer> activationCost, Map<Resource, Integer> availableResources){
        boolean executable = true;
        for (Map.Entry<Resource, Integer> entry : activationCost.entrySet()){
            try {
                executable = executable && entry.getValue() <= availableResources.get(entry.getKey());
            }catch(Exception e){
                //do nothing, it's only to easily skip a missing Resource in availableResources
            }
        }
        return executable;
    }
    @Override
    public void displaySelectDevelopmentCardSlotRequest(boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable) {
        int selection = -1;
        System.out.println("Select a development card slot");
        if(firstSlotAvailable){
            System.out.println("Slot number 0 is available");
        }
        if(secondSlotAvailable){
            System.out.println("Slot number 1 is available");
        }
        if(thirdSlotAvailable){
            System.out.println("Slot number 2 is available");
        }
        boolean done=false;
        while(!done){
            selection = InputParser.getInt("Error: write a number.");
            if(selection>=0&&selection<3){
                if((selection==0&&firstSlotAvailable)||(selection==1&&secondSlotAvailable)||(selection==2&&thirdSlotAvailable)){
                    done=true;
                }
            }
            if(!done){
                System.out.println("Invalid choice");
            }
        }

        client.sendMessageToServer( new SelectDevelopmentCardSlotResponse(selection));
    }
    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displaySelectStorageRequest(Resource resource, boolean isInWarehouse, boolean isInStrongbox, boolean isInLeaderDepot) {
        while(true){
            System.out.println("You can take a " + resource.toString() + " from:");
            if(isInWarehouse){
                System.out.println("1-Warehouse");
            }
            if(isInStrongbox){
                System.out.println("2-Strongbox");
            }
            if(isInLeaderDepot){
                System.out.println("3-Leader Depot");
            }
            System.out.println("Where would you like to remove it? Select the relative number:");
            int selection = InputParser.getInt("Error: write a number.");
            if (selection==1&&isInWarehouse){
                client.sendMessageToServer( new SelectStorageResponse(resource,ResourceStorageType.WAREHOUSE));
                return;
            }
            if (selection==2&&isInStrongbox){
                client.sendMessageToServer(new SelectStorageResponse(resource,ResourceStorageType.STRONGBOX));
                return;
            }
            if (selection==3&&isInLeaderDepot){
                client.sendMessageToServer(new SelectStorageResponse(resource,ResourceStorageType.LEADER_DEPOT));
                return;
            }
            System.out.println("Incorrect choice");
        }
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

    public static Predicate<String> conditionOnString(List<String> lis){
        return lis::contains;
    }
}
