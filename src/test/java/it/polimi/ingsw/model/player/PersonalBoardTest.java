package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.depot.LeaderDepot;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

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
        Map hm=new HashMap();
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

    @Test(expected = InvalidArgumentException.class)
    public void personalBoard_constructorCorrectlyThrowsExceptionNullPointer() throws InvalidArgumentException {
        PersonalBoard pb=new PersonalBoard(null);
    }
    @Test (expected = InvalidArgumentException.class)
    public void personalBoard_constructorCorrectlyThrowsExceptionNullNotTwoLeaderCards() throws InvalidArgumentException {
        ArrayList<LeaderCard> lcl=new ArrayList<>();
        lcl.add(basicLeaderCard1);
        PersonalBoard pb=new PersonalBoard(lcl);
    }

    @Test
    public void getFaithTrack_correctlyReturnsFaithTrack() throws InvalidArgumentException {
        assertEquals(FaithTrack.instance(),personalBoard.getFaithTrack());
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
        assertEquals(personalBoard.getDevelopmentCards()[0].size(), 2);
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
        Map resourcesToBeAdded=new HashMap();
        resourcesToBeAdded.put(Resource.COIN,2);
        resourcesToBeAdded.put(Resource.SERVANT,5);
        pb.addResourcesToStrongbox(resourcesToBeAdded);//2 COINS AND 5 SERVANTS
        pb.getWarehouse().addResourcesToDepot(1,Resource.SHIELD,1); //1 SHIELD
        Map availableResources= pb.countResources();
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
}
