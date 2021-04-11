package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Production;
import it.polimi.ingsw.model.cards.Value;
import it.polimi.ingsw.model.depot.Depot;
import it.polimi.ingsw.model.depot.StrongboxDepot;
import it.polimi.ingsw.model.depot.WarehouseDepot;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The class represents the {@link Player}'s Personal Board.It includes all the attributes and that PersonalBoard has.
 */
public class PersonalBoard {
    private StrongboxDepot[] strongbox;
    private Warehouse warehouse;
    private int markerPosition;
    private Stack<DevelopmentCard>[] developmentCardSlots;
    private ArrayList<LeaderCard> leaderCards;
    private Production defaultProduction;
    private FaithTrack faithTrack;
    private final int numberOfStrongboxDepots=4;
    private final int numberOfDevelopmentCardSlots=3;

    /**
     * @param leaderCards The list of maximum two {@link LeaderCard} to be assigned to the Personal Board
     * @throws InvalidArgumentException leaderCards is null
     */
    public PersonalBoard( ArrayList<LeaderCard> leaderCards) throws InvalidArgumentException {
        if(leaderCards==null){
            throw new InvalidArgumentException();
        }
        faithTrack=FaithTrack.instance();
        strongbox = new StrongboxDepot[numberOfStrongboxDepots];
        strongbox[0]= new StrongboxDepot(Resource.COIN);
        strongbox[1]= new StrongboxDepot(Resource.STONE);
        strongbox[2]= new StrongboxDepot(Resource.SERVANT);
        strongbox[3]= new StrongboxDepot(Resource.SHIELD);
        warehouse = new Warehouse();
        markerPosition = 0;
        developmentCardSlots = new Stack[numberOfDevelopmentCardSlots];
        this.leaderCards = leaderCards;
        HashMap input = new HashMap();
        input.put(Resource.ANY,2);
        HashMap output = new HashMap();
        output.put(Resource.ANY,1);
        defaultProduction = new Production(new Value(null,input,0),new Value( null,output,0));
        this.faithTrack = faithTrack;
    }

    /**
     *
     * @return Returns strongbox
     */
    public Depot[] getStrongbox() {
        return strongbox;
    }

    /**
     *
     * @return Returns FaithTrack
     */
    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    /**
     *
     * @return Returns marker's position
     */
    public int getMarkerPosition() {
        return markerPosition;
    }

    /**
     * @return Returns the whole developmentCardSlots structure ( 3 stacks of {@link DevelopmentCard})
     */
    public Stack<DevelopmentCard>[] getDevelopmentCards(){
        return developmentCardSlots;
    }

    /**
     * @return Returns all {@link LeaderCard}
     */

    public ArrayList<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    /**
     * @return Returns only the {@link DevelopmentCard} on the top of the developmentCardSlots' stacks. They are the only DevelopmentCards that can be utilized.
     */
    public ArrayList<DevelopmentCard> availableDevelopmentCards() {
        ArrayList<DevelopmentCard> tempList = new ArrayList<>();
        for (int i = 0; i < numberOfDevelopmentCardSlots; i++) {
            try {
                tempList.add(developmentCardSlots[i].peek());
            } catch ( EmptyStackException emptyStackException){ }
        }
        return tempList;
    }

    /**
     * @return Returns only the active {@link LeaderCard}
     */
    public List<LeaderCard> availableLeaderCards() {
        List<LeaderCard> tempList = new ArrayList<>();
        tempList=getLeaderCards().stream()
                .filter(leaderCard -> leaderCard.isActive())
                .collect(Collectors.toList());
        return tempList;
    }

    /**
     * Removes leaderCardToBeRemoved from leaderCards list.
     * @param leaderCardToBeRemoved The {@link LeaderCard} to  be removed
     * @throws NoSuchElementException leaderCards list doesn't contain leaderCardToBeRemoved
     */
    public void removeLeaderCard(LeaderCard leaderCardToBeRemoved)throws NoSuchElementException{
        if(leaderCards.contains(leaderCardToBeRemoved)) {
            leaderCards.remove(leaderCardToBeRemoved);
        }
        else{
            throw new NoSuchElementException();
        }
    }

