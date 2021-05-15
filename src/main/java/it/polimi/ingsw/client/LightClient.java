package it.polimi.ingsw.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class LightClient {

    private int faithTrackPosition;
    List<Integer> ownedLeaderCards;
    Stack<Integer>[] ownedDevelopmentCards;

    public LightClient() {
        this.faithTrackPosition = 0;
        this.ownedLeaderCards = new ArrayList<>();
        this.ownedDevelopmentCards = new Stack[3];
        for(int i = 0; i < 3; i++)
            ownedDevelopmentCards[i] = new Stack<Integer>();
    }


}
