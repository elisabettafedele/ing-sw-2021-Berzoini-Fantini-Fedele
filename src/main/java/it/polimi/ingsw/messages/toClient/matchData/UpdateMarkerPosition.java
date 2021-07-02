package it.polimi.ingsw.messages.toClient.matchData;

/**
 * Message to update the marker position
 */
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
