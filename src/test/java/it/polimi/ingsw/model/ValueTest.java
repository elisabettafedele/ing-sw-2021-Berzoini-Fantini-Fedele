package it.polimi.ingsw.model;

import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ValueTest {

    Value value;
    List<Flag> flagValue;
    Map<Resource, Integer> resourceValue;
    int faithValue;

    @Before
    public void setUp() throws Exception {
        flagValue = new ArrayList<>();
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

    @Test
    public void Value_constructor_OnlyResourceValue() throws InvalidArgumentException {
        Value v = new Value(resourceValue);
    }

    @Test
    public void Value_constructor_OnlyFaithValue() throws InvalidArgumentException {
        Value v = new Value(faithValue);
    }

    @Test
    public void Value_constructor_OnlyFlagValue() throws InvalidArgumentException {
        Value v = new Value(flagValue);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_OnlyNegativeFaithValue() throws InvalidArgumentException {
        Value invalidValue = new Value(-1);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_OnlyFlagValueNull() throws InvalidArgumentException {
        List<Flag> inv = null;
        Value invalidValue = new Value(inv);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_OnlyResourceValueNull() throws InvalidArgumentException {
        Map<Resource, Integer> inv = null;
        Value invalidValue = new Value(inv);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_2argsResourceNull() throws InvalidArgumentException {
        Map<Resource, Integer> inv = null;
        Value invalidValue = new Value(inv, 5);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_2argsFaithNull() throws InvalidArgumentException {
        Map<Resource, Integer> inv = new HashMap<>();
        Value invalidValue = new Value(inv, -5);
    }

    @Test
    public void Value_constructor_InvalidArgumentException_2argsNotNull() throws InvalidArgumentException {
        Map<Resource, Integer> r = new HashMap<>();
        Value value1 = new Value(r, 5);
        assertNull(value1.getFlagValue());
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_2argsFlagNull() throws InvalidArgumentException {
        List<Flag> inv = null;
        Map<Resource, Integer> r = null;
        Value invalidValue = new Value(inv, r);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_2argsResourcesNull() throws InvalidArgumentException {
        List<Flag> inv = new ArrayList<>();
        Map<Resource, Integer> r = null;
        Value invalidValue = new Value(inv, r);
    }

    @Test
    public void Value_constructor_InvalidArgumentException_2argsResourcesFlagNotNull() throws InvalidArgumentException {
        List<Flag> inv = new ArrayList<>();
        Map<Resource, Integer> r = new HashMap<>();
        Value invalidValue = new Value(inv, r);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_2argsFlagValueNull() throws InvalidArgumentException {
        List<Flag> inv = null;
        Value invalidValue = new Value(inv, 5);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_2argsFaithNegative() throws InvalidArgumentException {
        List<Flag> inv = new ArrayList<>();
        Value invalidValue = new Value(inv, -5);
    }

    @Test
    public void Value_constructor_InvalidArgumentException_2argsFaithFlagNotNull() throws InvalidArgumentException {
        List<Flag> inv = new ArrayList<>();
        Value invalidValue = new Value(inv, 5);
    }


}