package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.client.utilities.UtilityPrinter;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toServer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizeDepotsCLI {

    public static void displayChooseStorageTypeRequest(Client client, Resource resource, List<String> availableDepots, boolean setUpPhase) {
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



    public static void displayReorganizeDepotsRequest(Client client, List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResources){
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
        String commandType = InputParser.getString("Please insert a valid command", CLI.conditionOnString(possibleCommands));
        if (commandType.equals(Command.END_REORGANIZE_DEPOTS.command)) {
            client.sendMessageToServer(new NotifyEndDepotsReorganization());
            return;
        }
        System.out.print("Select the origin depot: ");
        String originDepot = InputParser.getString("Please insert a valid depot", CLI.conditionOnString(depots));
        System.out.print("Select the destination depot: ");
        String destinationDepot = InputParser.getString("Please insert a valid depot", CLI.conditionOnString(depots));
        if (commandType.equals(Command.SWAP.command))
            client.sendMessageToServer(new SwapWarehouseDepotsRequest(originDepot,destinationDepot));
        else {
            System.out.print("Select the quantity of the resources you want to move: ");
            Integer quantity = InputParser.getInt("Please insert a valid resource quantity", CLI.conditionOnIntegerRange(1, 4));
            assert (quantity != null);
            if (availableLeaderResources.size() == 2 && originDepot.equals("LEADER_DEPOT")) {
                System.out.print("Select the type of resource you want to remove from the leader depot: ");
                resource = Resource.valueOf(InputParser.getString("Insert a valid resource type", CLI.conditionOnString(availableLeaderResources.stream().map(Enum::name).collect(Collectors.toList()))));
            }
            client.sendMessageToServer(new MoveResourcesRequest(originDepot, destinationDepot, resource, quantity));
        }
    }
}
