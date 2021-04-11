package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.cards.LeaderCard;

import java.util.List;

public class Player {
    private String nickname;
    private PersonalBoard personalBoard;
    private int victoryPoints;
    private boolean winner;

    public Player(String nickname, List<LeaderCard> leaderCards){
        this.nickname = nickname;
        //this.personalBoard = new PersonalBoard(
    }

}
