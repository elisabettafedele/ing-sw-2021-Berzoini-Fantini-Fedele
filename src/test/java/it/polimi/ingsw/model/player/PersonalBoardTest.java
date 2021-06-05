package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enumerations.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.jsonParsers.DevelopmentCardParser;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.depot.LeaderDepot;
import it.polimi.ingsw.jsonParsers.LeaderCardParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class PersonalBoardTest {
    PersonalBoard personalBoard;
    DevelopmentCard basicDevelopmentCard1;
    DevelopmentCard basicDevelopmentCard2;
    LeaderCard basicLeaderCard1;
    LeaderCard basicLeaderCard2;
    LeaderCard basicLeaderCard3;
    LeaderCard basicLeaderCard4;
    Value basicValue;
    Production basicProduction;
    @Before
    public void setUp() throws Exception {
        Map<Resource, Integer> hm=new HashMap<>();
        hm.put(Resource.COIN,5);
        basicValue=new Value(null,hm,0);
        basicProduction=new Production(basicValue,basicValue);
        basicLeaderCard1=new LeaderCard(3,basicValue,new Effect(basicProduction),null,null);
        basicLeaderCard2=new LeaderCard(2,basicValue,new Effect(basicProduction),null,null);
        basicLeaderCard3=new LeaderCard(2,basicValue,new Effect(basicProduction),null,null);
        basicLeaderCard4=new LeaderCard(2,basicValue,new Effect(basicProduction),null,null);
        ArrayList<LeaderCard> leaderCardList=new ArrayList<>();
        leaderCardList.add(basicLeaderCard1);
        leaderCardList.add(basicLeaderCard2);
        leaderCardList.add(basicLeaderCard3);
        leaderCardList.add(basicLeaderCard4);
        basicLeaderCard2.activate();
        personalBoard=new PersonalBoard(leaderCardList);
        basicDevelopmentCard1=new DevelopmentCard(3,basicValue,new Flag(FlagColor.BLUE, Level.ONE),basicProduction,null,null);
        basicDevelopmentCard2=new DevelopmentCard(2,basicValue,new Flag(FlagColor.PURPLE, Level.TWO),basicProduction,null,null);
        personalBoard.addDevelopmentCard(basicDevelopmentCard1,0);
        personalBoard.addDevelopmentCard(basicDevelopmentCard2,0);


    }

    @Test (expected = InvalidArgumentException.class)
    public void personalBoard_constructorCorrectlyThrowsExceptionNullNotTwoLeaderCards() throws InvalidArgumentException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        PersonalBoard pb=new PersonalBoard(lcl);
    }


    @Test
    public void testGetStrongbox(){
        assertEquals(personalBoard.getStrongbox()[0].getResourceQuantity(), 0);
        assertEquals(personalBoard.getStrongbox()[1].getResourceQuantity(), 0);
        assertEquals(personalBoard.getStrongbox()[2].getResourceQuantity(), 0);
        assertEquals(personalBoard.getStrongbox()[3].getResourceQuantity(), 0);
        assertEquals(personalBoard.getStrongbox()[0].getResourceType(), Resource.valueOf(0));
        assertEquals(personalBoard.getStrongbox()[1].getResourceType(), Resource.valueOf(1));
        assertEquals(personalBoard.getStrongbox()[2].getResourceType(), Resource.valueOf(2));
        assertEquals(personalBoard.getStrongbox()[3].getResourceType(), Resource.valueOf(3));
    }

    @Test
    public void testGetWarehouse() throws InvalidArgumentException {
        assertEquals(personalBoard.getWarehouse().getResourceQuantityOfDepot(0), 0);
        assertEquals(personalBoard.getWarehouse().getResourceQuantityOfDepot(1), 0);
        assertEquals(personalBoard.getWarehouse().getResourceQuantityOfDepot(2), 0);
        assertEquals(personalBoard.getWarehouse().getResourceTypeOfDepot(0), Resource.ANY);
        assertEquals(personalBoard.getWarehouse().getResourceTypeOfDepot(1), Resource.ANY);
        assertEquals(personalBoard.getWarehouse().getResourceTypeOfDepot(2), Resource.ANY);
    }

    @Test
    public void testGetDevelopmentCards(){
        assertEquals(personalBoard.getDevelopmentCards().size(), 2);
    }

