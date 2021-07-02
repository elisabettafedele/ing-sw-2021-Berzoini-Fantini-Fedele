package it.polimi.ingsw.model.player;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.game.FaithTrack;
import it.polimi.ingsw.model.game.VaticanReportSection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FaithTrackTest {
    FaithTrack faithTrack;

    @Before
    public void setUp() throws Exception {
        faithTrack = new FaithTrack();
    }

   //since FaithTrack is a singleton and no order between tests can be assumed,everything has to be in the same test where instructions order can be imposed.
   //In fact, faithTrack.getCurrentSection() alters faithTrack status and would affect the result of other asserts, if executed after.
    @Test
    public void faithTrackTest() throws InvalidArgumentException {
        //Test:isVaticanReport_correctlyReturnsTrue
        assertTrue(faithTrack.isVaticanReport(9));
        //Test:isVaticanReport_correctlyReturnsFalse
        assertFalse(faithTrack.isVaticanReport(1));
        //Test:getVictoryPoints_returnsCorrectSumOfVictoryPoints
        assertTrue(FaithTrack.getVictoryPoints(14)==6);
        assertTrue(FaithTrack.getVictoryPoints(34)==20);
        assertTrue(FaithTrack.getVictoryPoints(-12)==0);
        assertTrue(FaithTrack.getVictoryPoints(2)==0);
        assertTrue(FaithTrack.getVictoryPoints(9)==4);
        //Test:getCurrentSection_returnsCorrectVaticanReportSection
        VaticanReportSection vaticanReportSection=faithTrack.getCurrentSection(true);
        assertTrue(vaticanReportSection.getStart()==5);
        assertTrue(vaticanReportSection.getEnd()==8);
        assertTrue(vaticanReportSection.getPopeFavorPoints()==2);
        vaticanReportSection=faithTrack.getCurrentSection(true);
        assertTrue(vaticanReportSection.getStart()==12);
        assertTrue(vaticanReportSection.getEnd()==16);
        assertTrue(vaticanReportSection.getPopeFavorPoints()==3);

    }
    @Test
    public void testSetIterator() throws InvalidArgumentException {
        VaticanReportSection vaticanReportSection = new VaticanReportSection(5, 8, 2);
        assertEquals(faithTrack.getVaticanReportSectionNumberByStart(5), 0);
        faithTrack.setVaticanReportSectionIterator(vaticanReportSection);
        assertEquals(vaticanReportSection.getStart(), faithTrack.getCurrentSection(false).getStart());
    }

    @Test
    public void testGetVaticanReportSectionNumberByStart() throws InvalidArgumentException {
        VaticanReportSection vaticanReportSection = new VaticanReportSection(12, 18, 2);
        assertEquals(faithTrack.getVaticanReportSectionNumberByStart(12), 1);
        vaticanReportSection = new VaticanReportSection(19, 22, 2);
        assertEquals(faithTrack.getVaticanReportSectionNumberByStart(19), 2);
    }
}
