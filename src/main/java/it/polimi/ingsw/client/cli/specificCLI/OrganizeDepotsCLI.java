package it.polimi.ingsw.client.cli.specificCLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.cli.graphical.Colour;
import it.polimi.ingsw.client.cli.graphical.Screen;
import it.polimi.ingsw.client.utilities.ReorganizeDepotsCommand;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.client.utilities.UtilityPrinter;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.messages.toServer.game.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class to manage all the interactions with depots
 */
public class OrganizeDepotsCLI {

    /**
     * Method to ask in which depot the player wants to put a resource
     * @param client {@link Client} with the connection to the server
     * @param resource the {@link Resource} to store
     * @param availableDepots a List with all the available depots for that resource
     * @param canDiscard true if it is possible to discard the resource
     * @param canReorganize true if a reorganization of depots it's possible
     */
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
            textCommands.add(ReorganizeDepotsCommand.DISCARD.command);
            if (canReorganize)
                textCommands.add(ReorganizeDepotsCommand.REORGANIZE.command);
        }
        String choiceString = null;
        try {
            choiceString = InputParser.getCommandFromList(textCommands, availableDepots);
        } catch (IOException e) {
            return;
        }
        if (choiceString == null)
            return;
        if (choiceString.equals(ReorganizeDepotsCommand.DISCARD.command))
            client.sendMessageToServer(new DiscardResourceRequest(resource));
        else if (choiceString.equals(ReorganizeDepotsCommand.REORGANIZE.command))
            client.sendMessageToServer(new ReorganizeDepotRequest());
        else
            client.sendMessageToServer(new ChooseStorageTypeResponse(resource, choiceString, canDiscard, canReorganize));
    }

    /**
     * Method to ask the player from which depot wants to take a specific resource
     * @param client {@link Client} with the connection to the server
     * @param resource the {@link Resource} to be taken
     * @param isInWarehouse true if the {@link Resource} is present in the {@link it.polimi.ingsw.model.player.Warehouse}
     * @param isInStrongbox true if the {@link Resource} is present in a {@link it.polimi.ingsw.model.depot.StrongboxDepot}
     * @param isInLeaderDepot true if the {@link Resource} is present in a {@link it.polimi.ingsw.model.depot.LeaderDepot}
     */
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
            Integer selection = InputParser.getInt();
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

    /**
     * Method to manage the reorganization of depots
     * @param client {@link Client} with the connection to the server
     * @param depots the depots in which the reorganization is possible
     * @param first true if it is the first time the reorganization has been asked
     * @param failure true if the given reorganization is not doable
     * @param availableLeaderResources the resources present in the leader cards, if any
     */
    public static void displayReorganizeDepotsRequest(Client client, List<String> depots, boolean first, boolean failure, List<Resource> availableLeaderResources) {
        if (first)
            System.out.print("You can now reorganize your depots with the command" + ReorganizeDepotsCommand.SWAP.command + " or " + ReorganizeDepotsCommand.MOVE.command + "\n- " + ReorganizeDepotsCommand.SWAP + ": realizes a swap of two depots of the warehouse which contain different resource types\n- " + ReorganizeDepotsCommand.MOVE + ": move a certain number of resources from one depot to another (be careful because the leader depots have a fixed resource type)\nIf you have finished type " + ReorganizeDepotsCommand.END_REORGANIZE_DEPOTS.command + "\n");
        if (failure)
            System.out.println("Invalid reorganization: check the capacity and the type of the depots before reorganizing.");
        List<String> possibleCommands = ReorganizeDepotsCommand.getReorganizeDepotsCommands();
        possibleCommands.addAll(depots);
        Resource resource = Resource.ANY;
        System.out.println("Do you want to swap or move your resources? Type " + ReorganizeDepotsCommand.END_REORGANIZE_DEPOTS.command + " if you want to end the reorganization of your depots");
        System.out.println(ReorganizeDepotsCommand.SWAP.command + " | " + ReorganizeDepotsCommand.MOVE.command + " | " + ReorganizeDepotsCommand.END_REORGANIZE_DEPOTS.command);
        String commandType = InputParser.getString("Please insert a valid command", CLI.conditionOnString(possibleCommands));
        if (commandType.equals(ReorganizeDepotsCommand.END_REORGANIZE_DEPOTS.command)) {
            client.sendMessageToServer(new NotifyEndDepotsReorganization());
            return;
        }
        System.out.println("Select the " + (commandType.equals(ReorganizeDepotsCommand.SWAP.command) ? "first" : "origin") + " depot:");
        UtilityPrinter.printNumericList(depots);
        String originDepot = null;
        try {
            originDepot = InputParser.getCommandFromList(depots);
        } catch (IOException e) {
            return;
        }
        System.out.println("Select the " + (commandType.equals(ReorganizeDepotsCommand.SWAP.command) ? "second" : "destination") + " depot:");
        UtilityPrinter.printNumericList(depots);
        String destinationDepot = null;
        try {
            destinationDepot = InputParser.getCommandFromList(depots);
        } catch (IOException e) {
            return;
        }
        if (commandType.equals(ReorganizeDepotsCommand.SWAP.command))
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
