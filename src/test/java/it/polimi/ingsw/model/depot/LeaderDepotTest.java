package it.polimi.ingsw.model.depot;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InsufficientSpaceException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidDepotException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LeaderDepotTest {
    LeaderDepot depot;

    @Before
    public void setUp(){
        depot = new LeaderDepot(Resource.COIN);
    }

    @Test
    public void testAddResources() throws InsufficientSpaceException, InvalidArgumentException, InvalidDepotException {
        depot.addResources(2);
        assertEquals(depot.getResourceQuantity(), 2);
    }

    @Test
    public void testSpaceAvailable() throws InvalidArgumentException, InsufficientSpaceException {
        depot.addResources(2);
        assertEquals(depot.spaceAvailable(), 0);
    }

    @Test (expected = InsufficientSpaceException.class)
    public void testExtraAdd() throws InsufficientSpaceException, InvalidDepotException, InvalidArgumentException {
        depot.addResources(3);
    }

    @Test (expected = InvalidArgumentException.class)
    public void addResources_negative_argument() throws InsufficientSpaceException, InvalidDepotException, InvalidArgumentException {
        depot.addResources(-1);
    }

}