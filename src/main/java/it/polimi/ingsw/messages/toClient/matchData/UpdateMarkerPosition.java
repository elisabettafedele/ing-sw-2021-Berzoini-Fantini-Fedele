package it.polimi.ingsw.messages.toClient.matchData;

import it.polimi.ingsw.common.VirtualView;
import it.polimi.ingsw.messages.toClient.MessageToClient;

public class UpdateMarkerPosition extends MatchDataMessage {
    private int markerPosition;

    public UpdateMarkerPosition(String nickname, int markerPosition) {
        super(nickname);
        this.markerPosition = markerPosition;
    }

    public int getMarkerPosition() {
        return markerPosition;
    }
}
