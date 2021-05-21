package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class ReloadMatchData implements MessageToClient {
    private final boolean start;
    private final boolean disconnection;
    public ReloadMatchData(boolean start, boolean disconnection) {
        this.start = start;
        //TODO handle disconnection
        this.disconnection = disconnection;
    }

    @Override
    public void handleMessage(VirtualView view) {
        if (!start)
            view.displayStandardView();
    }
}
