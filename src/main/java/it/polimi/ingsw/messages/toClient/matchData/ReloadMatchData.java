package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.messages.toClient.MessageToClient;

/**
 * Message to notify the beginning/end of the incoming reload messages
 */
public class ReloadMatchData extends MessageToClient {
    private final boolean start;
    private final boolean disconnection;
    public ReloadMatchData(boolean start, boolean disconnection) {
        super(false);
        this.start = start;
        this.disconnection = disconnection;
    }

    @Override
    public void handleMessage(ViewInterface view) {
        view.setIsReloading(start);
    }

    public String toString(){
        return (start ? "starting to send match data" : "finished to send match data") + ((disconnection && start) ? " because a client disconnected during his turn and it has been canceled" : "");
    }
}
