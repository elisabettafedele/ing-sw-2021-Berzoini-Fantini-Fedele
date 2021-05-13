package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.messages.toServer.ChooseProductionPowersResponse;
import it.polimi.ingsw.model.cards.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionCLI {
    private static final int BASIC_PRODUCTION_POWER = 0;

    public static void displayChooseProductionPowersRequest(Client client, Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources) {
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
                            "Error: the ID provided is not available. Provide a valid ID: ", CLI.conditionOnInteger(IDs));
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
                    "Error: the ID provided is not available. Provide a valid ID", CLI.conditionOnIntegerRange(1, 3));
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

    //check
    private static List<Integer> displayAvailableProductions(Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources){
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

    private static void manageRemoveProduction(Map<Integer, List<Value>> availableProductionPowers,
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
                "Error: the ID provided is not available. Provide a valid ID", CLI.conditionOnInteger(selectedIDs));
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

    private static List<Value> manageBasicProductionPower(Map<Resource, Integer> availableResources) {
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

    private static void subtractResources(Value activationCost, Map<Resource, Integer> availableResources){
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


    private static boolean hasResourcesForThisProduction(Map<Resource, Integer> activationCost, Map<Resource, Integer> availableResources){
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
}
