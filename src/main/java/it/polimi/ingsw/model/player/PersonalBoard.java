package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Production;
import it.polimi.ingsw.model.cards.Value;
import it.polimi.ingsw.model.depot.Depot;
import it.polimi.ingsw.model.depot.StrongboxDepot;
import it.polimi.ingsw.model.depot.WarehouseDepot;

import java.util.*;
import java.util.function.Supplier;

public class PersonalBoard {
    private StrongboxDepot[] strongbox;
    private Warehouse warehouse;
    private int markerPosition;
    private Stack<DevelopmentCard>[] developmentCardSlots;
    private ArrayList<LeaderCard> leaderCards;
    private Production defaultProduction;
    private FaithTrack faithTrack;

    public PersonalBoard( ArrayList<LeaderCard> leaderCards, FaithTrack faithTrack) throws InvalidArgumentException {
        if(leaderCards==null|| faithTrack==null){
            throw new InvalidArgumentException();
        }
        final int numberOfStrongboxDepots=4;
        strongbox = new StrongboxDepot[numberOfStrongboxDepots];
        strongbox[0]= new StrongboxDepot(Resource.COIN);
        strongbox[1]= new StrongboxDepot(Resource.STONE);
        strongbox[2]= new StrongboxDepot(Resource.SERVANT);
        strongbox[3]= new StrongboxDepot(Resource.SHIELD);
        warehouse = new Warehouse();
        markerPosition = 0;
        developmentCardSlots = new Stack[3];
        this.leaderCards = leaderCards;
        HashMap input = new HashMap();
        input.put(Resource.ANY,2);
        HashMap output = new HashMap();
        output.put(Resource.ANY,1);
        defaultProduction = new Production(new Value(input),new Value(output));// TODO LAMBDA FUNCTION HASHMAP
        this.faithTrack = faithTrack;
    }

    public StrongboxDepot[] getStrongbox() {
        return strongbox;
    }

    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    public int getMarkerPosition() {
        return markerPosition;
    }

    public ArrayList<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public void MoveMarker(int boxesToMoveQuantity){
        markerPosition = markerPosition + boxesToMoveQuantity;
    }

    public ArrayList<DevelopmentCard> availableDevelopmentCards() {
        ArrayList<DevelopmentCard> tempList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            try {
                tempList.add(developmentCardSlots[i].peek());
            } catch ( EmptyStackException emptyStackException){ }
        }
        return tempList;
    }
    public ArrayList<LeaderCard> availableLeaderCards() {
        ArrayList<LeaderCard> tempList = new ArrayList<>();
        LeaderCard tempLeaderCard;
        Iterator<LeaderCard> leaderCardIterator= leaderCards.iterator();
        while(leaderCardIterator.hasNext()){
            tempLeaderCard=leaderCardIterator.next();
            if(tempLeaderCard.isActive()){
                tempList.add(tempLeaderCard);
            }
        }
        return tempList;
    }


    public void removeLeaderCard(LeaderCard leaderCardToBeRemoved)throws NoSuchElementException{
        if(leaderCards.contains(leaderCardToBeRemoved)) {
            leaderCards.remove(leaderCardToBeRemoved);
        }
        else{
            throw new NoSuchElementException();
        }
    }

    public HashMap<Resource,Integer> countResources(){ //TODO FINISH
        HashMap<Resource,Integer> availableResources=new HashMap<>();
        int[] resources=new int[3] ;
        for(int i=0;i<4;i++){
            resources[i]=0+strongbox[i].getResourceQuantity();

        }

        return availableResources;
    }

    public boolean addDevelopmentCard(DevelopmentCard CardToBeAdded, int slotNumber){
        return true;
    }



}
