package it.polimi.ingsw.client;

import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.model.cards.LeaderCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchData {

    List<LightDevelopmentCard> lightDevelopmentCards;
    List<LightLeaderCard> lightLeaderCards;

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
        this.lightLeaderCards = new ArrayList<>();
    }

    public void addChosenLeaderCard(Integer ID){
        ownedLeaderCards.add(ID);
    }

    public void setAllLeaderCards(List<LightLeaderCard> allLeaderCards){
        this.lightLeaderCards = allLeaderCards;
    }


    public LightLeaderCard getLeaderCardByID(int ID){
        for (LightLeaderCard lc : lightLeaderCards){
            if(lc.getID() == ID){
                return lc;
            }
        }
        return null;
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
