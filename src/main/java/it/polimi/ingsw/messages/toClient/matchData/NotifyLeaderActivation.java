package it.polimi.ingsw.messages.toClient.matchData;

public class NotifyLeaderActivation extends MatchDataMessage{
    int id;

    public NotifyLeaderActivation(String nickname, int id) {
        super(nickname);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
