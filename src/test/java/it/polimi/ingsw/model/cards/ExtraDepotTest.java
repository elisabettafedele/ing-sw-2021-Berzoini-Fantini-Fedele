package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.ExtraDepot;
import it.polimi.ingsw.model.depot.LeaderDepot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExtraDepotTest {

    ExtraDepot extraDepot;
    LeaderDepot leaderDepot;
    @Before
    public void setUp() throws Exception {
        leaderDepot = new LeaderDepot(Resource.COIN);
        extraDepot = new ExtraDepot(leaderDepot);
    }

    @After
    public void tearDown() throws Exception {
        extraDepot = null;
    }


    @Test
    public void getEffect_returnsCorrectLeaderDepot() {
        assertEquals(leaderDepot, extraDepot.getLeaderDepot());
    }

    @Test (expected = InvalidArgumentException.class)
    public void ExtraDepot_constructor_InvalidArgumentException_leaderDepotNull() throws InvalidArgumentException {
        ExtraDepot invalidExtraDepot = new ExtraDepot(null);
    }
}