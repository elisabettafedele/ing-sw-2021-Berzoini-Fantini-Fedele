package it.polimi.ingsw.client.cli.specificCLI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.cli.CLI;
import it.polimi.ingsw.client.utilities.InputParser;
import it.polimi.ingsw.messages.toServer.SelectCardResponse;
import it.polimi.ingsw.messages.toServer.SelectDevelopmentCardSlotResponse;

import java.util.List;

public class CardsCLI {

    public static void displaySelectDevelopmentCardSlotRequest(Client client, boolean firstSlotAvailable, boolean secondSlotAvailable, boolean thirdSlotAvailable) {
        int selection = -1;
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

    public static void displaySelectCardRequest(Client client, List<Integer> cardsIDs, boolean leaderORdevelopment) {
        System.out.println("Select a card:");
        for (Integer id : cardsIDs){
            if(leaderORdevelopment){
                System.out.printf("%d. %s \n", id, MatchData.getInstance().getLeaderCardByID(id));
            }
            else{ System.out.printf("%d. %s \n", id, MatchData.getInstance().getDevelopmentCardByID(id));
            }

        }
        System.out.print("Insert the ID of the card you want to select: ");
        Integer selection = InputParser.getInt("Error: the ID provided is not available. Provide a valid ID", CLI.conditionOnInteger(cardsIDs));
        client.sendMessageToServer( new SelectCardResponse(selection));
    }
}
