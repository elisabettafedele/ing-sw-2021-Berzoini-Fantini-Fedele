package it.polimi.ingsw.model;

import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FlagTest {

    Flag flag;
    Level level = Level.ONE;
    FlagColor flagColor = FlagColor.GREEN;
    @Before
    public void setUp() throws Exception {
        flag = new Flag(flagColor, level);
    }

    @After
    public void tearDown() throws Exception {
        flag = null;
    }

    @Test
    public void getFlagColor_returnsCorrectColor(){
        assertEquals(flagColor, flag.getFlagColor());
    }

    @Test
    public void getFlagLevel_returnsCorrectLevel(){
        assertEquals(level, flag.getFlagLevel());
    }

    @Test (expected = InvalidArgumentException.class)
    public void Flag_constructor_InvalidArgumentException_FlagColorNull() throws InvalidArgumentException {
        Flag invalidFlag = new Flag(null, level);
    }

    @Test (expected = InvalidArgumentException.class)
    public void Flag_constructor_InvalidArgumentException_FlagLevelNull() throws InvalidArgumentException {
        Flag invalidFlag = new Flag(flagColor, null);
    }
}