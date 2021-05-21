package it.polimi.ingsw.messages.toClient.matchData;

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
