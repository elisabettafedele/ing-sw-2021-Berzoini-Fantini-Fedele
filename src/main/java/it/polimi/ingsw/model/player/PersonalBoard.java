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
     * @return Returns marker's position
     */
    public int getMarkerPosition() {
        return markerPosition;
    }

    public boolean isResourceAvailableAndRemove(ResourceStorageType depot, Resource resource, int quantity, boolean wantToRemove){
        if(depot==ResourceStorageType.STRONGBOX){
            for(int i=0; i< 3 ; i++){
                if(strongbox[i].getResourceType()==resource && strongbox[i].getResourceQuantity()>=quantity){
                    if(wantToRemove){
                        try {
                            strongbox[i].removeResources(quantity);
                        } catch (InsufficientQuantityException e) {
                            e.printStackTrace();
                        } catch (InvalidArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        if(depot==ResourceStorageType.LEADER_DEPOT){
            List<Effect> extraDepotEffects = this.getAvailableEffects(EffectType.EXTRA_DEPOT);
            if (extraDepotEffects.isEmpty())
                return false;
            for (Effect effect : extraDepotEffects) {
                try {
                    if(effect.getExtraDepotEffect().getLeaderDepot().getResourceType()==resource&& effect.getExtraDepotEffect().getLeaderDepot().getResourceQuantity()>=quantity){
                        if(wantToRemove){
                            try {
                                effect.getExtraDepotEffect().getLeaderDepot().removeResources(quantity);
                            } catch (InsufficientQuantityException e) {
                                e.printStackTrace();
                            } catch (InvalidArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                } catch (DifferentEffectTypeException e) {
                }
            }
            return false;
        }
        if(depot==ResourceStorageType.WAREHOUSE){
            for(int ii=0;ii<warehouse.getNumberOfDepots();ii++){
                try {
                    if(resource==warehouse.getResourceTypeOfDepot(ii)&&warehouse.getResourceQuantityOfDepot(ii)>=quantity){
                        if(wantToRemove){
                            try {
                                warehouse.removeResourcesFromDepot(resource,quantity);
                            } catch (InvalidResourceTypeException e) {
                                e.printStackTrace();
                            } catch (InsufficientQuantityException e) {
                                e.printStackTrace();
                            }
                        }
                        return true;
                    }
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        return false;
    }


    /**
     * @return Returns all developmentCards as a list
     */
    public List<DevelopmentCard> getDevelopmentCards(){
        List<DevelopmentCard> developmentCards=new ArrayList<>();
        for(int i=0;i<developmentCardSlots.length;i++){
            for(int ii=0; ii<developmentCardSlots[i].size();ii++){
                developmentCards.add(developmentCardSlots[i].get(ii));
            }
        }
        return developmentCards;
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
    public void removeLeaderCard(LeaderCard leaderCardToBeRemoved) throws NoSuchElementException{
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

    public Map<Resource,Integer> countResources() {
        Map<Resource,Integer> availableResources=new HashMap<>();
        int[] resources=new int[4] ;
        for(int i=0;i<numberOfStrongboxDepots;i++){
            resources[i]=0+strongbox[i].getResourceQuantity();
        }
        for(LeaderCard lc : availableLeaderCards()){
            if(lc.getEffect().getEffectType()== EffectType.EXTRA_DEPOT){
                try {
                    resources[lc.getEffect().getExtraDepotEffect().getLeaderDepot().getResourceType().getValue()]+=lc.getEffect().getExtraDepotEffect().getLeaderDepot().getResourceQuantity();
                } catch (DifferentEffectTypeException e) {
                    e.printStackTrace();
                }
            }
        }
        for(int i=0;i < warehouse.getNumberOfDepots();i++){
            try {
                if(!warehouse.getResourceTypeOfDepot(i).equals(Resource.ANY)) {
                    resources[warehouse.getResourceTypeOfDepot(i).getValue()]+= warehouse.getResourceQuantityOfDepot(i);
                }
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
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
    public void addResourcesToStrongbox(Map<Resource,Integer> resourcesToBeAdded) throws InvalidDepotException, InvalidArgumentException {
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
    public boolean cardInsertionIsLegal(DevelopmentCard card) {
        for (Stack<DevelopmentCard> stack : developmentCardSlots) {
            if ((stack.isEmpty() && card.getFlag().getFlagLevel() == Level.ONE) || (!stack.isEmpty() && stack.peek().getFlag().getFlagLevel().getValue() == card.getFlag().getFlagLevel().getValue() - 1)){
                return true;
            }
        }
        return false;
    }
    public boolean cardInsertionIsLegal(DevelopmentCard card, int slotNum) {
            if ((developmentCardSlots[slotNum].isEmpty() && card.getFlag().getFlagLevel() == Level.ONE) || (!developmentCardSlots[slotNum].isEmpty() && developmentCardSlots[slotNum].peek().getFlag().getFlagLevel().getValue() == card.getFlag().getFlagLevel().getValue() - 1)){
                return true;
            }
            return false;
    }

    /**
     * Method use to get the available effects of a certain {@link EffectType}
     * @param effectType
     * @return an empty list if no effect of this type is available or a list of effects if at least one is available
     */
    public List<Effect> getAvailableEffects(EffectType effectType){
        //If there is not any leader card active return an empty list
        return this.availableLeaderCards().stream().filter(x -> (x.isActive() && x.getEffect().getEffectType().equals(effectType))).map(LeaderCard::getEffect).collect(Collectors.toList());
    }

    /**
     * Method to add Resources of the same type to the personal board, given a specific {@link ResourceStorageType}
     * @param depot the place where the resource(s) should be stored: it can be first, second, third row of the warehouse depot, the leader depots or the strongbox
     * @param resource the type of the resource to be added
     * @param quantity the quantity of the resource to be added
     * @throws InvalidDepotException if the depot type is Warehouse, it is too generic, you must provide a more specific one
     * @throws InvalidArgumentException when the quantity is null or negative
     * @throws InvalidResourceTypeException if the resource type provided is ANY
     * @throws InsufficientSpaceException if the depot does not have sufficient space to store the resource
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

    public void removeResources(ResourceStorageType resourceStorageType, Resource resource, int quantity) throws InsufficientQuantityException, InvalidResourceTypeException {
        if (resourceStorageType.getValue() < 3) {
            try {
                getWarehouse().removeResourcesFromDepot(getWarehouse().getResourceTypeOfDepot(resourceStorageType.getValue()), quantity);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
        }
        else if (resourceStorageType == ResourceStorageType.LEADER_DEPOT){
            for (Effect effect : getAvailableEffects(EffectType.EXTRA_DEPOT)){
                try {
                    if (effect.getExtraDepotEffect().getLeaderDepot().getResourceType() == resource)
                        effect.getExtraDepotEffect().getLeaderDepot().removeResources(quantity);
                } catch (DifferentEffectTypeException | InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (resourceStorageType == ResourceStorageType.STRONGBOX){
            try {
                getStrongbox()[resource.getValue()].removeResources(quantity);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }
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

    /**
     * Method used to remove a {@link LeaderCard given its ID}
     * @param ID the ID of the {@link LeaderCard} to remove
     */
    public void removeLeaderCard(int ID){
        for (LeaderCard leaderCard : leaderCards){
            if(ID == leaderCard.getID()){
                removeLeaderCard(leaderCard);
                return;
            }
        }
    }

    /**
     * Method used to check whether there is an available Leader
     * @param resource the {@link Resource} to store
     * @param quantity the quantity of the {@link Resource} to store
     * @return true only if it is possible to store a certain quantity of a certain {@link Resource} in a {@link LeaderDepot}
     */
    public boolean isLeaderDepotAvailable(Resource resource, int quantity){
        List<Effect> effects = getAvailableEffects(EffectType.EXTRA_DEPOT);
        if (effects.isEmpty() || quantity > 2)
            return false;
        for (Effect effect : effects){
            try {
                if (effect.getExtraDepotEffect().getLeaderDepot().getResourceType() == resource && effect.getExtraDepotEffect().getLeaderDepot().getResourceQuantity() < 3 - quantity)
                    return true;
            } catch (DifferentEffectTypeException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int countResourceNumber(){
        Map<Resource, Integer> resources = countResources();
        int quantity = 0;
        for (Resource resource : resources.keySet())
            quantity += resource.getValue();
        return quantity;
    }
}
