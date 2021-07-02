package it.polimi.ingsw.client;

import it.polimi.ingsw.enumerations.Resource;

import java.util.*;

/**
 * This class represent a light version of the player and the
 * {@link it.polimi.ingsw.model.player.PersonalBoard} of the Model
 */
public class LightClient {

    private int faithTrackPosition;
    private Map<Integer, Boolean> ownedLeaderCards; //<ID, active>
    //Slot number is the key, the id is the value...if the slot number is not present, it is empty
    private int[] victoryPointsDevelopmentCardSlots;
    private String nickname;

    private Stack<Integer>[] developmentCardSlots;
    List<Resource>[] warehouse;
    int[] strongbox;
    Map<Integer, Integer> leaderDepots; //ID, Qty

    private int victoryPoints;

    private PopesTileState[] popesTileStates;


    public LightClient() {
        this.faithTrackPosition = 0;
        this.victoryPoints = 0;
        this.ownedLeaderCards = new HashMap<>();
        this.warehouse = new ArrayList[3];
        for (int i = 0; i < 3; i++) {
            warehouse[i] = new ArrayList<>();
        }
        this.strongbox = new int[4];
        this.victoryPointsDevelopmentCardSlots = new int[3];
        this.popesTileStates = new PopesTileState[]{PopesTileState.NOT_REACHED, PopesTileState.NOT_REACHED, PopesTileState.NOT_REACHED};
        this.developmentCardSlots = new Stack[3];
        for (int i = 0; i < developmentCardSlots.length; i++)
            developmentCardSlots[i] = new Stack<>();
        this.leaderDepots = new HashMap<>();
    }

    /**
     * Method to add a leader card to a player
     * @param ID the ID of the card
     * @param active true if the card is active
     */
    public void addLeaderCard(Integer ID, boolean active) {
        ownedLeaderCards.put(ID, active);
    }

    /**
     * Method to remove a player's leader card
     * @param ID the ID of the card
     */
    public void removeLeaderCard(Integer ID) {
        ownedLeaderCards.remove(ID);
    }

    public void addDevelopmentCard(int id, int slot, int victoryPoints){
        this.developmentCardSlots[slot].push(id);
        this.victoryPoints += victoryPoints;
    }

    /**
     * Method to update the warehouse, the strongbox and leader depots client-side
     * @param warehouseDepots
     * @param strongboxDepots
     * @param leaderDepots
     */
    public void updateDepotStatus(List<Resource>[] warehouseDepots, int[] strongboxDepots, Map<Integer, Integer> leaderDepots) {
        this.warehouse = warehouseDepots;
        this.strongbox = strongboxDepots;
        this.leaderDepots = leaderDepots;
    }

    /**
     * Update the marker position of the client
     * @param position the new marker position on the {@link it.polimi.ingsw.model.game.FaithTrack}
     */
    public void updateMarkerPosition(int position){
        faithTrackPosition = position;
    }

    public void activateLeader(int id){
        if (ownedLeaderCards.containsKey(id))
            ownedLeaderCards.replace(id, true);
        else
            ownedLeaderCards.put(id, true);
    }

    public void setDevelopmentCardSlots(Stack[] developmentCardSlots){
        this.developmentCardSlots = developmentCardSlots;
    }

    public boolean leaderCardIsActive(int id){
        return ownedLeaderCards.get(id);
    }


    public List<Integer> getOwnedLeaderCards() {
        return new ArrayList<Integer>(ownedLeaderCards.keySet());
    }


    public void updatePopeFavorTilesStatus(int number, boolean taken){
        this.popesTileStates[number] = taken ? PopesTileState.TAKEN : PopesTileState.NOT_TAKEN;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public Stack<Integer>[] getDevelopmentCardSlots() {
        return developmentCardSlots;
    }

    public Map<Integer, Integer> getLeaderDepots() {
        return leaderDepots;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public List<Resource>[] getWarehouse() {
        return warehouse;
    }

    public int[] getStrongbox() {
        return strongbox;
    }

    public void reloadLeaderCards(Map<Integer, Boolean> cards){
        this.ownedLeaderCards = cards;
    }

    public void setPopesTileStates(PopesTileState[] popesTileStates){
        this.popesTileStates = popesTileStates;
    }

    public void setVictoryPointsDevelopmentCardSlots(int[] victoryPointsDevelopmentCardSlots){
        this.victoryPointsDevelopmentCardSlots = victoryPointsDevelopmentCardSlots;
    }

    public int getFaithTrackPosition() {
        return faithTrackPosition;
    }

    public PopesTileState[] getPopesTileStates() {
        return popesTileStates;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
