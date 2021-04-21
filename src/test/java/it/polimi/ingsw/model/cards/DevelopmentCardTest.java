package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DevelopmentCardTest {

    DevelopmentCard developmentCard;
    Flag flag;
    Production production;
    Value cost;
    int victoryPoints, ID;
    String pathImageFront;
    String pathImageBack;
    @Before
    public void setUp() throws Exception {
        victoryPoints = 3;
        flag = new Flag(FlagColor.PURPLE, Level.ONE);
        production = new Production(new Value(null,null, 1), new Value( null,null, 1));
        cost = new Value(null,null, 1);

        pathImageFront = "/img/Cards/DevelopmentCards/front/Masters of Renaissance_Cards_FRONT_3mmBleed_1-1";
        pathImageBack = "/img/Cards/DevelopmentCards/back/Masters of Renaissance_Cards_BACK_3mmBleed_1-1";
        developmentCard = new DevelopmentCard(victoryPoints, cost, flag, production, pathImageFront, pathImageBack);
    }

    @After
    public void tearDown() throws Exception {
        developmentCard = null;
    }

    @Test
    public void testGetPathImageFront(){
        assertEquals(pathImageFront, developmentCard.getPathImageFront());
    }

    @Test
    public void testGetPathImageBack(){
        assertEquals(pathImageBack, developmentCard.getPathImageBack());
    }

    @Test
    public void getVictoryPoints() {
        assertEquals(victoryPoints, developmentCard.getVictoryPoints());
    }


    @Test
    public void getCost() {
        assertEquals(cost, developmentCard.getCost());
    }

    @Test
    public void testGetDiscountedCostTwoDiscounts() throws InvalidArgumentException, ValueNotPresentException {
        Map<Resource, Integer> originalCost = new HashMap<>();
        originalCost.put(Resource.SERVANT, 2);
        originalCost.put(Resource.SHIELD, 3);
        List<Resource> discounts = new ArrayList<>();
        discounts.add(Resource.SERVANT);
        discounts.add(Resource.SHIELD);
        Map<Resource, Integer> discountedCost = new HashMap<>();
        discountedCost.put(Resource.SERVANT, 1);
        discountedCost.put(Resource.SHIELD, 2);
        Value originalValue = new Value(null, originalCost, 0);
        DevelopmentCard card = new DevelopmentCard(victoryPoints, originalValue, flag, production, pathImageFront, pathImageBack);
        assertEquals(discountedCost, card.getDiscountedCost(discounts));
    }

    @Test
    public void testGetDiscountedCostOneDiscount() throws InvalidArgumentException, ValueNotPresentException {
        Map<Resource, Integer> originalCost = new HashMap<>();
        originalCost.put(Resource.SERVANT, 2);
        originalCost.put(Resource.SHIELD, 3);
        List<Resource> discounts = new ArrayList<>();
        discounts.add(Resource.SERVANT);
        Map<Resource, Integer> discountedCost = new HashMap<>();
        discountedCost.put(Resource.SERVANT, 1);
        discountedCost.put(Resource.SHIELD, 3);
        Value originalValue = new Value(null, originalCost, 0);
        DevelopmentCard card = new DevelopmentCard(victoryPoints, originalValue, flag, production, pathImageFront, pathImageBack);
        assertEquals(discountedCost, card.getDiscountedCost(discounts));
    }

    @Test
    public void testGetDiscountedCostEmptyList() throws InvalidArgumentException, ValueNotPresentException {
        Map<Resource, Integer> originalCost = new HashMap<>();
        originalCost.put(Resource.SERVANT, 2);
        originalCost.put(Resource.SHIELD, 3);
        List<Resource> discounts = new ArrayList<>();
        Map<Resource, Integer> discountedCost = new HashMap<>();
        discountedCost.put(Resource.SERVANT, 2);
        discountedCost.put(Resource.SHIELD, 3);
        Value originalValue = new Value(null, originalCost, 0);
        DevelopmentCard card = new DevelopmentCard(victoryPoints, originalValue, flag, production, pathImageFront, pathImageBack);
        assertEquals(discountedCost, card.getDiscountedCost(discounts));
    }

    @Test
    public void getFlag() {
        assertEquals(flag, developmentCard.getFlag());
    }

    @Test
    public void getProduction() {
        assertEquals(production, developmentCard.getProduction());
    }

    @Test
    public void use_returnTrue() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(victoryPoints, cost, flag, production, pathImageFront, pathImageBack);
        developmentCard1.setUsed();
        assertTrue(developmentCard1.getUsed());
    }

    @Test
    public void use_returnFalse() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(victoryPoints, cost, flag, production, pathImageFront, pathImageBack);
        developmentCard1.setUsed();
        assertEquals(true, developmentCard1.getUsed());
        developmentCard1.resetUsed();
        assertFalse(developmentCard.getUsed());
    }

    @Test
    public void testResourceValueNotPresentCatch() throws InvalidArgumentException {
        DevelopmentCard developmentCard = new DevelopmentCard(victoryPoints, cost, flag, production, pathImageFront, pathImageBack);
        List<Resource> discounts = new ArrayList<>();
        discounts.add(Resource.SERVANT);
        developmentCard.getDiscountedCost(discounts);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Card_constructor_InvalidArgumentException_CostNull() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(victoryPoints, null, flag, production, pathImageFront, pathImageBack);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Card_constructor_InvalidArgumentException_NegativeVictoryPoints() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(-1, cost, flag, production, pathImageFront, pathImageBack);
    }

    @Test (expected = InvalidArgumentException.class)
    public void DevelopmentCard_constructor_InvalidArgumentException_FlagNull() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(victoryPoints, cost, null, production, pathImageFront, pathImageBack);
    }

    @Test (expected = InvalidArgumentException.class)
    public void DevelopmentCard_constructor_InvalidArgumentException_ProductionNull() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(victoryPoints, cost, flag, null, pathImageFront, pathImageBack);
    }


}