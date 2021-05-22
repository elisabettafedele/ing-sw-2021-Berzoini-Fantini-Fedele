package it.polimi.ingsw.client;

import it.polimi.ingsw.enumerations.Resource;

import java.util.*;

public class LightClient {

    private int faithTrackPosition;
    private Map<Integer, Boolean> ownedLeaderCards; //<ID, active>
    //Slot number is the key, the id is the value...if the slot number is not present, it is empty
    //TODO Message
    private int[] victoryPointsDevelopmentCardSlots;
    private String nickname;

    private Stack<Integer>[] developmentCardSlots;
    List<Resource>[] warehouse;
    int[] strongbox;
    Map<Integer, Integer> leaderDepots; //ID, Qty


    //TODO Raffa non serve più, puoi anche rimuovere
    private boolean[] hasTakenPopesTile;
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

        //TODO Raffa non serve più, puoi anche rimuovere
        this.hasTakenPopesTile = new boolean[3];
        this.leaderDepots = new HashMap<>();
    }

    public void addLeaderCard(Integer ID, boolean active) {
        ownedLeaderCards.put(ID, active);
    }

    //TODO: check that uses ID as Integer and not as int
    public void removeLeaderCard(Integer ID) {
        ownedLeaderCards.remove(ID);
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


    public void addDevelopmentCard(int id, int slot, int victoryPoints){
        this.developmentCardSlots[slot].push(id);
        this.victoryPoints += victoryPoints;
    }

    public void updateDepotStatus(List<Resource>[] warehouseDepots, int[] strongboxDepots, Map<Integer, Integer> leaderDepots) {
        this.warehouse = warehouseDepots;
        this.strongbox = strongboxDepots;
        this.leaderDepots = leaderDepots;
    }

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


    //TODO Raffa non serve più, puoi anche rimuovere
    public void updateTakenPopesFavorTile(int number){
        this.hasTakenPopesTile[number] = true;
    }

    public void updatePopeFavorTilesStatus(int number, boolean taken){
        this.popesTileStates[number] = taken ? PopesTileState.TAKEN : PopesTileState.NOT_TAKEN;
    }

    public boolean hasTakenPopesFavorTile(int number){
        return this.hasTakenPopesTile[number];
    }


    //TODO: victoryPointsMessage
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
}
