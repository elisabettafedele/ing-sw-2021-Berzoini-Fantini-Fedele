package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

/**
 * Message to ask which marble conversion the player wants to use
 */
public class ChooseWhiteMarbleConversionRequest extends MessageToClient {

    private List<Resource> conversions;
    private int numberOfWhiteMarbles;


    public ChooseWhiteMarbleConversionRequest(List<Resource> conversions, int numberOfWhiteMarbles){
        super(true);
        this.conversions = conversions;
        this.numberOfWhiteMarbles = numberOfWhiteMarbles;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.displayChooseWhiteMarbleConversionRequest(conversions, numberOfWhiteMarbles);

    }

    public String toString(){
        return "asking to choose one out of the two white marble conversions available";
    }
}
