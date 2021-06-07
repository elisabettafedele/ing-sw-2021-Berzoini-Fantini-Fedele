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

    public static void displayMarbleInsertionPositionRequest(Client client) {
        System.out.println("Insert a marble insertion position (from 1 to 7) to insert the marble in the market tray: ");
        Integer selection = InputParser.getInt("Invalid position: the position must be an integer from 1 to 7!", CLI.conditionOnIntegerRange(1, 7));
        if (selection != null)
            client.sendMessageToServer(new MarbleInsertionPositionResponse(selection));
    }

    public static void displayChooseWhiteMarbleConversionRequest(Client client, List<Resource> resources, int numberOfMarbles) {
        System.out.println("You have these two possible white marble conversions: " + resources.get(0) + " | " + resources.get(1));
        List<Resource> resourcesChosen = new LinkedList<>();
        for (int i = 0; i < numberOfMarbles; i++){
            if (numberOfMarbles > 1)
                System.out.printf("White marble #%d\n", i+1);
            try {
                resourcesChosen.add(getMarbleColor(resources));
            } catch (IOException e) {
                return;
            }
        }
        client.sendMessageToServer(new ChooseWhiteMarbleConversionResponse(resourcesChosen));
    }

    private static Resource getMarbleColor(List<Resource> conversions) throws IOException {
        return Resource.valueOf(InputParser.getCommandFromList(conversions.stream().map(Enum :: name).collect(Collectors.toList())));
    }

    public static void displayMarblesTaken(List<Marble> marblesTaken, boolean needToChooseConversion) {
        System.out.println("These are the marbles you took from the market: " );
        for (Marble marble : marblesTaken)
            System.out.print(Colour.getMarbleColour(marble) + '\u25CF' + Colour.ANSI_RESET + " ");
        if (marblesTaken.contains(Marble.WHITE)){
            if (!needToChooseConversion)
                System.out.println("\nWhite marbles will be automatically converted according to your leader card effects (if any)");
            else
                System.out.println("You have more than one White Marble Effect Active! You need to choose one white marble at a time how you want to convert it");
        }
    }
}
