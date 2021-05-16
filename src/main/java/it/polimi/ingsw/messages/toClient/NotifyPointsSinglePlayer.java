package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.common.VirtualView;

public class NotifyPointsSinglePlayer implements MessageToClient{
    private int victoryPoints;

    public NotifyPointsSinglePlayer(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    @Override
    public void handleMessage(VirtualView view) {
        view.displayResults(victoryPoints);
    }
}
