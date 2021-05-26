package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

/**
 * Message to display the standard view in the client
 */
public class DisplayStandardView implements MessageToClient {
    @Override
    public void handleMessage(VirtualView view) {
        view.displayStandardView();
    }
}
