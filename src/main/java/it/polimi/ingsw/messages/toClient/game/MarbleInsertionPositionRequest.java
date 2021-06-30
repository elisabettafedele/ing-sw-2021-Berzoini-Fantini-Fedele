package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class MarbleInsertionPositionRequest extends MessageToClient {

    public MarbleInsertionPositionRequest(){
        super(true);
    }
    @Override
    public void handleMessage(VirtualView view) {
        view.displayMarbleInsertionPositionRequest();
    }

    public String toString(){
        return "asking marble insertion position";
    }
}
