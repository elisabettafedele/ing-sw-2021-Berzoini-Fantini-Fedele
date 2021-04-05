package it.polimi.ingsw.model.player;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
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
    public void testGetters() throws InvalidArgumentException {
        assertEquals(warehouse.getResourceQuantityOfDepot(0), 0);
        assertEquals(warehouse.getResourceTypeOfDepot(0), Resource.ANY);
    }

    @Test(expected = InvalidArgumentException.class)
    public void testInvalidGetType() throws InvalidArgumentException {
        warehouse.getResourceTypeOfDepot(-1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void testInvalidGetQuantity() throws InvalidArgumentException {
        warehouse.getResourceQuantityOfDepot(-1);
    }




}