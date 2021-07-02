package it.polimi.ingsw.messages.toClient.matchData;

/**
 * Message to load the victory points of the slots of development cards
 */
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
