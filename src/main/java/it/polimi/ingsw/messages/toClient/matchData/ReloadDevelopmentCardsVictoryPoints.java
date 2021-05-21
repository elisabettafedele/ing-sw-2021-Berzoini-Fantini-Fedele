package it.polimi.ingsw.messages.toClient.matchData;

public class ReloadDevelopmentCardsVictoryPoints extends  MatchDataMessage{
    private int[] developmentCardsVictoryPoints;

    public ReloadDevelopmentCardsVictoryPoints(String nickname, int[] developmentCardsVictoryPoints) {
        super(nickname);
        this.developmentCardsVictoryPoints = developmentCardsVictoryPoints;
    }

    public int[] getDevelopmentCardsVictoryPoints() {
        return developmentCardsVictoryPoints;
    }
}
