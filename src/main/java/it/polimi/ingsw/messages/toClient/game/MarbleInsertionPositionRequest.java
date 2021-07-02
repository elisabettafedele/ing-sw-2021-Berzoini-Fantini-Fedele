package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

/**
 * Message to ask where the player wants to insert the marble
 */
public class MarbleInsertionPositionRequest extends MessageToClient {

    public MarbleInsertionPositionRequest(){
        super(true);
    }
    @Override
    public void handleMessage(ViewInterface view) {
        view.displayMarbleInsertionPositionRequest();
    }

    public String toString(){
        return "asking marble insertion position";
    }
}
