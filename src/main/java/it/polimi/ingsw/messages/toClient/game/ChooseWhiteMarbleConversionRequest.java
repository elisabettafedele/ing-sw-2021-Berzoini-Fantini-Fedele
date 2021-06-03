package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class ChooseWhiteMarbleConversionRequest implements MessageToClient {

    private List<Resource> conversions;
    private int numberOfWhiteMarbles;


    public ChooseWhiteMarbleConversionRequest(List<Resource> conversions, int numberOfWhiteMarbles){
        this.conversions = conversions;
        this.numberOfWhiteMarbles = numberOfWhiteMarbles;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayChooseWhiteMarbleConversionRequest(conversions, numberOfWhiteMarbles);

    }

    public String toString(){
        return "asking to choose one out of the two white marble conversions available";
    }
}
