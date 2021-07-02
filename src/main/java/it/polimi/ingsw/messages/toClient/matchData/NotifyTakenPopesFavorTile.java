package it.polimi.ingsw.messages.toClient.matchData;

/**
 * Message to notify an update on the popes tile status
 */
public class NotifyTakenPopesFavorTile extends MatchDataMessage{
    private int number;
    private boolean taken;

    public NotifyTakenPopesFavorTile(String nickname, int number, boolean taken) {
        super(nickname);
        this.number = number;
        this.taken = taken;
    }

    public int getNumber() {
        return number;
    }

    public boolean isTaken(){
        return taken;
    }
}
