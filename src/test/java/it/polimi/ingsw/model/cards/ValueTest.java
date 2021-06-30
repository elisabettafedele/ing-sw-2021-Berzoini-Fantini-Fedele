package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.model.cards.Flag;
import it.polimi.ingsw.model.cards.Value;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ValueTest {

    Value value;
    Map<Flag, Integer> flagValue;
    Map<Resource, Integer> resourceValue;
    int faithValue;

    @Before
    public void setUp() throws Exception {
        flagValue = new LinkedHashMap<>();
        resourceValue = new LinkedHashMap<>();
        flagValue.put(new Flag(FlagColor.PURPLE, Level.THREE), 4);
        resourceValue.put(Resource.COIN, 3);
        faithValue = 5;
        value = new Value(flagValue, resourceValue, faithValue);
    }

    @After
    public void tearDown() throws Exception {
        value = null;
    }

    @Test
    public void getFlagValue_returnCorrectFlagValue() throws ValueNotPresentException {
        assertEquals(flagValue, value.getFlagValue());
    }

    @Test
    public void getResourceValue_returnsCorrectResourceValue() throws ValueNotPresentException {
        assertEquals(resourceValue, value.getResourceValue());
    }
    @Test
    public void getFaithValue_returnsCorrectFaithValue() throws ValueNotPresentException {
        assertEquals(faithValue, value.getFaithValue());
    }

    @Test (expected = InvalidArgumentException.class)
    public void Value_constructor_InvalidArgumentException_NegativeFaithValue() throws InvalidArgumentException {
        Value invalidValue = new Value(null, null, -1);
    }

    @Test (expected = ValueNotPresentException.class)
    public void getFlagValue_throws_ValueNotPresentException() throws InvalidArgumentException, ValueNotPresentException {
        Map<Flag, Integer> flagValue1;
        Map<Resource, Integer> resourceValue1;
        Value value1;
        int faithValue1;

        flagValue1 = new LinkedHashMap<>();
        resourceValue1 = new LinkedHashMap<>();
        resourceValue1.put(Resource.COIN, 3);
        faithValue1 = 5;
        value1 = new Value(flagValue1, resourceValue1, faithValue1);
        value1.getFlagValue();

    }

    @Test (expected = ValueNotPresentException.class)
    public void getResourceValue_throws_ValueNotPresentException() throws InvalidArgumentException, ValueNotPresentException {
        Map<Flag, Integer> flagValue1;
        Map<Resource, Integer> resourceValue1;
        Value value1;
        int faithValue1;

        flagValue1 = new LinkedHashMap<>();
        resourceValue1 = new LinkedHashMap<>();
        flagValue1.put(new Flag(FlagColor.PURPLE, Level.THREE), 4);
        faithValue1 = 5;
        value1 = new Value(flagValue1, resourceValue1, faithValue1);
        value1.getResourceValue();

    }

    @Test (expected = ValueNotPresentException.class)
    public void getFaithValue_throws_ValueNotPresentException() throws InvalidArgumentException, ValueNotPresentException {
        Map<Flag, Integer> flagValue1;
        Map<Resource, Integer> resourceValue1;
        Value value1;
        int faithValue1;

        flagValue1 = new LinkedHashMap<>();
        resourceValue1 = new LinkedHashMap<>();
        flagValue1.put(new Flag(FlagColor.PURPLE, Level.THREE), 4);
        resourceValue1.put(Resource.COIN, 3);
        faithValue1 = 0;
        value1 = new Value(flagValue1, resourceValue1, faithValue1);
        value1.getFaithValue();

    }

    @Test
    public void equals_and_hashcode() throws InvalidArgumentException {
        Map<Flag, Integer> flagValue1;
        Map<Resource, Integer> resourceValue1;
        int faithValue1;
        Value value1;

        Map<Flag, Integer> flagValue2;
        Map<Resource, Integer> resourceValue2;
        int faithValue2;
        Value value2;

        flagValue1 = new LinkedHashMap<>();
        resourceValue1 = new LinkedHashMap<>();
        flagValue1.put(new Flag(FlagColor.PURPLE, Level.THREE), 4);
        resourceValue1.put(Resource.COIN, 3);
        faithValue1 = 5;
        value1 = new Value(flagValue1, resourceValue1, faithValue1);

        flagValue2 = new LinkedHashMap<>();
        resourceValue2 = new LinkedHashMap<>();
        flagValue2.put(new Flag(FlagColor.PURPLE, Level.THREE), 4);
        resourceValue2.put(Resource.COIN, 3);
        faithValue2 = 5;
        value2 = new Value(flagValue2, resourceValue2, faithValue2);

        assertEquals(value1, value2);
        assertTrue(value1.hashCode() == value2.hashCode());


    }


}