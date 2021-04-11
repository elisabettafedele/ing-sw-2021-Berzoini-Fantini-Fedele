package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LeaderCardTest {

    Effect effect;
    LeaderCard leaderCard;
    Value value;
    String pathImageFront;
    String pathImageBack;
    @Before
    public void setUp() throws Exception {
        effect = new Effect(Marble.GREY);
        value = new Value(null,null,5);
        pathImageFront = "/img/Cards/DevelopmentCards/front/Masters of Renaissance_Cards_FRONT_3mmBleed_1-1";
        pathImageBack = "/img/Cards/DevelopmentCards/back/Masters of Renaissance_Cards_BACK_3mmBleed_1-1";
    }

    @After
    public void tearDown() throws Exception {
        leaderCard = null;
    }

    @Test
    public void isActive_and_activate() throws InvalidArgumentException {
        leaderCard = new LeaderCard(5, value, effect, pathImageFront, pathImageBack);
        assertTrue(!leaderCard.isActive());
        assertTrue(leaderCard.activate());
        assertTrue(leaderCard.isActive());
        assertTrue(!leaderCard.activate());
    }

    @Test
    public void testUsable() throws InvalidArgumentException {
        leaderCard = new LeaderCard(5, value, effect, pathImageFront, pathImageBack);

    }


    @Test
    public void activate_and_resetUsed() throws InvalidArgumentException {
        leaderCard = new LeaderCard(5, value, effect, pathImageFront, pathImageBack);
        leaderCard.activate();
        assertTrue(leaderCard.isActive());
        leaderCard.resetUsed();
        assertFalse(leaderCard.getUsed());
    }

    @Test (expected = InvalidArgumentException.class)
    public void LeaderCard_constructor_InvalidArgumentException() throws InvalidArgumentException {
        leaderCard = new LeaderCard(5, value, (Effect) null, pathImageFront, pathImageBack);
    }
}