package it.polimi.ingsw.model.player;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.player.VaticanReportSection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class VaticanReportSectionTest {
    VaticanReportSection vaticanReportSection;
    VaticanReportSection vaticanReportSectionConstructorTest;

    @Before
    public void setUp() throws Exception {
        vaticanReportSection = new VaticanReportSection(1,2,3);
    }

    @After
    public void tearDown() throws Exception {
        vaticanReportSectionConstructorTest=null;
        vaticanReportSection=null;
    }

    @Test
    public void getStart_correctlyReturnsStart() {
        assertEquals(1,vaticanReportSection.getStart());
    }

    @Test
    public void getEnd_correctlyReturnsEnd() {
        assertEquals(2,vaticanReportSection.getEnd());
    }

    @Test
    public void getPopeFavorPoints_correctlyReturnsPopeFavorPoints() {
        assertEquals(3,vaticanReportSection.getPopeFavorPoints());
    }
/*
    @Test
    public void isVaticanReportAvailable_correctlyReturnsTrue() throws InvalidArgumentException {
        vaticanReportSectionConstructorTest = new VaticanReportSection(1,2,3);
        assertTrue(vaticanReportSectionConstructorTest.isVaticanReportAvailable());
    }

 */
/*
    @Test
    public void setVaticanReportUnavailable_correctlySetsVaticanReportAvailableToFalse() throws InvalidArgumentException {
        vaticanReportSectionConstructorTest = new VaticanReportSection(1,2,3);
        vaticanReportSectionConstructorTest.setVaticanReportUnavailable();
        assertFalse(vaticanReportSectionConstructorTest.isVaticanReportAvailable());
    }

 */

    @Test (expected = InvalidArgumentException.class)
    public void VaticanReportSection_constructor_InvalidArgumentException_negativeStart() throws InvalidArgumentException {
        vaticanReportSectionConstructorTest = new VaticanReportSection(-2,2,3);
    }
    @Test (expected = InvalidArgumentException.class)
    public void VaticanReportSection_constructor_InvalidArgumentException_negativeEnd() throws InvalidArgumentException {
        vaticanReportSectionConstructorTest = new VaticanReportSection(2,-2,3);
    }
    @Test (expected = InvalidArgumentException.class)
    public void VaticanReportSection_constructor_InvalidArgumentException_negativePopesFavorPoints() throws InvalidArgumentException {
        vaticanReportSectionConstructorTest = new VaticanReportSection(2,3,-3);
    }
    @Test (expected = InvalidArgumentException.class)
    public void VaticanReportSection_constructor_InvalidArgumentException_startGreaterThanEnd() throws InvalidArgumentException {
        vaticanReportSectionConstructorTest = new VaticanReportSection(3,2,3);
    }


}
