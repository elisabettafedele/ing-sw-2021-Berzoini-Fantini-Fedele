package it.polimi.ingsw.client.cli.specificCLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.cli.graphical.Colour;
import it.polimi.ingsw.client.cli.graphical.Screen;
import it.polimi.ingsw.client.utilities.Command;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.client.utilities.UtilityPrinter;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.messages.toServer.game.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizeDepotsCLI {

    public static void displayChooseStorageTypeRequest(Client client, Resource resource, List<String> availableDepots, boolean canDiscard, boolean canReorganize) {
        if (availableDepots.isEmpty())
            System.out.println("There are no available depots for " + resource);
        else {
            System.out.print("Choose a depot for the " + resource + " ");
            System.out.println(Colour.getColourByResource(resource).getCode() + resource.symbol + Colour.ANSI_RESET);
            Screen.getInstance().displayWarehouse();
            System.out.println("Available depots for this resource type are:");
            UtilityPrinter.printNumericList(availableDepots);
        }
        List<String> textCommands = new ArrayList<>();
        if (canDiscard) {
            System.out.println("Type d to discard the resource" + (canReorganize ? " or r to reorganize your depots: d | r" : ": d"));
            textCommands.add(Command.DISCARD.command);
            if (canReorganize)
                textCommands.add(Command.REORGANIZE.command);
        }
        String choiceString = null;
        try {
            choiceString = InputParser.getCommandFromList(textCommands, availableDepots);
        } catch (IOException e) {
            return;
        }
        if (choiceString == null)
            return;
        if (choiceString.equals(Command.DISCARD.command))
            client.sendMessageToServer(new DiscardResourceRequest(resource));
        else if (choiceString.equals(Command.REORGANIZE.command))
            client.sendMessageToServer(new ReorganizeDepotRequest());
        else
            client.sendMessageToServer(new ChooseStorageTypeResponse(resource, choiceString, canDiscard, canReorganize));
    }

    public static void displaySelectStorageRequest(Client client, Resource resource, boolean isInWarehouse, boolean isInStrongbox, boolean isInLeaderDepot) {
        if (isInLeaderDepot ^ isInStrongbox ^ isInWarehouse){
            if (isInWarehouse)
                client.sendMessageToServer(new SelectStorageResponse(resource, ResourceStorageType.WAREHOUSE));
            else if (isInStrongbox)
                client.sendMessageToServer(new SelectStorageResponse(resource, ResourceStorageType.STRONGBOX));
            else
                client.sendMessageToServer(new SelectStorageResponse(resource, ResourceStorageType.LEADER_DEPOT));
            return;
        }
        while (true) {
            System.out.println("You can take a " + resource.toString() + " from:");
            if (isInWarehouse) {
                System.out.println("1. WAREHOUSE");
            }
            if (isInStrongbox) {
                System.out.println("2. STRONGBOX");
            }
            if (isInLeaderDepot) {
                System.out.println("3. LEADER DEPOT");
            }
            System.out.println("Where would you like to remove it? Select the relative number:");
            Integer selection = InputParser.getInt("Error: write a number.");
            if (selection == null)
                return;
            if (selection == 1 && isInWarehouse) {
                client.sendMessageToServer(new SelectStorageResponse(resource, ResourceStorageType.WAREHOUSE));
                return;
            }
            if (selection == 2 && isInStrongbox) {
                client.sendMessageToServer(new SelectStorageResponse(resource, ResourceStorageType.STRONGBOX));
                return;
            }
            if (selection == 3 && isInLeaderDepot) {
                client.sendMessageToServer(new SelectStorageResponse(resource, ResourceStorageType.LEADER_DEPOT));
                return;
            }
            System.out.println("Incorrect choice");
        }
    }


    public static void displayReorganizeDepotsRequest(Client client, List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResources) {
        if (first)
            System.out.print("You can now reorganize your depots with the command" + Command.SWAP.command + " or " + Command.MOVE.command + "\n- " + Command.SWAP + ": realizes a swap of two depots of the warehouse which contain different resource types\n- " + Command.MOVE + ": move a certain number of resources from one depot to another (be careful because the leader depots have a fixed resource type)\nIf you have finished type " + Command.END_REORGANIZE_DEPOTS.command + "\n");
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
        System.out.println("Select the " + (commandType.equals(Command.SWAP.command) ? "first" : "origin") + " depot:");
        UtilityPrinter.printNumericList(depots);
        String originDepot = null;
        try {
            originDepot = InputParser.getCommandFromList(depots);
        } catch (IOException e) {
            return;
        }
        System.out.println("Select the " + (commandType.equals(Command.SWAP.command) ? "second" : "destination") + " depot:");
        UtilityPrinter.printNumericList(depots);
        String destinationDepot = null;
        try {
            destinationDepot = InputParser.getCommandFromList(depots);
        } catch (IOException e) {
            return;
        }
        if (commandType.equals(Command.SWAP.command))
            client.sendMessageToServer(new SwapWarehouseDepotsRequest(originDepot, destinationDepot));
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

    public static void displayResourcesToStore(List<Resource> resourcesToStore){
        System.out.println("You now have to store these resources: ");
        for (Resource resource : resourcesToStore){
            System.out.print(Colour.getColourByResource(resource).getCode() + resource.symbol + Colour.ANSI_RESET + " ");
        }
        System.out.println(" ");
    }
}
