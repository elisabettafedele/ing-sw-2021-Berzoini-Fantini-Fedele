package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class WarehouseTest {
    Warehouse warehouse;

    @Before
    public void setUp() throws InvalidArgumentException {
        warehouse = new Warehouse();
    }

    @Test
    public void testAddRemoveSwitch() throws InvalidArgumentException, InsufficientSpaceException, InvalidDepotException, InvalidResourceTypeException, UnswitchableDepotsException, InsufficientQuantityException {
        assertEquals(warehouse.getResourceQuantityOfDepot(0), 0);
        assertEquals(warehouse.getResourceTypeOfDepot(0), Resource.ANY);
        warehouse.addResourcesToDepot(0, Resource.COIN, 1);
        warehouse.addResourcesToDepot(1, Resource.SHIELD, 1);
        assertEquals(warehouse.getResourceQuantityOfDepot(0), 1);
        assertEquals(warehouse.getResourceQuantityOfDepot(1), 1);
        assertEquals(warehouse.getResourceTypeOfDepot(0), Resource.COIN);
        assertEquals(warehouse.getResourceTypeOfDepot(1), Resource.SHIELD);
        warehouse.switchRows(0, 1);
        assertEquals(warehouse.getResourceQuantityOfDepot(0), 1);
        assertEquals(warehouse.getResourceQuantityOfDepot(1), 1);
        assertEquals(warehouse.getResourceTypeOfDepot(1), Resource.COIN);
        assertEquals(warehouse.getResourceTypeOfDepot(0), Resource.SHIELD);
        warehouse.removeResourcesFromDepot(Resource.COIN, 1);
        assertEquals(warehouse.getResourceTypeOfDepot(1), Resource.ANY);
        assertEquals(warehouse.getResourceQuantityOfDepot(1), 0);
    }

    @Test(expected = InvalidArgumentException.class)
    public void testInvalidGetType() throws InvalidArgumentException {
        warehouse.getResourceTypeOfDepot(-1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void testInvalidGetQuantity() throws InvalidArgumentException {
        warehouse.getResourceQuantityOfDepot(-1);
    }

    @Test (expected = InvalidResourceTypeException.class)
    public void testInvalidTypeAdd1() throws InvalidArgumentException, InsufficientSpaceException, InvalidResourceTypeException, InvalidDepotException {
        warehouse.addResourcesToDepot(0, Resource.COIN, 1);
        warehouse.addResourcesToDepot(0, Resource.SHIELD, 1);
    }

    @Test (expected = InvalidResourceTypeException.class)
    public void testInvalidTypeAdd2() throws InvalidArgumentException, InsufficientSpaceException, InvalidResourceTypeException, InvalidDepotException {
        warehouse.addResourcesToDepot(0, Resource.COIN, 1);
        warehouse.addResourcesToDepot(1, Resource.COIN, 1);
    }

    @Test (expected = InsufficientSpaceException.class)
    public void testInvalidSpaceAdd() throws InvalidArgumentException, InsufficientSpaceException, InvalidResourceTypeException, InvalidDepotException {
        warehouse.addResourcesToDepot(0, Resource.COIN, 3);
    }

    @Test (expected = UnswitchableDepotsException.class)
    public void testUnswitchable() throws InvalidArgumentException, InsufficientSpaceException, InvalidResourceTypeException, InvalidDepotException, UnswitchableDepotsException {
        warehouse.addResourcesToDepot(0, Resource.COIN, 1);
        warehouse.addResourcesToDepot(1, Resource.SHIELD, 2);
        warehouse.switchRows(0, 1);
    }

    @Test (expected = InvalidResourceTypeException.class)
    public void testInvalidTypeRemove() throws InvalidArgumentException, InsufficientSpaceException, InvalidResourceTypeException, InvalidDepotException, InsufficientQuantityException {
        warehouse.addResourcesToDepot(0, Resource.COIN, 1);
        warehouse.removeResourcesFromDepot(Resource.SHIELD, 2);
    }

}