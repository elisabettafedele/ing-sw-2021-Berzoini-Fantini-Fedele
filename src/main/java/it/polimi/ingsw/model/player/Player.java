package it.polimi.ingsw.model.player;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game.FaithTrack;
import it.polimi.ingsw.model.persistency.PersistentPlayer;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.observe.Observable;

import java.io.Serializable;
import java.util.List;

public class Player extends Observable {
    private String nickname;
    private PersonalBoard personalBoard;
    private int victoryPoints;
    private boolean winner;
    private boolean active;
    private boolean inkwell;

    /**
     * Constructor of the class player
     * @param nickname unique id of the player
     * @param leaderCards a list of 4 leader cards given by the Controller in the set up phase of the game
     * @throws InvalidArgumentException
     */
    public Player(String nickname, List<LeaderCard> leaderCards) throws InvalidArgumentException {
        this.nickname = nickname;
        this.personalBoard = new PersonalBoard(leaderCards);
        this.victoryPoints = 0;
        this.winner = false;
        this.active = true;
    }

    public Player(PersistentPlayer persistentPlayer){
        nickname = persistentPlayer.getNickname();
        victoryPoints = persistentPlayer.getVictoryPoints();
        active = persistentPlayer.isActive();
        personalBoard = new PersonalBoard(persistentPlayer);
    }

    /**
     * @return the {@link PersonalBoard} of the {@link Player}
     */
    public PersonalBoard getPersonalBoard() {
        return personalBoard;
    }

    /**
     * @return the nickname of the {@link Player}
     */
    public String getNickname(){
        return nickname;
    }

    /**
     * @return the victory points of the {@link Player}
     */
    public int getVictoryPoints(){
        return victoryPoints;
    }

    public boolean isActive(){
        return active;
    }

    /**
     * setter of the attribute winner
     * @param isWinner true only if the {@link Player} is the winner of the game
     */
    public void setWinner(boolean isWinner){
        winner = isWinner;
    }

    /**
     * @return true only if the {@link Player} is the winner of the game
     */
    public boolean isWinner(){
        return winner;
    }

    /**
     * Method used to add victory point to a specific player
     * @param points number of points to add
     */
    public void addVictoryPoints(int points) throws InvalidArgumentException {
        if(points < 0){
            throw new InvalidArgumentException("negative points added");
        }
        this.victoryPoints += points;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public boolean hasInkwell() {
        return inkwell;
    }

    public void setInkwell(boolean inkwell) {
        this.inkwell = inkwell;
    }

    public int countPoints(){
        int victoryPoints = 0;
        victoryPoints += getPersonalBoard().getDevelopmentCards().stream().map(Card::getVictoryPoints).mapToInt(i -> i).sum();
        victoryPoints += getPersonalBoard().getLeaderCards().stream().filter(LeaderCard::isActive).map(Card::getVictoryPoints).mapToInt(i -> i).sum();
        victoryPoints += getPersonalBoard().countResourceNumber() / 5;
        victoryPoints += FaithTrack.getVictoryPoints(personalBoard.getMarkerPosition());
        victoryPoints += this.victoryPoints;
        return victoryPoints;
    }


}
