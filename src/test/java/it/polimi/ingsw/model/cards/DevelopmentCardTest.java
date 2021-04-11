package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.Flag;
import it.polimi.ingsw.model.cards.Production;
import it.polimi.ingsw.model.cards.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DevelopmentCardTest {

    DevelopmentCard developmentCard;
    Flag flag;
    Production production;
    Value cost;
    int victoryPoints, ID;
    @Before
    public void setUp() throws Exception {
        victoryPoints = 3;
        flag = new Flag(FlagColor.PURPLE, Level.ONE);
        production = new Production(new Value(null,null, 1), new Value( null,null, 1));
        cost = new Value(null,null, 1);
        developmentCard = new DevelopmentCard(victoryPoints, cost, flag, production);
    }

    @After
    public void tearDown() throws Exception {
        developmentCard = null;
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
    public void alreadyUsed() {
        assertEquals(false, developmentCard.alreadyUsed());
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
        DevelopmentCard developmentCard1 = new DevelopmentCard(victoryPoints, cost, flag, production);
        assertEquals(true, developmentCard1.use());
    }

    @Test
    public void use_returnFalse() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(victoryPoints, cost, flag, production);
        developmentCard1.use();
        assertEquals(false, developmentCard1.use());
    }

    @Test (expected = InvalidArgumentException.class)
    public void Card_constructor_InvalidArgumentException_CostNull() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(victoryPoints, null, flag, production);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Card_constructor_InvalidArgumentException_NegativeVictoryPoints() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(-1, cost, flag, production);
    }

    @Test (expected = InvalidArgumentException.class)
    public void DevelopmentCard_constructor_InvalidArgumentException_FlagNull() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(victoryPoints, cost, null, production);
    }

    @Test (expected = InvalidArgumentException.class)
    public void DevelopmentCard_constructor_InvalidArgumentException_ProductionNull() throws InvalidArgumentException {
        DevelopmentCard developmentCard1 = new DevelopmentCard(victoryPoints, cost, flag, null);
    }

}