//DOUBLE TEST
    @Test
    public void getMarkerPosition_returnsCorrectMarkerPosition_AND_moveMarker_correctlyMovesMarker() throws InvalidArgumentException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        assertEquals(0,pb.getMarkerPosition());
        pb.moveMarker(5);
        assertEquals(5,pb.getMarkerPosition());
    }

    @Test
    public void testCardInsertionIsLegal() throws InvalidArgumentException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb = new PersonalBoard(lcl);
        assertFalse(pb.cardInsertionIsLegal(basicDevelopmentCard2));
        assertTrue(pb.cardInsertionIsLegal(basicDevelopmentCard1));
    }

    @Test
    public void testGetAvailableEffects() throws InvalidArgumentException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.getLeaderCards().get(0).activate();
        List <Effect> productionEffects = new ArrayList<>();
        productionEffects.add(new Effect(basicProduction));
        assertTrue(pb.getAvailableEffects(EffectType.PRODUCTION).containsAll(productionEffects));
        assertTrue(productionEffects.containsAll(pb.getAvailableEffects(EffectType.PRODUCTION)));
        pb.getLeaderCards().get(1).activate();
        productionEffects.add(new Effect(basicProduction));
        assertTrue(pb.getAvailableEffects(EffectType.PRODUCTION).containsAll(productionEffects));
        assertTrue(productionEffects.containsAll(pb.getAvailableEffects(EffectType.PRODUCTION)));
        assertTrue(pb.getAvailableEffects(EffectType.DISCOUNT).isEmpty());
    }

    @Test
    public void getLeaderCards_returnsCorrectLeaderCards() throws InvalidArgumentException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        assertEquals(lcl,pb.getLeaderCards());
    }

    @Test
    public void availableDevelopmentCards_returnsAvailableDevelopmentCardsOnly() throws InvalidArgumentException, InvalidSlotException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        pb.addDevelopmentCard(basicDevelopmentCard1,0);
        pb.addDevelopmentCard(basicDevelopmentCard2,0);
        List<DevelopmentCard> dc=pb.availableDevelopmentCards();
        assertTrue(dc.size()==1);
        assertEquals(dc.get(0),basicDevelopmentCard2);
    }

    @Test
    public void availableLeaderCards_returnsAvailableLeaderCardsOnly() throws InvalidArgumentException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        LeaderCard differentLeaderCard2=new LeaderCard(2,basicValue,new Effect(basicProduction),null,null);
        differentLeaderCard2.activate();
        lcl.add(basicLeaderCard1);
        lcl.add(differentLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        assertTrue(pb.availableLeaderCards().size()==1);
        assertTrue(pb.availableLeaderCards().contains(differentLeaderCard2));
    }

    @Test
    public void removeLeaderCard_correctlyRemovesOnlyGivenLeaderCard() throws InvalidArgumentException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        pb.removeLeaderCard(basicLeaderCard2);
        assertFalse(pb.getLeaderCards().contains(basicLeaderCard2));
        assertTrue(pb.getLeaderCards().contains(basicLeaderCard1));
    }

    @Test
    public void testAddResourceFirstRowWarehouse() throws InvalidArgumentException, InvalidDepotException, InvalidResourceTypeException, InsufficientSpaceException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        pb.addResources(ResourceStorageType.WAREHOUSE_FIRST_DEPOT, Resource.STONE, 1);
        assertEquals(pb.getWarehouse().getResourceTypeOfDepot(0), Resource.STONE);
        assertEquals(pb.getWarehouse().getResourceQuantityOfDepot(0), 1);
        assertEquals(pb.getWarehouse().getResourceTypeOfDepot(1), Resource.ANY);
        assertEquals(pb.getWarehouse().getResourceQuantityOfDepot(1), 0);
        assertEquals(pb.getWarehouse().getResourceTypeOfDepot(2), Resource.ANY);
        assertEquals(pb.getWarehouse().getResourceQuantityOfDepot(2), 0);
    }

    @Test
    public void testAddResourceStrongbox() throws InvalidArgumentException, InvalidDepotException, InvalidResourceTypeException, InsufficientSpaceException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        pb.addResources(ResourceStorageType.STRONGBOX, Resource.STONE, 1);
        assertEquals(pb.getStrongbox()[Resource.STONE.getValue()].getResourceType(), Resource.STONE);
        assertEquals(pb.getStrongbox()[Resource.STONE.getValue()].getResourceQuantity(), 1);
        assertEquals(pb.getStrongbox()[Resource.COIN.getValue()].getResourceType(), Resource.COIN);
        assertEquals(pb.getStrongbox()[Resource.COIN.getValue()].getResourceQuantity(), 0);
        assertEquals(pb.getStrongbox()[Resource.SHIELD.getValue()].getResourceType(), Resource.SHIELD);
        assertEquals(pb.getStrongbox()[Resource.SHIELD.getValue()].getResourceQuantity(), 0);
        assertEquals(pb.getStrongbox()[Resource.SERVANT.getValue()].getResourceType(), Resource.SERVANT);
        assertEquals(pb.getStrongbox()[Resource.SERVANT.getValue()].getResourceQuantity(), 0);
    }

    @Test
    public void testAddResourceLeaderCard() throws InvalidArgumentException, InvalidDepotException, InvalidResourceTypeException, InsufficientSpaceException, DifferentEffectTypeException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        PersonalBoard pb=new PersonalBoard(lcl);
        lcl.forEach(x -> x.activate());
        pb.addResources(ResourceStorageType.LEADER_DEPOT, Resource.STONE, 1);
        List<Effect> effects = pb.getAvailableEffects(EffectType.EXTRA_DEPOT);
        for (Effect effect : effects)
            if (effect.getExtraDepotEffect().getLeaderDepot().getResourceType() == Resource.STONE)
                assertEquals(effect.getExtraDepotEffect().getLeaderDepot().getResourceQuantity(), 1);
            else
                assertEquals(effect.getExtraDepotEffect().getLeaderDepot().getResourceQuantity(), 0);
    }

    @Test (expected = InvalidDepotException.class)
    public void testInvalidDepotAddResource() throws InvalidArgumentException, InvalidDepotException, InvalidResourceTypeException, InsufficientSpaceException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.addResources(ResourceStorageType.LEADER_DEPOT, Resource.SERVANT, 2);
    }


    @Test(expected = NoSuchElementException.class)
    public void removeLeaderCard_correctlyThrowsNoSuchElementException() throws InvalidArgumentException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        pb.removeLeaderCard(basicLeaderCard2);
        pb.removeLeaderCard(basicLeaderCard2);
    }

    @Test
    public void addDevelopmentCard_correctlyAddsCard() throws InvalidArgumentException,  InvalidSlotException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        pb.addDevelopmentCard(basicDevelopmentCard1,0);
        assertTrue(pb.availableDevelopmentCards().contains(basicDevelopmentCard1));
    }
    @Test(expected = InvalidArgumentException.class)
    public void addDevelopmentCard_correctlyThrowsInvalidArgumentException() throws InvalidArgumentException,  InvalidSlotException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        pb.addDevelopmentCard(basicDevelopmentCard1,3);
    }
    @Test(expected = InvalidSlotException.class)
    public void addDevelopmentCard_correctlyThrowsFullSlotExceptionLevelOneOnLevelOne() throws InvalidArgumentException,  InvalidSlotException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        pb.addDevelopmentCard(basicDevelopmentCard1,0);
        pb.addDevelopmentCard(basicDevelopmentCard1,0);
    }
    @Test(expected = InvalidSlotException.class)
    public void addDevelopmentCard_correctlyThrowsFullSlotExceptionLevelTwoOnEmpty() throws InvalidArgumentException,  InvalidSlotException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(basicLeaderCard2);
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        PersonalBoard pb=new PersonalBoard(lcl);
        pb.addDevelopmentCard(basicDevelopmentCard2,0);
    }

    //DOUBLE TEST
    @Test
    public void countResources_correctlyReturnsResources_AND_addResourcesToStrongbox_correctlyAddsResources() throws InvalidArgumentException, InactiveCardException, DifferentEffectTypeException, InsufficientSpaceException, InvalidDepotException, InvalidResourceTypeException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        lcl.add(new LeaderCard(3,basicValue,new Effect(new ExtraDepot(new LeaderDepot(Resource.COIN))),null,null));
        lcl.add(basicLeaderCard3);
        lcl.add(basicLeaderCard4);
        lcl.get(1).activate();
        lcl.get(1).getEffect().getExtraDepotEffect().getLeaderDepot().addResources(1);//1 COIN
        PersonalBoard pb=new PersonalBoard(lcl);
        Map<Resource, Integer> resourcesToBeAdded=new HashMap();
        resourcesToBeAdded.put(Resource.COIN,2);
        resourcesToBeAdded.put(Resource.SERVANT,5);
        pb.addResourcesToStrongbox(resourcesToBeAdded);//2 COINS AND 5 SERVANTS
        pb.getWarehouse().addResourcesToDepot(1,Resource.SHIELD,1); //1 SHIELD
        Map<Resource, Integer> availableResources= pb.countResources();
        assertTrue(availableResources.get(Resource.COIN).equals(3));
        assertTrue(availableResources.get(Resource.STONE).equals(0));
        assertTrue(availableResources.get(Resource.SERVANT).equals(5));
        assertTrue(availableResources.get(Resource.SHIELD).equals(1));
    }

    @Test(expected = InvalidArgumentException.class)
    public void moveMarker_correctlyThrowsInvalidArgumentException() throws InvalidArgumentException {
        personalBoard.moveMarker(-2);
    }


    @Test
    public void getNumOfDevelopmentCards_correctlyReturnsNumOfDevelopmentCards() {
        assertTrue(personalBoard.getNumOfDevelopmentCards()==2);
    }

    @Test
    public void getAvailableProductions_correctlyReturnsAvailableProductions() throws DifferentEffectTypeException, InactiveCardException {
        assertTrue(personalBoard.getAvailableProductions().size()==3);
    }

    @Test
    public void testGetAvailableConversion() throws JsonFileNotFoundException, InvalidArgumentException, FileNotFoundException, UnsupportedEncodingException, InvalidSlotException {
        List<LeaderCard> whiteMarbleLeader = LeaderCardParser.parseCards().stream().filter(x->x.getEffect().getEffectType() == EffectType.WHITE_MARBLE).collect(Collectors.toList());
        PersonalBoard personalBoardBis=new PersonalBoard(whiteMarbleLeader);
        personalBoardBis.addDevelopmentCard(basicDevelopmentCard1,0);
        personalBoardBis.addDevelopmentCard(basicDevelopmentCard2,0);
        assertTrue(personalBoardBis.getAvailableConversions().isEmpty());
        for (LeaderCard card : personalBoardBis.getLeaderCards())
            card.activate();
        List<Marble> marbles = new ArrayList<>();
        marbles.add(Marble.BLUE);
        marbles.add(Marble.YELLOW);
        marbles.add(Marble.GREY);
        marbles.add(Marble.PURPLE);
        assertTrue(personalBoardBis.getAvailableConversions().containsAll(marbles));
        assertTrue(marbles.containsAll(personalBoardBis.getAvailableConversions()));
    }

    @Test
    public void testGetAvailableLeaderDepots() throws InvalidArgumentException, InvalidDepotException, InvalidResourceTypeException, InsufficientSpaceException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        assertTrue(pb.isLeaderDepotAvailable(Resource.COIN, 1));
        pb.addResources(ResourceStorageType.LEADER_DEPOT, Resource.COIN, 2);
        assertFalse(pb.isLeaderDepotAvailable(Resource.COIN, 1));
    }

    @Test
    public void testRemoveResourcesWarehouse() throws InvalidArgumentException, InvalidDepotException, InvalidResourceTypeException, InsufficientSpaceException, InsufficientQuantityException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.addResources(ResourceStorageType.WAREHOUSE_FIRST_DEPOT, Resource.COIN, 1);
        pb.addResources(ResourceStorageType.WAREHOUSE_SECOND_DEPOT, Resource.SHIELD, 2);
        pb.addResources(ResourceStorageType.WAREHOUSE_THIRD_DEPOT, Resource.SERVANT, 3);
        pb.removeResources(ResourceStorageType.WAREHOUSE_FIRST_DEPOT, Resource.ANY, 1);
        pb.removeResources(ResourceStorageType.WAREHOUSE_SECOND_DEPOT, Resource.ANY, 2);
        pb.removeResources(ResourceStorageType.WAREHOUSE_THIRD_DEPOT, Resource.ANY, 3);
        assertEquals(0, pb.getWarehouse().getResourceQuantityOfDepot(0));
        assertEquals(0, pb.getWarehouse().getResourceQuantityOfDepot(1));
        assertEquals(0, pb.getWarehouse().getResourceQuantityOfDepot(2));
    }

    @Test
    public void testRemoveResourcesStrongbox() throws InvalidDepotException, InvalidArgumentException, InvalidResourceTypeException, InsufficientSpaceException, InsufficientQuantityException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.addResources(ResourceStorageType.STRONGBOX, Resource.COIN, 1);
        pb.addResources(ResourceStorageType.STRONGBOX, Resource.SHIELD, 2);
        pb.addResources(ResourceStorageType.STRONGBOX, Resource.SERVANT, 3);
        pb.removeResources(ResourceStorageType.STRONGBOX, Resource.COIN, 1);
        pb.removeResources(ResourceStorageType.STRONGBOX, Resource.SHIELD, 2);
        pb.removeResources(ResourceStorageType.STRONGBOX, Resource.SERVANT, 3);
        for (int i = 0; i < pb.getStrongbox().length; i++)
            assertEquals(0, pb.getStrongbox()[i].getResourceQuantity());
    }

    @Test
    public void testRemoveResourcesLeader() throws InvalidDepotException, InvalidArgumentException, InvalidResourceTypeException, InsufficientSpaceException, InsufficientQuantityException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.addResources(ResourceStorageType.LEADER_DEPOT, Resource.COIN, 1);
        pb.addResources(ResourceStorageType.LEADER_DEPOT, Resource.SHIELD, 2);
        pb.removeResources(ResourceStorageType.LEADER_DEPOT, Resource.COIN, 1);
        pb.removeResources(ResourceStorageType.LEADER_DEPOT, Resource.SHIELD, 2);
        assertEquals(0, pb.countResourceNumber());
    }

    @Test
    public void testRemoveAllFullDepots() throws InvalidArgumentException, InvalidDepotException, InvalidResourceTypeException, InsufficientSpaceException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.getWarehouse().addResourcesToDepot(1,Resource.SHIELD, 2);
        pb.addResources(ResourceStorageType.STRONGBOX, Resource.SHIELD, 3);
        pb.addResources(ResourceStorageType.LEADER_DEPOT, Resource.SHIELD, 2);
        pb.removeAll(Resource.SHIELD);
        assertTrue(pb.countResources().get(Resource.SHIELD) == 0);
    }

    @Test
    public void testRemoveAllEmptyDepots() throws InvalidArgumentException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.removeAll(Resource.SHIELD);
        assertTrue(pb.countResources().get(Resource.SHIELD) == 0);
    }

    @Test
    public void testRemoveAllDifferentResource() throws InvalidArgumentException, InvalidDepotException, InvalidResourceTypeException, InsufficientSpaceException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.addResources(ResourceStorageType.LEADER_DEPOT, Resource.SHIELD, 2);
        pb.removeAll(Resource.SHIELD);
        assertTrue(pb.countResources().get(Resource.SERVANT) == 0);
    }

    @Test
    public void testGetHiddenDevelopmentCards() throws InvalidArgumentException, InvalidSlotException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        List<DevelopmentCard> developmentCards = DevelopmentCardParser.parseCards();
        for (int i = 0; i < 3; i++) {
            pb.addDevelopmentCard(developmentCards.get(i), i % 3);
            pb.addDevelopmentCard(developmentCards.get(i + 16), i % 3);
            pb.addDevelopmentCard(developmentCards.get(i + 32), i % 3);
        }
    }

    @Test
    public void testRemove() throws InvalidDepotException, InvalidArgumentException, InvalidResourceTypeException, InsufficientSpaceException, DifferentEffectTypeException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.addResources(ResourceStorageType.LEADER_DEPOT, Resource.COIN, 1);
        pb.addResources(ResourceStorageType.LEADER_DEPOT, Resource.SHIELD, 2);
        pb.addResources(ResourceStorageType.STRONGBOX, Resource.SERVANT, 2);
        assertTrue(pb.isResourceAvailableAndRemove(ResourceStorageType.STRONGBOX, Resource.SERVANT, 1, false));
        assertTrue(pb.isResourceAvailableAndRemove(ResourceStorageType.STRONGBOX, Resource.SERVANT, 1, false));
        assertTrue(pb.isResourceAvailableAndRemove(ResourceStorageType.STRONGBOX, Resource.SERVANT, 2, true));
        assertEquals(pb.getStrongbox()[Resource.SERVANT.getValue()].getResourceQuantity(), 0);
        assertFalse(pb.isResourceAvailableAndRemove(ResourceStorageType.STRONGBOX, Resource.SERVANT, 2, true));
        assertFalse(pb.isResourceAvailableAndRemove(ResourceStorageType.LEADER_DEPOT, Resource.SERVANT, 2, true));
        pb.addResources(ResourceStorageType.WAREHOUSE_FIRST_DEPOT, Resource.SERVANT, 1);
        assertTrue(pb.isResourceAvailableAndRemove(ResourceStorageType.WAREHOUSE, Resource.SERVANT, 1, true));
        assertFalse(pb.isResourceAvailableAndRemove(ResourceStorageType.WAREHOUSE, Resource.SERVANT, 1, true));
        assertTrue(pb.isResourceAvailableAndRemove(ResourceStorageType.LEADER_DEPOT, Resource.SHIELD, 1, true));
        List<LeaderCard> depotsCard = pb.getLeaderCards().stream().filter(x -> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        for (LeaderCard card : depotsCard){
            if (card.getEffect().getExtraDepotEffect().getLeaderDepot().getResourceType() == Resource.SHIELD)
                assertEquals(card.getEffect().getExtraDepotEffect().getLeaderDepot().getResourceQuantity(), 1);
        }
    }

    @Test
    public void testRemoveLeaderCards() throws InvalidArgumentException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        List<LeaderCard> leaderCardsBis = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        leaderCardsBis = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.removeLeaderCard(53);
        pb.removeLeaderCard(54);
        assertEquals(pb.getLeaderCards().get(0).getID(), 55);
        assertEquals(pb.getLeaderCards().get(1).getID(), 56);
    }

    @Test
    public void testGetLeaderCardMap() throws InvalidArgumentException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        Map<Integer, Boolean> result = new HashMap<>();
        result.put(53, true);
        result.put(54, true);
        result.put(55, true);
        result.put(56, true);
        assertEquals(result.size(), pb.getLeaderCardsMap().size());
        for (Integer num : result.keySet())
            assertEquals(result.get(num), pb.getLeaderCardsMap().get(num));
    }

    @Test
    public void testGetLeaderStatus() throws InvalidArgumentException, InvalidDepotException, InvalidResourceTypeException, InsufficientSpaceException {
        List<LeaderCard> lcl = LeaderCardParser.parseCards();
        lcl = lcl.stream().filter(x-> x.getEffect().getEffectType() == EffectType.EXTRA_DEPOT).collect(Collectors.toList());
        lcl.forEach(LeaderCard::activate);
        PersonalBoard pb = new PersonalBoard(lcl);
        pb.addResources(ResourceStorageType.LEADER_DEPOT, Resource.STONE, 2);
        assertEquals(pb.getLeaderStatus().size(), 4);
        assertEquals(2, pb.getLeaderStatus().get(53).intValue());
        assertEquals(0, pb.getLeaderStatus().get(54).intValue());
        assertEquals(0, pb.getLeaderStatus().get(55).intValue());
        assertEquals(0, pb.getLeaderStatus().get(56).intValue());

    }
}
