package it.polimi.ingsw.client.cli.specificCLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.cli.graphical.Screen;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.client.utilities.UtilityPrinter;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toServer.game.ChooseLeaderCardsResponse;
import it.polimi.ingsw.messages.toServer.game.ChooseResourceTypeResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetUpCLI {

    public static void displayChooseLeaderCardsRequest(Client client, List<Integer> leaderCardsIDs){
        Screen.getInstance().displayCardSelection(leaderCardsIDs, null);
        System.out.println("Choose two Leader Cards to keep");
        //InputParser.flush();
        System.out.print("Insert the ID of the first leader card chosen: ");
        Integer firstChoice = InputParser.getInt("Error: the ID provided is not available. Provide a valid ID", CLI.conditionOnInteger(leaderCardsIDs));
        leaderCardsIDs.remove(firstChoice);
        MatchData.getInstance().addChosenLeaderCard(firstChoice, false);
        System.out.print("Insert the ID of the second leader card chosen: ");
        Integer secondChoice = InputParser.getInt("Error: the ID provided is not available. Provide a valid ID", CLI.conditionOnInteger(leaderCardsIDs));
        MatchData.getInstance().addChosenLeaderCard(secondChoice, false);
        leaderCardsIDs.remove(secondChoice);
        client.sendMessageToServer(new ChooseLeaderCardsResponse(leaderCardsIDs));
    }

    public static void displayChooseResourceTypeRequest(Client client, List<Resource> resourceTypes, int quantity){
        List<String> resourcesToString = resourceTypes.stream().map(Enum::name).collect(Collectors.toList());
        System.out.printf("You have to choose %d resource type. \nAvailable resource types are:\n", quantity);
        UtilityPrinter.printNumericList(resourcesToString);
        List<Resource> selectedResources = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            if (quantity > 1)
                System.out.print("Choice " + i+1 + ": ");
            else
                System.out.print("Your choice: ");
            try {
                selectedResources.add(Resource.valueOf(InputParser.getCommandFromList(resourcesToString)));
            } catch (IOException e) {
                return;
            }
        }
        client.sendMessageToServer(new ChooseResourceTypeResponse(selectedResources));
    }


}
