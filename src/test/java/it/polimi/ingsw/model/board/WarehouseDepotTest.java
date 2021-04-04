package it.polimi.ingsw.model.board;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InsufficientQuantityException;
import it.polimi.ingsw.exceptions.InsufficientSpaceException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidDepotException;
import it.polimi.ingsw.model.Depot;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WarehouseDepotTest {
    WarehouseDepot depot;

    @Test
    public void testAddRemoveResources() throws InvalidArgumentException, InsufficientQuantityException, InsufficientSpaceException, InvalidDepotException {
        depot = new WarehouseDepot(3);
        assertEquals(depot.getMaxResourceQuantity(), 3);
        assertEquals(depot.getResourceType(), Resource.ANY);
        depot.setResourceType(Resource.COIN);
        depot.addResources(2);
        assertEquals(depot.getResourceType(), Resource.COIN);
        assertEquals(depot.getResourceQuantity(), 2);
        assertFalse(depot.isEmpty());
        depot.removeResources(2);
        assertEquals(depot.getResourceQuantity(), 0);
        assertEquals(depot.getResourceType(), Resource.ANY);
        assertTrue(depot.isEmpty());
    }

    @Test (expected = InvalidArgumentException.class)
    public void testInvalidConstructor() throws InvalidArgumentException{
        depot = new WarehouseDepot(0);
    }

    @Test (expected = InsufficientSpaceException.class)
    public void testExtraAdd() throws InvalidArgumentException, InsufficientSpaceException, InvalidDepotException {
        depot = new WarehouseDepot(3);
        depot.setResourceType(Resource.COIN);
        depot.addResources(4);
    }

    @Test (expected = InsufficientQuantityException.class)
    public void testExtraRemove() throws InvalidArgumentException, InsufficientQuantityException {
        depot = new WarehouseDepot(3);
        depot.setResourceType(Resource.COIN);
        depot.removeResources(2);
    }

    @Test (expected = InvalidArgumentException.class)
    public void testInvalidRemove() throws InvalidArgumentException, InsufficientQuantityException {
        depot = new WarehouseDepot(3);
        depot.setResourceType(Resource.COIN);
        depot.removeResources(-2);
    }

    @Test (expected = InvalidArgumentException.class)
    public void testInvalidAdd() throws InvalidArgumentException, InsufficientQuantityException, InsufficientSpaceException, InvalidDepotException {
        depot = new WarehouseDepot(3);
        depot.setResourceType(Resource.COIN);
        depot.addResources(-2);
    }


}