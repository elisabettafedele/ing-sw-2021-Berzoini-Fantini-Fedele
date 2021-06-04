package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class ReloadMatchData implements MessageToClient {
    private final boolean start;
    private final boolean disconnection;
    public ReloadMatchData(boolean start, boolean disconnection) {
        this.start = start;
        this.disconnection = disconnection;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.setIsReloading(start);
        if (!start)
            view.displayStandardView();
    }

    public String toString(){
        return (start ? "starting to send match data" : "finished to send match data") + ((disconnection && start) ? " because a client disconnected during his turn and it has been canceled" : "");
    }
}
