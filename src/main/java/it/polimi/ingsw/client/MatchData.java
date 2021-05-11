package it.polimi.ingsw.client;

import it.polimi.ingsw.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchData {
    List<Integer> ownedLeaderCards;
    List<LeaderCard> allLeaderCards;

    Map<Integer, List<String>> lightDevelopmentCards;

    private static MatchData instance;

    public static MatchData getInstance(){
        if (instance == null){
            instance = new MatchData();
        }
        return instance;
    }

    private MatchData(){
        this.ownedLeaderCards = new ArrayList<>();
        this.allLeaderCards = new ArrayList<>();
    }

    public void addChosenLeaderCard(Integer ID){
        ownedLeaderCards.add(ID);
    }

    public void setAllLeaderCards(List<LeaderCard> allLeaderCards){
        this.allLeaderCards = allLeaderCards;
    }

    public List<Integer> getOwnedLeaderCards(){
        return this.ownedLeaderCards;
    }

    public LeaderCard getLeaderCardByID(int ID){
        for (LeaderCard lc : allLeaderCards){
            if(lc.getID() == ID){
                return lc;
            }
        }
        return null;
    }

    public void setAllDevelopmentCards(Map<Integer, List<String>> lightDevelopmentCards) {
        this.lightDevelopmentCards = lightDevelopmentCards;
    }

    public List<String> getDevelopmentCardByID(Integer ID){
        return lightDevelopmentCards.get(ID);
    }
}
