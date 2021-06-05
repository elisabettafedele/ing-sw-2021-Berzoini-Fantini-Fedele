package it.polimi.ingsw.client.utilities;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.View;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.messages.toServer.game.ChooseProductionPowersResponse;
import it.polimi.ingsw.model.cards.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common class of {@link CLI} and {@link GUI} to manage the activate production action
 * client side
 */
public class UtilityProduction {
    private static final int BASIC_PRODUCTION_POWER = 0;
    private static Map<Integer, List<Value>> selectedProductions;
    private static List<Value> actualChosenProduction;
    private static View view;
    private static Client client;
    private static Map<Integer, List<Value>> availableProductionPowers;
    private static Map<Resource, Integer> availableResources;

    /**
     * Resets the initial information to perform the action
     * @param view the {@link CLI} or {@link GUI} instance
     * @param client
     * @param availableProductionPowers
     * @param availableResources
     */
    public static void initialize(View view, Client client, Map<Integer, List<Value>> availableProductionPowers, Map<Resource, Integer> availableResources ){
        selectedProductions = new HashMap<>();
        actualChosenProduction = null;
        UtilityProduction.view=view;
        UtilityProduction.client=client;
        UtilityProduction.availableProductionPowers=availableProductionPowers;
        UtilityProduction.availableResources=availableResources;
        displayAvailableProductions();

    }

    /**
     * Among all the initial availableProduction powers checks if the player has enough resources to activate
     * a production. This method is necessary because the player can activate more than one production power during
     * his turn.
     */
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
                availableProductionIDs.add(entry.getKey());
            }
        }
        if(availableResources.values().stream().mapToInt(Integer::intValue).sum() >= 2 && availableProductionPowers.containsKey(0)){
            availableProductionIDs.add(BASIC_PRODUCTION_POWER);
        }
        if(view instanceof CLI) ((CLI) view).displayCurrentSelectedProductions(selectedProductions.keySet(),selectedProductions.get(0));
        view.displayProductionCardYouCanSelect(availableProductionIDs, availableProductionPowers.get(0));
        view.displayChooseProduction(availableProductionIDs,availableResources,true);
    }

    /**
     * Given a production cost checks if tha player has enough resources to activate it
     * @param activationCost the cost of the production power to analyze
     * @param availableResources the resources that the player has not spent yet
     * @return True if the player can activate the production
     */
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


    /**
     * Add the production power selected to a list
     * @param selection the ID of the production power selcted
     */
    public static void addProductionPower(Integer selection) {
        actualChosenProduction = availableProductionPowers.get(selection);
        addProdPowerToList(selection);
    }

    /**
     * Method to manage the construction of the basic production power
     * @param chosenResources the resource chosen by the player to be used for input and output
     */
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

    /**
     * Add the production power selected to a list
     * @param selection the ID of the production power selcted
     */
    public static void addProdPowerToList(int selection){
        selectedProductions.put(selection, actualChosenProduction);
        availableProductionPowers.remove(selection);
        subtractResources(actualChosenProduction.get(0), availableResources);
    }

    /**
     * After choosing a production power the activation cost is removed from the initial availableResources in order
     * to check correctly the available resources for other production powers selections
     * @param activationCost
     * @param availableResources
     */
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

    /**
     * Method to notify the server when the player has completed the selection of the production powers
     */
    public static void confirmChoices() {
        List<Integer> productionPowersSelected= new ArrayList<>(selectedProductions.keySet());
        if(productionPowersSelected.contains(BASIC_PRODUCTION_POWER)){
            client.sendMessageToServer(new ChooseProductionPowersResponse(productionPowersSelected, selectedProductions.get(BASIC_PRODUCTION_POWER)));
        } else {
            client.sendMessageToServer(new ChooseProductionPowersResponse(productionPowersSelected)); //If the player confirms with zero selections don't increment the actionDone variable!!!
        }
    }

    /**
     * Method to ask the player which of his production powers want to remove
     */
    public static void chooseProductionToRemove() {
        if(view instanceof CLI) ((CLI) view).displayCurrentSelectedProductions(selectedProductions.keySet(),selectedProductions.get(0));
        view.displayChooseProduction(new ArrayList<>(selectedProductions.keySet()), availableResources,false);
    }

    /**
     * Method to manage the removal of a chosen production. If the removed production is the basic power, the basic
     * power it's rebuilt.
     * Then the production is removed from the list of the chosen ones and his activation cost is re-added to the
     * availableResources map
     * @param selection
     */
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
