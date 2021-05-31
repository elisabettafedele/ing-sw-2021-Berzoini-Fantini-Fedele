package it.polimi.ingsw.messages.toClient.matchData;

import java.util.List;

public class LoadDevelopmentCardGrid extends MatchDataMessage {
    private List<Integer> availableCardsIds;

    public LoadDevelopmentCardGrid(String nickname, List<Integer> availableCardsIds) {
        super(nickname);
        this.availableCardsIds = availableCardsIds;
    }

    public List<Integer> getAvailableCardsIds() {
        return availableCardsIds;
    }
}
