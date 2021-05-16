package it.polimi.ingsw.client;

import it.polimi.ingsw.enumerations.Resource;

import java.util.*;

public class LightClient {

    private int faithTrackPosition;
    private List<Integer> ownedLeaderCards;
    private Stack<Integer>[] ownedDevelopmentCards;
    private String nickname;

    List<Resource>[] warehouse;
    Map<Resource, Integer> strongbox;

    public LightClient() {
        this.faithTrackPosition = 0;
        this.ownedLeaderCards = new ArrayList<>();
        this.ownedDevelopmentCards = new Stack[3];
        this.warehouse = new ArrayList[3];
        for(int i = 0; i < 3; i++) {
            ownedDevelopmentCards[i] = new Stack<Integer>();
            warehouse[i] = new ArrayList<>();
        }
        this.strongbox = new HashMap<>();
    }

    public void addChosenLeaderCard(Integer ID){
        ownedLeaderCards.add(ID);
    }

    //TODO: check that uses ID as Integer and not as int
    public void removeLeaderCard(Integer ID){
        ownedLeaderCards.remove(ID);
    }

    public void faithTrackAdvancement(int steps){
        faithTrackPosition += steps;
    }

    public void addDevelopmentCard(Integer ID, int slot){
        ownedDevelopmentCards[slot].push(ID);
    }

    public int getFaithTrackPosition() {
        return faithTrackPosition;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
