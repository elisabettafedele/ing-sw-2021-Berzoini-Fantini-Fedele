package it.polimi.ingsw.messages.toClient.game;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class NotifyPointsSinglePlayer extends MessageToClient {
    private int victoryPoints;

    public NotifyPointsSinglePlayer(int victoryPoints) {
        super(false);
        this.victoryPoints = victoryPoints;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayResults(victoryPoints);
    }

    public String toString(){
        return "notifying results";
    }
}
