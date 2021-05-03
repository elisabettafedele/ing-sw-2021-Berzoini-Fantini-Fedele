package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.depot.Depot;
import it.polimi.ingsw.model.depot.LeaderDepot;
import it.polimi.ingsw.model.depot.StrongboxDepot;

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
    private List<LeaderCard> leaderCards;
    private Production defaultProduction;
    private FaithTrack faithTrack;
    private final int numberOfStrongboxDepots = 4;
    private final int numberOfDevelopmentCardSlots = 3;
    private final int numberOfInitialLeaderCards = 4;

    /**
     * @param leaderCards The list of maximum two {@link LeaderCard} to be assigned to the Personal Board
     * @throws InvalidArgumentException leaderCards is null or contains less or more than 2 Leader Cards
     */
    public PersonalBoard( List<LeaderCard> leaderCards) throws InvalidArgumentException {
        if(leaderCards==null||leaderCards.size()!=numberOfInitialLeaderCards){
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
        for(int i=0;i<numberOfDevelopmentCardSlots;i++){
            developmentCardSlots[i]=new Stack<>();
        }
        this.leaderCards = leaderCards;
        Map input = new HashMap();
        input.put(Resource.ANY,2);
        Map output = new HashMap();
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
     * @return Returns warehouse
     */
    public Warehouse getWarehouse(){
        return warehouse;
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

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    /**
     * @return Returns only the {@link DevelopmentCard} on the top of the developmentCardSlots' stacks. They are the only DevelopmentCards that can be utilized.
     */
    public List<DevelopmentCard> availableDevelopmentCards() {
        List<DevelopmentCard> tempList = new ArrayList<>();
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
        for(LeaderCard lc : getLeaderCards()){
            if(lc.isActive()){
                tempList.add(lc);
            }
        }
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
     * @throws InvalidArgumentException slotNumber is negative or greater than two
     * @throws InvalidSlotException Slot is Empty and/or CardToBeAdded level is not a single level higher than the one of the {@link DevelopmentCard} on the top of the slot
     */
    public void addDevelopmentCard(DevelopmentCard CardToBeAdded, int slotNumber) throws InvalidArgumentException,InvalidSlotException{
        if(slotNumber<0||slotNumber>2){
            throw new InvalidArgumentException();
        }
        if(developmentCardSlots[slotNumber].isEmpty()){
            if(CardToBeAdded.getFlag().getFlagLevel().getValue()!=0){
                throw new InvalidSlotException(0);
            }
            developmentCardSlots[slotNumber].push(CardToBeAdded);
            return;
        }
        if(!(CardToBeAdded.getFlag().getFlagLevel().getValue()==1+developmentCardSlots[slotNumber].peek().getFlag().getFlagLevel().getValue())){
            throw new InvalidSlotException(developmentCardSlots[slotNumber].peek().getFlag().getFlagLevel().getValue());
        }
        developmentCardSlots[slotNumber].push(CardToBeAdded);
    }

    /**
     * @return The HashMap returned contains four couples of <Resource,Integer>, one for each {@link Resource} and in the same enumeration's order. The Integer value of each couple is a sum of all the resources of that type in strongbox,warehouse and extra depots (if present).
     * @throws InactiveCardException
     * @throws DifferentEffectTypeException
     * @throws InvalidArgumentException
     */

    public Map<Resource,Integer> countResources() throws InactiveCardException, DifferentEffectTypeException, InvalidArgumentException {
        Map<Resource,Integer> availableResources=new HashMap<>();
        int[] resources=new int[4] ;
        for(int i=0;i<numberOfStrongboxDepots;i++){
            resources[i]=0+strongbox[i].getResourceQuantity();
        }
        for(LeaderCard lc : availableLeaderCards()){
            if(lc.getEffect().getEffectType()== EffectType.EXTRA_DEPOT){
                resources[lc.getEffect().getExtraDepotEffect().getLeaderDepot().getResourceType().getValue()]+=lc.getEffect().getExtraDepotEffect().getLeaderDepot().getResourceQuantity();
            }
        }
        for(int i=0;i < warehouse.getNumberOfDepots();i++){
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
    public void moveMarker(int boxesToMoveQuantity) throws InvalidArgumentException{
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
            if(lc!=null && lc.getEffect().getEffectType().equals(EffectType.PRODUCTION)){
                listOfProductions.add(lc.getEffect().getProductionEffect());
            }
        }
        listOfProductions.add(defaultProduction);
        return listOfProductions;
    }

    /**
     * Method that check if a new development card can be added legally to the Personal Board
     * @param card is the card to add
     * @return true only if the insertion is legal
     */
    public boolean cardInsertionIsLegal(DevelopmentCard card){
        for (Stack<DevelopmentCard> stack : developmentCardSlots)
            if ((stack.isEmpty() && card.getFlag().getFlagLevel() == Level.ONE)|| (!stack.isEmpty() && stack.peek().getFlag().getFlagLevel().getValue() == card.getFlag().getFlagLevel().getValue() - 1))
                return true;
        return false;
    }

    /**
     * Method use to get the available effects of a certain {@link EffectType}
     * @param effectType
     * @return an empty list if no effect of this type is available or a list of effects if at least one is available
     */
    public List<Effect> getAvailableEffects(EffectType effectType){
        //If there is not any leader card active return an empty list
        return this.availableLeaderCards().stream().filter(x -> x.getEffect().getEffectType().equals(effectType)).map(LeaderCard::getEffect).collect(Collectors.toList());
    }

    //TODO finish JavaDoc
    /**
     * Method to add Resources of the same type to the personal board, given a specific {@link ResourceStorageType}
     * @param depot the place where the resource(s) should be stored: it can be first, second, third row of the warehouse depot, the leader depots or the strongbox
     * @param resource the type of the resource to be added
     * @param quantity the quanitty of the resource to be added
     * @throws InvalidDepotException
     * @throws InvalidArgumentException
     * @throws InvalidResourceTypeException
     * @throws InsufficientSpaceException
     */
    public void addResources (ResourceStorageType depot, Resource resource, int quantity) throws InvalidDepotException, InvalidArgumentException, InvalidResourceTypeException, InsufficientSpaceException {
        if (depot == ResourceStorageType.WAREHOUSE)
            throw new InvalidDepotException("Invalid depot: you must specify the specific depot of the Warehouse");

        //depot == Warehouse's depot
        if (depot.getValue() < 3)
            this.getWarehouse().addResourcesToDepot(depot.getValue(), resource, quantity);

        //depot == Strongbox's depot
        else if (depot == ResourceStorageType.STRONGBOX) {
            Map<Resource, Integer> resourcesToAdd = new HashMap<>();
            resourcesToAdd.put(resource, quantity);
            this.addResourcesToStrongbox(resourcesToAdd);
        }

        //depot == Leader card's depot
        else if (depot == ResourceStorageType.LEADER_DEPOT){
            List<Effect> extraDepotEffects = this.getAvailableEffects(EffectType.EXTRA_DEPOT);
            if (extraDepotEffects.isEmpty())
                throw new InvalidDepotException("Invalid depot: there is not any leader card with Extra Depot effect active at the moment");
            List<LeaderDepot> depots = new ArrayList<>();
            for (Effect effect : extraDepotEffects) {
                try {
                    depots.add(effect.getExtraDepotEffect().getLeaderDepot());
                } catch (DifferentEffectTypeException e) {
                    e.printStackTrace();
                    return;
                }
            }
            depots = depots.stream().filter(x -> x.getResourceType() == resource).collect(Collectors.toList());
            if (depots.isEmpty())
                throw new InvalidDepotException("Invalid depot: the leader card with the extra depot for "+resource+" is not present/active in your personal board");
            depots.get(0).addResources(quantity);
        }
    }

    /**
     * Method used to get the available white marble conversion of a Personal Board
     * @return a List with the marbles that can be obtained from a white marble
     */
    public List<Marble> getAvailableConversions(){
        List<Marble> availableConversions = new ArrayList<>();

        //I get the available leader cards' effects
        List<Effect> availableWhiteMarbleEffects = getAvailableEffects(EffectType.WHITE_MARBLE);

        //I add the available marble conversions to the list of the available ones (if any)
        for (Effect effect : availableWhiteMarbleEffects) {
            try {
                availableConversions.add(effect.getWhiteMarbleEffect());
            } catch (DifferentEffectTypeException e) {
                e.printStackTrace();
            }
        }
        return availableConversions;
    }

    public void removeLeaderCard(int ID){
        for (LeaderCard leaderCard : leaderCards){
            if(ID == leaderCard.getID()){
                removeLeaderCard(leaderCard);
            }
        }
    }

}
