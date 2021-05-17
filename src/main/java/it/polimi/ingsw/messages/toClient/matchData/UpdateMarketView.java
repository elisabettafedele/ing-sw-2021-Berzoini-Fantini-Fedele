package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.client.cli.graphical.GraphicalMarket;
import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class UpdateMarketView extends MatchDataMessage {
    private Marble[][] marbles;
    private Marble sideMarble;

    public UpdateMarketView(String nickname, Marble[][] marbles, Marble sideMarble){
        super(nickname);
        this.marbles = marbles;
        this.sideMarble = sideMarble;
    }

    public Marble[][] getMarbles() {
        return marbles;
    }

    public Marble getSideMarble() {
        return sideMarble;
    }

}
