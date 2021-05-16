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
    List<Integer> ownedLeaderCards;
    LightClient thisClient;
    List<LightClient> otherClients;

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
        this.thisClient = new LightClient();
        this.otherClients = new ArrayList<>();
    }

    public void setThisClient(String nickname){
        thisClient.setNickname(nickname);
    }

    public void addLightClient(String nickname){
        LightClient lc = new LightClient();
        lc.setNickname(nickname);
        otherClients.add(lc);

    }

    public void updateInfo(String nickname, int steps){
        LightClient lc = getLightClientByNickname(nickname);
        lc.faithTrackAdvancement(steps);
    }

    private LightClient getLightClientByNickname(String nickname) {
        for(LightClient lc : otherClients){
            if(lc.getNickname().equals(nickname))
                return lc;
        }
        return thisClient;
    }

    public void addChosenLeaderCard(Integer ID){
        thisClient.addChosenLeaderCard(ID);
    }

    public void setAllLeaderCards(List<LightLeaderCard> allLeaderCards){
        this.lightLeaderCards = allLeaderCards;
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

    public LightLeaderCard getLeaderCardByID(int ID){
        for (LightLeaderCard lc : lightLeaderCards){
            if(lc.getID() == ID){
                return lc;
            }
        }
        return null;
    }
}
