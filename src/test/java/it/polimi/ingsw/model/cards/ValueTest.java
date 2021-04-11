package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ValueTest {

    Value value;
    Map<Flag, Integer> flagValue;
    Map<Resource, Integer> resourceValue;
    int faithValue;

    @Before
    public void setUp() throws Exception {
        flagValue = new HashMap<>();
        resourceValue = new HashMap<>();
        faithValue = 5;
        value = new Value(flagValue, resourceValue, faithValue);
    }

    @After
    public void tearDown() throws Exception {
        value = null;
    }

    @Test
    public void getFlagValue_returnCorrectFlagValue() {
        assertEquals(flagValue, value.getFlagValue());
    }

    @Test
    public void getResourceValue_returnsCorrectResourceValue() {
        assertEquals(resourceValue, value.getResourceValue());
    }
    @Test
    public void getFaithValue_returnsCorrectFaithValue() {
        assertEquals(faithValue, value.getFaithValue());
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_NegativeFaithValue() throws InvalidArgumentException {
        Value invalidValue = new Value(null, null, -1);
    }

    


}