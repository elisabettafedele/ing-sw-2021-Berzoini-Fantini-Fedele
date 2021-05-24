package it.polimi.ingsw.model.persistency;

import it.polimi.ingsw.client.PopesTileState;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.player.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class PersistentPlayer implements Serializable {
    private String nickname;
    private int faithTrackPosition;
    private Map<Integer, Boolean> ownedLeaderCards; //<ID, active>
    private Stack<Integer>[] developmentCardSlots;
    private List<Resource>[] warehouse;
    private int[] strongbox;
    private Map<Integer, Integer> leaderDepots;
    private int victoryPoints;
    private PopesTileState[] popesTileStates;
    private boolean active;

    public PersistentPlayer(Player player) {
        this.active = player.isActive();
        nickname = player.getNickname();
        faithTrackPosition = player.getPersonalBoard().getMarkerPosition();
        ownedLeaderCards = new HashMap<>();
        for (LeaderCard card : player.getPersonalBoard().getLeaderCards()){
            ownedLeaderCards.put(card.getID(), card.isActive());
        }
        developmentCardSlots = player.getPersonalBoard().getDevelopmentCardIdSlots();
        warehouse = player.getPersonalBoard().getWarehouse().getWarehouseDepotsStatus();
        strongbox = player.getPersonalBoard().getStrongboxStatus();
        leaderDepots = player.getPersonalBoard().getLeaderStatus();
        victoryPoints = player.getVictoryPoints();
        popesTileStates = player.getPersonalBoard().getPopesTileStates();
    }

    public PersistentPlayer(){}

    public String getNickname() {
        return nickname;
    }

    public int getFaithTrackPosition() {
        return faithTrackPosition;
    }

    public Map<Integer, Boolean> getOwnedLeaderCards() {
        return ownedLeaderCards;
    }

    public Stack<Integer>[] getDevelopmentCardSlots() {
        return developmentCardSlots;
    }

    public List<Resource>[] getWarehouse() {
        return warehouse;
    }

    public int[] getStrongbox() {
        return strongbox;
    }

    public Map<Integer, Integer> getLeaderDepots() {
        return leaderDepots;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public PopesTileState[] getPopesTileStates() {
        return popesTileStates;
    }

    public boolean isActive() {
        return active;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setFaithTrackPosition(int faithTrackPosition) {
        this.faithTrackPosition = faithTrackPosition;
    }

    public void setOwnedLeaderCards(Map<Integer, Boolean> ownedLeaderCards) {
        this.ownedLeaderCards = ownedLeaderCards;
    }

    public void setDevelopmentCardSlots(Stack<Integer>[] developmentCardSlots) {
        this.developmentCardSlots = developmentCardSlots;
    }

    public void setWarehouse(List<Resource>[] warehouse) {
        this.warehouse = warehouse;
    }

    public void setStrongbox(int[] strongbox) {
        this.strongbox = strongbox;
    }

    public void setLeaderDepots(Map<Integer, Integer> leaderDepots) {
        this.leaderDepots = leaderDepots;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public void setPopesTileStates(PopesTileState[] popesTileStates) {
        this.popesTileStates = popesTileStates;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
