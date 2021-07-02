package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.ViewInterface;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.messages.toClient.MessageToClient;

import java.util.List;

public class SendReorganizeDepotsCommands extends MessageToClient {
    private List<String> availableDepots;
    private boolean first;
    private boolean failure;
    private List<Resource> availableLeaderResources;

    public SendReorganizeDepotsCommands(List<String> availableDepots, boolean first, boolean failure, List<Resource> availableLeaderResources) {
        super(true);
        this.availableDepots = availableDepots;
        this.first = first;
        this.failure = failure;
        this.availableLeaderResources = availableLeaderResources;
    }

    public String toString(){
        return "asking to choose a command to reorganize the depots";
    }


    @Override
    public void handleMessage(ViewInterface view) {
        view.displayReorganizeDepotsRequest(availableDepots, first, failure, availableLeaderResources);
    }
}
