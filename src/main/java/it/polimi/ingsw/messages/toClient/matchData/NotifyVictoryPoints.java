package it.polimi.ingsw.messages.toClient.matchData;

public class NotifyVictoryPoints extends MatchDataMessage{

    private int victoryPoints;

    public NotifyVictoryPoints(String nickname, int victoryPoints) {
        super(nickname);
        this.victoryPoints = victoryPoints;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }
}
