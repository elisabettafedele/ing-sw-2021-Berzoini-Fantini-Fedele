package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.controller.actions.Action;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class MarbleInsertionPositionRequest implements MessageToClient {


    @Override
    public void handleMessage(VirtualView view) {
        view.displayMarbleInsertionPositionRequest();
    }
}
