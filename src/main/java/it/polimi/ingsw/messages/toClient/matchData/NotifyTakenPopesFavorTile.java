package it.polimi.ingsw.messages.toClient.matchData;

public class NotifyTakenPopesFavorTile extends MatchDataMessage{
    private int number;
    private boolean taken;

    public NotifyTakenPopesFavorTile(String nickname, int start, boolean taken) {
        super(nickname);
        if (start == 5)
            this.number = 0;
        else if (start == 12)
            this.number = 1;
        else
            this.number = 2;
    }

    public int getNumber() {
        return number;
    }

    public boolean isTaken(){
        return taken;
    }
}
