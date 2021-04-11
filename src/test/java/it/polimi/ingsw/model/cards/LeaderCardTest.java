package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.exceptions.InactiveCardException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LeaderCardTest {

    Effect effect;
    LeaderCard leaderCard;
    Value value;
    @Before
    public void setUp() throws Exception {
        effect = new Effect(Marble.GREY);
        value = new Value(null,null,5);
    }

    @After
    public void tearDown() throws Exception {
        leaderCard = null;
    }

    @Test
    public void isActive_and_activate() throws InvalidArgumentException {
        leaderCard = new LeaderCard(5, value, effect);
        assertTrue(!leaderCard.isActive());
        assertTrue(leaderCard.activate());
        assertTrue(leaderCard.isActive());
        assertTrue(!leaderCard.activate());
    }

    @Test
    public void activate_and_resetUsed() throws InvalidArgumentException {
        leaderCard = new LeaderCard(5, value, effect);
        assertTrue(leaderCard.use());
        assertTrue(leaderCard.alreadyUsed());
        leaderCard.resetUsed();
        assertFalse(leaderCard.alreadyUsed());
    }



    @Test (expected = InactiveCardException.class)
    public void getEffect_beforeActivation_InactiveCardException() throws InvalidArgumentException, InactiveCardException {
        leaderCard = new LeaderCard(5, value, effect);
        assertEquals(effect, leaderCard.getEffect());
    }

    @Test
    public void getEffect_afterActivation_InactiveCardException() throws InvalidArgumentException, InactiveCardException {
        leaderCard = new LeaderCard(5, value, effect);
        leaderCard.activate();
        assertEquals(effect, leaderCard.getEffect());
    }

    @Test (expected = InvalidArgumentException.class)
    public void LeaderCard_constructor_InvalidArgumentException() throws InvalidArgumentException {
        leaderCard = new LeaderCard(5, value, (Effect) null);
    }
}