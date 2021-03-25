package it.polimi.ingsw.model;

import it.polimi.ingsw.enumerations.ValueType;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FaithValueTest {

    FaithValue faithVal;
    @Before
    public void setUp() throws Exception {
        faithVal = new FaithValue(5);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getQuantity_returnsCorrectValue() {
        int value = faithVal.getQuantity();
        assertEquals(value, 5);
    }

    @Test
    public void getType_returnsCorrectType() {
        ValueType type = faithVal.getType();
        assertTrue(type.equals(ValueType.FAITH));
    }

    @Test (expected = InvalidArgumentException.class)
    public void FaithValue_constructor_InvalidArgumentException() throws InvalidArgumentException {
        FaithValue invalidFaithVal = new FaithValue(-1);
    }
}