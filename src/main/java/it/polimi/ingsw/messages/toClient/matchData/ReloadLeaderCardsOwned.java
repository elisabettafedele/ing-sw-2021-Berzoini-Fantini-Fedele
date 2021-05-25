package it.polimi.ingsw.messages.toClient.matchData;

import java.util.Map;

public class ReloadLeaderCardsOwned extends MatchDataMessage{
    private Map<Integer, Boolean> cards;
    public ReloadLeaderCardsOwned(String nickname, Map<Integer, Boolean> cards) {
        super(nickname);
        this.cards = cards;
    }

    public Map<Integer, Boolean> getCards() {
        return cards;
    }
}