package it.polimi.ingsw.messages.toClient.matchData;

import java.util.Map;

public class UpdateOwnedDevelopmentCards extends MatchDataMessage{
    private Map<Integer, Integer> ids;
    private Map<Integer, Integer> victoryPoints;

    public UpdateOwnedDevelopmentCards(String nickname, Map<Integer, Integer> ids, Map<Integer, Integer> victoryPoints) {
        super(nickname);
        this.ids = ids;
        this.victoryPoints = victoryPoints;
    }

    public Map<Integer, Integer> getIds() {
        return ids;
    }

    public Map<Integer, Integer> getVictoryPoints() {
        return victoryPoints;
    }
}
