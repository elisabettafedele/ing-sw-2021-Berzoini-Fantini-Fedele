package it.polimi.ingsw.messages.toClient;

import it.polimi.ingsw.client.cli.graphical.GraphicalMarket;
import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Marble;

public class SendMarketView implements MessageToClient{
    private Marble[][] marbles;
    private Marble sideMarble;

    public SendMarketView(Marble[][] marbles, Marble sideMarble){
        this.marbles = marbles;
        this.sideMarble = sideMarble;
    }

    @Override
    public void handleMessage(VirtualView view) {
        GraphicalMarket.printMarket(marbles, sideMarble);
    }
}
