package it.polimi.ingsw.messages.toClient.matchData;

import java.util.ArrayList;
import java.util.List;

public class ReloadDevelopmentCardOwned extends MatchDataMessage{
    private List<String>[] hiddenDevelopmentCardColours;
    private int[] ownedDevelopmentCards;
    public ReloadDevelopmentCardOwned(String nickname, List<String>[] hiddenDevelopmentCardColours, int[] ownedDevelopmentCards) {
        super(nickname);
        this.hiddenDevelopmentCardColours = hiddenDevelopmentCardColours;
        this.ownedDevelopmentCards = ownedDevelopmentCards;

    }

    public List<String>[] getHiddenDevelopmentCardColours() {
        return hiddenDevelopmentCardColours;
    }

    public int[] getOwnedDevelopmentCards() {
        return ownedDevelopmentCards;
    }
}
