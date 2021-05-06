package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

import java.util.List;

public class SendReorganizeDepotsCommands implements MessageToClient{
    private List<String> availableDepots;

    public SendReorganizeDepotsCommands(List<String> availableDepots) {
        this.availableDepots = availableDepots;
    }


    @Override
    public void handleMessage(VirtualView view) {

    }
}
