package it.polimi.ingsw.messages.toClient.matchData;

public class NotifyLeaderAction extends MatchDataMessage{
    private int id;
    private boolean discard;

    public NotifyLeaderAction(String nickname, int id, boolean discard) {
        super(nickname);
        this.id = id;
        this.discard = discard;
    }

    public int getId() {
        return id;
    }

    public boolean isDiscard(){
        return discard;
    }
}
