package it.polimi.ingsw.client.cli.specificCLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.cli.graphical.Screen;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.messages.toServer.game.SelectCardResponse;
import it.polimi.ingsw.messages.toServer.game.SelectDevelopmentCardSlotResponse;

import java.util.List;

public class CardsCLI {

    public static void displaySelectDevelopmentCardSlotRequest(Client client, boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable) {
        Integer selection = -1;
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
            if (selection == null)
                return;
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

    /**
     * Method to display a graphical request to the user when activating/discarding
     * {@link it.polimi.ingsw.model.cards.LeaderCard} or when buying {@link it.polimi.ingsw.model.cards.DevelopmentCard}
     * @param client the {@link Client} to who the message is directed
     * @param cardsIDs the IDs of the {@link it.polimi.ingsw.model.cards.Card} to be displayed
     * @param leaderORdevelopment True if the cards to display are leader cards, False if they are development
     */
    public static void displaySelectCardRequest(Client client, List<Integer> cardsIDs, boolean leaderORdevelopment) {
        Screen.getInstance().displayCardSelection(cardsIDs, null);

        System.out.print("Insert the ID of the card you want to select: ");
        Integer selection = InputParser.getInt("Error: the ID provided is not available. Provide a valid ID", CLI.conditionOnInteger(cardsIDs));
        if (selection == null)
            return;
        client.sendMessageToServer( new SelectCardResponse(selection));
    }
}
