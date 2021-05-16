package it.polimi.ingsw.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LightClient {

    private int faithTrackPosition;
    List<Integer> ownedLeaderCards;
    Stack<Integer>[] ownedDevelopmentCards;
    private String nickname;


    //TODO: warehouse and strongbox resources

    public LightClient() {
        this.faithTrackPosition = 0;
        this.ownedLeaderCards = new ArrayList<>();
        this.ownedDevelopmentCards = new Stack[3];
        for(int i = 0; i < 3; i++)
            ownedDevelopmentCards[i] = new Stack<Integer>();
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
