package it.polimi.ingsw.client.cli.specificCLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.cli.graphical.Colour;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toServer.game.ChooseWhiteMarbleConversionResponse;
import it.polimi.ingsw.messages.toServer.game.MarbleInsertionPositionResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TakeResourcesFromMarketCLI {

    /**
     * Method to display the request of the position to insert the marble and to get the client's answer
     * @param client the {@link Client} that has received the request
     */
    public static void displayMarbleInsertionPositionRequest(Client client) {
        System.out.println("Insert a marble insertion position (from 1 to 7) to insert the marble in the market tray: ");
        Integer selection = InputParser.getInt("Invalid position: the position must be an integer from 1 to 7!", CLI.conditionOnIntegerRange(1, 7));
        if (selection != null)
            client.sendMessageToServer(new MarbleInsertionPositionResponse(selection));
    }

    /**
     * Method to display the request of a white marble conversion when more than one option is available
     * @param client the {@link Client} that has received the request
     * @param resources the available conversions
     * @param numberOfMarbles the number of white marbles that must be converted
     */
    public static void displayChooseWhiteMarbleConversionRequest(Client client, List<Resource> resources, int numberOfMarbles) {
        System.out.println("Choose one these two possible white marble conversions: \n1." + resources.get(0) + "\n2." + resources.get(1));
        List<Resource> resourcesChosen = new LinkedList<>();
        for (int i = 0; i < numberOfMarbles; i++){
            if (numberOfMarbles > 1)
                System.out.printf("Choice %d: ", i+1);
            try {
                resourcesChosen.add(getMarbleColor(resources));
            } catch (IOException e) {
                return;
            }
        }
        client.sendMessageToServer(new ChooseWhiteMarbleConversionResponse(resourcesChosen));
    }

    /**
     *
     * @param conversions the available conversions
     * @return the {@link Resource} chosen from the menu
     * @throws IOException when the clients is disconnected due to a server disconnection
     */
    private static Resource getMarbleColor(List<Resource> conversions) throws IOException {
        return Resource.valueOf(InputParser.getCommandFromList(conversions.stream().map(Enum :: name).collect(Collectors.toList())));
    }

    /**
     * Method to display the marbles taken from the market
     * @param marblesTaken the list of the {@link Marble} which has been taken from the market
     * @param needToChooseConversion true if the client has to choose a white marble conversion
     */
    public static void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion) {
        System.out.println("These are the marbles you took from the market: " );
        for (Marble marble : marblesTaken)
            System.out.print(Colour.getMarbleColour(marble) + '\u25CF' + Colour.ANSI_RESET + " ");
        System.out.print("\n");
        if (marblesTaken.contains(Marble.WHITE)){
            if (!needToChooseConversion)
                System.out.println("White marbles will be automatically converted according to your leader card effects (if any)");
            else
                System.out.println("You have more than one White Marble Effect Active! You need to choose one white marble at a time how you want to convert it");
        }
    }
}
