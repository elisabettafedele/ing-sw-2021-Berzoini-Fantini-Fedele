package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class ReloadMatchData implements MessageToClient {
    private boolean start;
    private boolean disconnection;
    public ReloadMatchData(boolean start, boolean disconnection) {
        this.start = start;
        this.disconnection = disconnection;
    }

    @Override
    public void handleMessage(VirtualView view) {

    }
}