    /**
     * @param CardToBeAdded The {@link DevelopmentCard} to be added
     * @param slotNumber the number of slot where CardToBeAdded wants to be added. Slots available are Slot 0,1 and 2.
     * @return Returns true if the card was successfully added, false if CardToBeAdded level is wrong, it needs to be a single level higher than the one of the Card currently on the top of the Slot.
     * @throws FullSlotException The Slot chosen already contains the maximum amount of cards (three)
     * @throws InvalidArgumentException slotNumber is negative or greater than two
     */
    public boolean addDevelopmentCard(DevelopmentCard CardToBeAdded, int slotNumber) throws InvalidArgumentException,FullSlotException{
        if(slotNumber<0||slotNumber>2){
            throw new InvalidArgumentException();
        }
        if(developmentCardSlots[slotNumber].size()==3){
            throw new FullSlotException();
        }
        if(CardToBeAdded.getFlag().getFlagLevel().getValue()==1+developmentCardSlots[slotNumber].peek().getFlag().getFlagLevel().getValue()){
            developmentCardSlots[slotNumber].push(CardToBeAdded);
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * @return The HashMap returned contains four couples of <Resource,Integer>, one for each {@link Resource} and in the same enumeration's order. The Integer value of each couple is a sum of all the resources of that type in strongbox,warehouse and extra depots (if present).
     * @throws InactiveCardException
     * @throws DifferentEffectTypeException
     * @throws InvalidArgumentException
     */

    public HashMap<Resource,Integer> countResources() throws InactiveCardException, DifferentEffectTypeException, InvalidArgumentException {
        HashMap<Resource,Integer> availableResources=new HashMap<>();
        int[] resources=new int[3] ;
        for(int i=0;i<numberOfStrongboxDepots;i++){
            resources[i]=0+strongbox[i].getResourceQuantity();
        }
        for(LeaderCard lc : availableLeaderCards()){
            if(lc.getEffect().getEffectType()== EffectType.EXTRA_DEPOT){
                resources[lc.getEffect().getExtraDepotEffect().getLeaderDepot().getResourceType().getValue()]+=lc.getEffect().getExtraDepotEffect().getLeaderDepot().getResourceQuantity();
            }
        }
        for(int i=0;i<3;i++){
            if(!warehouse.getResourceTypeOfDepot(i).equals(Resource.ANY)) {
                resources[warehouse.getResourceTypeOfDepot(i).getValue()]+= warehouse.getResourceQuantityOfDepot(i);
            }
        }
        for(int i=0;i<4;i++){
            availableResources.put(Resource.valueOf(i),resources[i]);
        }
        return availableResources;
    }


    /**
     *
     * @param resourcesToBeAdded An HashMap of resources to be added to strongbox
     * @throws InvalidDepotException
     * @throws InvalidArgumentException
     */
    public void addResourcesToStrongbox(Map<Resource,Integer> resourcesToBeAdded) throws InvalidDepotException, InvalidArgumentException, InsufficientSpaceException {
        for(Map.Entry<Resource,Integer> entry : resourcesToBeAdded.entrySet()){
            strongbox[entry.getKey().getValue()].addResources(entry.getValue());
        }
    }

    /**
     *
     * @param boxesToMoveQuantity The amount of boxes that {@link PersonalBoard}'s marker needs to be moved ahead.
     * @throws InvalidArgumentException boxesToMoveQuantity is negative
     */
    public void MoveMarker(int boxesToMoveQuantity) throws InvalidArgumentException{
        if(boxesToMoveQuantity<0){
            throw new InvalidArgumentException();
        }
        markerPosition = markerPosition + boxesToMoveQuantity;
    }

    /**
     *
     * @return Returns the number of all {@link DevelopmentCard} in developmentCardSlots
     */
    public int getNumOfDevelopmentCards(){
        int numOfDevelopmentCards=0;
        for(int i=0;i<numberOfDevelopmentCardSlots;i++){
            numOfDevelopmentCards+=developmentCardSlots[i].size();
        }
        return numOfDevelopmentCards;
    }

    /**
     *
     * @return Returns all the {@link Production} that {@link PersonalBoard} has, the ones on {@link DevelopmentCard}, on {@link LeaderCard} and the default production
     * @throws InactiveCardException
     * @throws DifferentEffectTypeException
     */
    public List<Production> getAvailableProductions() throws InactiveCardException, DifferentEffectTypeException {
        List<Production> listOfProductions=new ArrayList<>();
        availableDevelopmentCards().stream()
                .forEach(developmentCard -> listOfProductions.add(developmentCard.getProduction()));
        for(LeaderCard lc : availableLeaderCards()){
            if(lc.getEffect().getEffectType().equals(EffectType.PRODUCTION)){
                listOfProductions.add(lc.getEffect().getProductionEffect());
            }
        }
        listOfProductions.add(defaultProduction);
        return listOfProductions;
    }


}
