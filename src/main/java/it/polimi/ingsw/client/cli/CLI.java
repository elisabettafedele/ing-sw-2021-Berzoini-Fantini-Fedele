package it.polimi.ingsw.client.cli;


import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.client.utilities.UtilityPrinter;
import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.messages.toServer.*;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Value;

import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CLI implements View {

    private static final String DEFAULT_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_PORT = 1234;
    private final int BASIC_PRODUCTION_POWER = 0;

    private String IPAddress;
    private int port;

    private Client client;

    public static void main(String[] args) {
        CLI cli= new CLI();
        cli.init();
    }

    private void init(){
        askConnectionParameters();
        client = new Client(IPAddress, port, this);
        client.start();
    }

    private void askConnectionParameters(){
        int port;
        String IPAddress;
        boolean firstTry = true;
        //Insert IP address
        do{
            if(firstTry)
                System.out.println("Enter the server's IP address or d (default configuration): ");
            else
                System.out.println("Invalid IP address: enter x.x.x.x where x is called an octet and must be a decimal value between 0 and 255. Enter d for default configuration: ");
            IPAddress = InputParser.getLine();
            firstTry = false;
            if (IPAddress.toLowerCase().equals("d")){
                this.IPAddress = DEFAULT_ADDRESS;
                this.port = DEFAULT_PORT;
                return;
            }
        }while(!Utils.IPAddressIsValid(IPAddress));
        firstTry = true;
        this.IPAddress = IPAddress;
        //Insert port number
        do{
            if(firstTry)
                System.out.println("Enter the port you want to connect to: ");
            else
                System.out.println("Invalid port number: enter an integer between 1024 and 65535");
            firstTry = false;
            port = InputParser.getInt("Invalid port number: enter an integer between 1024 and 65535", conditionOnIntegerRange(1024, 65535));
        }while (!Utils.portIsValid(port));
        this.port = port;
    }

    @Override
    public void displayNicknameRequest(boolean isRetry, boolean alreadyTaken) {
        if (!isRetry)
            System.out.println("Insert your nickname");
        else if (!alreadyTaken)
            System.out.println("Your nickname was invalid, be sure to insert only valid characters (A-Z, a-z, 0-9)");
        else {
            System.out.println("Your nickname has already been taken, insert another one");
        }
        client.sendMessageToServer(new NicknameResponse(InputParser.getLine()));
    }

    @Override
    public void displayGameModeRequest() {
        System.out.println("Insert a game mode, multiplayer or solo mode: m | s");
        client.sendMessageToServer(new GameModeResponse(getGameMode()));
    }

    @Override
    public void displayNumberOfPlayersRequest(boolean isRetry) {
        if (isRetry)
            System.out.println("Invalid number of players: games can be of 2, 3 or 4 players");
        else {
            System.out.println("Insert desired number of players: 2, 3 or 4");
        }
        client.sendMessageToServer(new NumberOfPlayersResponse(InputParser.getInt("Invalid number of players: please insert an integer number between 2 and 4", conditionOnIntegerRange(2, 4))));
    }

    @Override
    public void displayWaitingInTheLobbyMessage() {
        System.out.println("Waiting in the lobby...");
    }

    @Override
    public void displayPlayersReadyToStartMessage(List<String> nicknames) {
        //TODO print nicknames
        System.out.println("All the players are ready to start, the game will start in a while...");
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
    public void displayMarbleInsertionPositionRequest() {
        System.out.println("Insert a marble insertion position (from 1 to 7) to insert the marble in the market trace: ");
        client.sendMessageToServer(new MarbleInsertionPositionResponse(InputParser.getInt("Invalid position: the position must be an integer from 1 to 7", conditionOnIntegerRange(1, 7))));
    }

    @Override
    public void displayChooseWhiteMarbleConversionRequest(List<Resource> resources, int numberOfMarbles) {
        System.out.println("You have these two possible white marble conversions: " + resources.get(0) + " | " + resources.get(1));
        List<Resource> resourcesChosen = new LinkedList<>();
        for (int i = 0; i < numberOfMarbles; i++){
            if (numberOfMarbles > 1)
                System.out.printf("White marble #%d\n", i+1);
            resourcesChosen.add(getMarbleColor(resources));
        }
        client.sendMessageToServer(new ChooseWhiteMarbleConversionResponse(resourcesChosen));
    }

    @Override
    public void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion) {
        System.out.println("These are the colors of the marbles you took from the market:");
        for (int i = 0; i < marblesTaken.size() - 1; i++)
            System.out.println(marblesTaken.get(i) + ", ");
        System.out.println(marblesTaken.get(marblesTaken.size()-1));
        if (marblesTaken.contains(Marble.WHITE)){
            if (!needToChooseConversion)
                System.out.println("White marbles will be automatically converted according to your leader card effects (if any)");
            else
                System.out.println("You have more than one White Marble Effect Active! You need to choose one white marble at a time how you want to convert it");
        }
    }

    @Override
    public void displayChooseStorageTypeRequest(Resource resource, List<String> availableDepots, boolean setUpPhase) {
        if (availableDepots.isEmpty())
            System.out.println("There are no available depots for " + resource);
        else {
            System.out.println("Choose a depot for the " + resource + "\nAvailable depots for this resource type are:");
            UtilityPrinter.printNumericList(availableDepots);
        }
        List<String> textCommands = new ArrayList<>();
        if (!setUpPhase) {
            System.out.println("Type d if you want to discard the resource or r if you want to reorganize your depots");
            textCommands.add(Command.DISCARD.command);
            textCommands.add(Command.REORGANIZE.command);
        }
        String choiceString = InputParser.getCommandFromList(textCommands, availableDepots);
        if (choiceString.equals(Command.DISCARD.command))
            client.sendMessageToServer(new DiscardResourceRequest(resource));
        else if (choiceString.equals(Command.REORGANIZE.command))
            client.sendMessageToServer(new ReorganizeDepotRequest());
        else
            client.sendMessageToServer(new ChooseStorageTypeResponse(resource, choiceString, setUpPhase));
    }


    @Override
    public void displayReorganizeDepotsRequest(List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResources){
        //Just temporary I want to make this check earlier
        if (depots.isEmpty()){
            System.out.println("You do not have any depot to reorganize");
            client.sendMessageToServer(new NotifyEndDepotsReorganization());
            return;
        }
        if (first)
            System.out.println("You can now reorganize your depots with the command" + Command.SWAP.command + " or " + Command.MOVE.command + "- "+ Command.SWAP + ": realizes a swap of two depots which contain different resource types\n- " + Command.MOVE + ": move a certain number of resources from one depot to another (be careful because the leader depots have a fixed resource type!\nIf you have finished type " + Command.END_REORGANIZE_DEPOTS.command);
        if (failure)
            System.out.println("Invalid reorganization: check the capacity and the type of the depots before reorganizing.");
        List<String> possibleCommands = Command.getReorganizeDepotsCommands();
        possibleCommands.addAll(depots);
        Resource resource = Resource.ANY;
        System.out.println("Do you want to swap or move your resources? Type " + Command.END_REORGANIZE_DEPOTS.command + " if you want to end the reorganization of your depots");
        System.out.println(Command.SWAP.command + " | " + Command.MOVE.command + " | " + Command.END_REORGANIZE_DEPOTS.command);
        String commandType = InputParser.getString("Please insert a valid command", conditionOnString(possibleCommands));
        if (commandType.equals(Command.END_REORGANIZE_DEPOTS.command)) {
            client.sendMessageToServer(new NotifyEndDepotsReorganization());
            return;
        }
        System.out.print("Select the origin depot: ");
        String originDepot = InputParser.getString("Please insert a valid depot", conditionOnString(depots));
        System.out.print("Select the destination depot: ");
        String destinationDepot = InputParser.getString("Please insert a valid depot", conditionOnString(depots));
        if (commandType.equals(Command.SWAP.command))
            client.sendMessageToServer(new SwapWarehouseDepotsRequest(originDepot,destinationDepot));
        else {
            System.out.print("Select the quantity of the resources you want to move: ");
            Integer quantity = InputParser.getInt("Please insert a valid resource quantity", conditionOnIntegerRange(1, 4));
            assert (quantity != null);
            if (availableLeaderResources.size() == 2 && originDepot.equals("LEADER_DEPOT")) {
                System.out.print("Select the type of resource you want to remove from the leader depot: ");
                resource = Resource.valueOf(InputParser.getString("Insert a valid resource type", conditionOnString(availableLeaderResources.stream().map(Enum::name).collect(Collectors.toList()))));
            }
            client.sendMessageToServer(new MoveResourcesRequest(originDepot, destinationDepot, resource, quantity));
        }
    }


    private GameMode getGameMode(){
        while(true) {
            String gameModeString = InputParser.getLine();
            if (gameModeString.equals("m"))
                return GameMode.MULTI_PLAYER;
            else if (gameModeString.equals("s"))
                return GameMode.SINGLE_PLAYER;
            else {
                System.out.println("Invalid game mode: type m for multiplayer mode or s for solo mode");
            }
        }
    }

    private boolean getBoolean(String message){
        String reconnectString;
        do {
            System.out.println(message);
            reconnectString = InputParser.getLine();
        } while (!reconnectString.equalsIgnoreCase("y")  && !reconnectString.equalsIgnoreCase("n"));
        return reconnectString.equalsIgnoreCase("y");

    }

    private Resource getMarbleColor(List<Resource> conversions){
        Resource resource = null;
        String resourceString;
        boolean error;
        do {
            System.out.println("Select one conversion: ");
            resourceString = InputParser.getLine();
            try {
                resource = Resource.valueOf(resourceString.toUpperCase());
                error = !conversions.contains(resource);
            }catch (IllegalArgumentException e){
                error = true;
                System.out.println("Error: insert a valid resource type");
            }
        } while (error);
        return resource;
    }

    @Override
    public void displayChooseLeaderCardsRequest(List<Integer> leaderCardsIDs) {
        System.out.println("Choose two Leader Cards to keep");
        for (Integer id : leaderCardsIDs){
            System.out.printf("%d. %s \n", id, MatchData.getInstance().getLeaderCardByID(id));
        }
        System.out.print("Insert the ID of the first leader card chosen: ");
        Integer firstChoice = InputParser.getInt("Error: the ID provided is not available. Provide a valid ID", conditionOnInteger(leaderCardsIDs));
        leaderCardsIDs.remove(firstChoice);
        MatchData.getInstance().addChosenLeaderCard(firstChoice);
        System.out.print("Insert the ID of the second leader card chosen: ");
        Integer secondChoice = InputParser.getInt("Error: the ID provided is not available. Provide a valid ID", conditionOnInteger(leaderCardsIDs));
        MatchData.getInstance().addChosenLeaderCard(secondChoice);
        leaderCardsIDs.remove(secondChoice);
        client.sendMessageToServer(new ChooseLeaderCardsResponse(leaderCardsIDs));
    }

    @Override
    public void displaySelectCardRequest(List<Integer> CardsIDs,boolean leaderORdevelopment) {
        System.out.println("Select a card");
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
    public void loadLeaderCards(List<LeaderCard> leaderCards){
        MatchData.getInstance().setAllLeaderCards(leaderCards);
    }

    @Override
    public void displayChooseResourceTypeRequest(List<Resource> resourceTypes, int quantity) {
        //TODO show choose resource type view
        List<String> resourcesToString = resourceTypes.stream().map(Enum::name).collect(Collectors.toList());
        System.out.printf("You have to choose %d resource type. \nAvailable resource types are:\n", quantity);
        UtilityPrinter.printNumericList(resourcesToString);
        List<Resource> selectedResources = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            if (quantity > 1)
                System.out.print("Choice " + i+1 + ": ");
            else
                System.out.print("Your choice: ");
            selectedResources.add(Resource.valueOf(InputParser.getCommandFromList(resourcesToString)));
        }
        client.sendMessageToServer(new ChooseResourceTypeResponse(selectedResources));
    }




    public void loadDevelopmentCards(Map<Integer, List<String>> lightDevelopmentCards) {
        MatchData.getInstance().setAllDevelopmentCards(lightDevelopmentCards);
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
                    System.out.print("Insert the ID of the card with the production you want to activate: ");
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
            System.out.println("Where would you like to remove it? Select the relative number");
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

    @Override
    public void displayChooseActionRequest(Map<ActionType, Boolean> executableActions, boolean standardActionDone) {
        List<String> availableActions = executableActions.keySet().stream().filter(executableActions::get).map(Enum::name).collect(Collectors.toList());
        List<String> textCommands = new ArrayList<>();
        System.out.println("Choose your next action: ");
        UtilityPrinter.printNumericList(availableActions);
        if (standardActionDone)
            System.out.println("Otherwise, you can end your turn now, just typing \"end\"");
        textCommands.add("end");
        //TODO gestire la possibilità di vedere gli altri giocatori la grid e il market prima di scegliere la action
        int selection=0;//per esempio, corrisponde al value della actionType (nella enum) scelta
        String selectionString = InputParser.getCommandFromList(textCommands, availableActions);
        if (selectionString.equals("end"))
            client.sendMessageToServer(new EndTurnRequest());
        else {
            selection = ActionType.valueOf(selectionString).getValue();
            client.sendMessageToServer(new ChooseActionResponse(selection));
        }

    }

    public static Predicate<Integer> conditionOnIntegerRange(int min, int max){
        return p -> p >= min && p <= max;
    }

    public static Predicate<Integer> conditionOnInteger(List<Integer> list){
        return list::contains;
    }

    public static Predicate<String> conditionOnString(List<String> lis){
        return lis::contains;
    }


    public static BiPredicate<String, List<String>> conditionOnReorganizeDepotsCommand =
            Utils::isACorrectReorganizeDepotCommand;
}
