package it.polimi.ingsw.client;

import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchData {

    List<LeaderCard> allLeaderCards;
    List<LightDevelopmentCard> lightDevelopmentCards;

    int faithTrackPosition;
    List<Integer> ownedLeaderCards;
    List<Integer>[] personalBoardSlots;


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

    public List<LeaderCard> getLeaderCardsByID(List<Integer> ids){
        List<LeaderCard> cards = new ArrayList<>();
        for (Integer id : ids)
            cards.add(getLeaderCardByID(id));
        return cards;
    }

    public void setAllDevelopmentCards(List<LightDevelopmentCard> lightDevelopmentCards) {
        this.lightDevelopmentCards = lightDevelopmentCards;
    }

    public LightDevelopmentCard getDevelopmentCardByID(Integer ID){
        for (LightDevelopmentCard ldc : lightDevelopmentCards){
            if(ldc.getID() == ID){
                return ldc;
            }
        }
        return null;
    }
}
