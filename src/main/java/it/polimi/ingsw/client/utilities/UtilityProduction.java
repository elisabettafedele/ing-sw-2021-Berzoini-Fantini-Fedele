package it.polimi.ingsw.client.utilities;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.messages.toServer.game.ChooseProductionPowersResponse;
import it.polimi.ingsw.model.cards.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtilityProduction {
    private static final int BASIC_PRODUCTION_POWER = 0;
    private static Map<Integer, List<Value>> selectedProductions;
    private static List<Value> actualChosenProduction;
    private static View view;
    private static Client client;
    private static Map<Integer, List<Value>> availableProductionPowers;
    private static Map<Resource, Integer> availableResources;

    public static void initialize(View view, Client client, Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources ){
        selectedProductions = new HashMap<>();
        actualChosenProduction = null;
        UtilityProduction.view=view;
        UtilityProduction.client=client;
        UtilityProduction.availableProductionPowers=availableProductionPowers;
        UtilityProduction.availableResources=availableResources;
        displayAvailableProductions();

    }
    public static void displayAvailableProductions(){
        view.displayStandardView();
        List <Integer> availableProductionIDs = new ArrayList<>();
        for(Map.Entry<Integer, List<Value>> entry : availableProductionPowers.entrySet()){
            Map<Resource, Integer> activationCost = null;
            try {
                activationCost = entry.getValue().get(0).getResourceValue();
            } catch (ValueNotPresentException e) {
                //skip
            }
            if(hasResourcesForThisProduction(activationCost, availableResources) && entry.getKey() != 0){
                //System.out.println(entry.getKey());
                availableProductionIDs.add(entry.getKey());
            }
        }
        if(availableResources.values().stream().mapToInt(Integer::intValue).sum() >= 2 && availableProductionPowers.containsKey(0)){
            //System.out.println("0. Basic Production Power: " + availableProductionPowers.get(0));
            availableProductionIDs.add(BASIC_PRODUCTION_POWER);
        }
        if(view instanceof CLI) ((CLI) view).displayCurrentSelectedProductions(selectedProductions.keySet(),selectedProductions.get(0));
        //TODO nella gui mettere che se availableProductionIDs.size()==0 deve chiamare la nextActionProduction della view
        view.displayProductionCardYouCanSelect(availableProductionIDs, availableProductionPowers.get(0));
        view.displayChooseProduction(availableProductionIDs,availableResources,true);
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



    public static void addProductionPower(Integer selection) {
        actualChosenProduction = availableProductionPowers.get(selection);
        addProdPowerToList(selection);
    }

    public static void manageBasicProductionPower(List<Resource> chosenResources) {
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
        actualChosenProduction=production;
        addProdPowerToList(0);
    }

    public static void addProdPowerToList(int selection){
        selectedProductions.put(selection, actualChosenProduction);
        availableProductionPowers.remove(selection);
        subtractResources(actualChosenProduction.get(0), availableResources);
    }

    private static void subtractResources(Value activationCost, Map<Resource, Integer> availableResources){
        Map<Resource, Integer> resourceToBeRemoved = null;
        try {
            resourceToBeRemoved = activationCost.getResourceValue();
        } catch (ValueNotPresentException e) {
            e.printStackTrace();
        }
        for(Map.Entry<Resource, Integer> entry : resourceToBeRemoved.entrySet()){
            availableResources.put(entry.getKey(), availableResources.get(entry.getKey()) - entry.getValue()); //TODO: there was -1 instead of entry.getValue()
        }
        if(view instanceof CLI) ((CLI) view).displayCurrentSelectedProductions(selectedProductions.keySet(),selectedProductions.get(0));
        view.chooseNextProductionAction();
    }

    public static void confirmChoices() {
        List<Integer> productionPowersSelected= new ArrayList<>(selectedProductions.keySet());
        if(productionPowersSelected.contains(BASIC_PRODUCTION_POWER)){
            client.sendMessageToServer(new ChooseProductionPowersResponse(productionPowersSelected, selectedProductions.get(BASIC_PRODUCTION_POWER)));
        } else {
            client.sendMessageToServer(new ChooseProductionPowersResponse(productionPowersSelected)); //If the player confirms with zero selections don't increment the actionDone variable!!!
        }
    }

    public static void chooseProductionToRemove() {
        if(view instanceof CLI) ((CLI) view).displayCurrentSelectedProductions(selectedProductions.keySet(),selectedProductions.get(0));
        view.displayChooseProduction(new ArrayList<>(selectedProductions.keySet()), availableResources,false);
    }

    public static void removeProduction(Integer selection) {
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
        view.chooseNextProductionAction();
    }
}
