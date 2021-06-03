package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.messages.toClient.matchData.MatchDataMessage;

public class TurnMessage extends MatchDataMessage {
    private boolean started;

    public TurnMessage(String nickname, boolean started) {
        super(nickname);
        this.started = started;
    }

    public boolean isStarted() {
        return started;
    }

}
