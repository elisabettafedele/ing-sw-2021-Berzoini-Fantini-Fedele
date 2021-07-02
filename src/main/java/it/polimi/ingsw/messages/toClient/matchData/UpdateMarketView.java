package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.enumerations.Marble;

/**
 * Message to update the status of the market
 */
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
