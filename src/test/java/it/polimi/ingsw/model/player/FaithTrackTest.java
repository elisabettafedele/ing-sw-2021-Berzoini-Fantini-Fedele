package it.polimi.ingsw.model.player;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.player.FaithTrack;
import it.polimi.ingsw.model.player.VaticanReportSection;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class FaithTrackTest {
    FaithTrack faithTrack;

    @After
    public void tearDown() throws Exception {

    }

    @Test (expected = InvalidArgumentException.class)
    public void FaithTrack_constructor_InvalidArgumentException_NullPointerVaticanReportSectionIterator() throws InvalidArgumentException {
        faithTrack = new FaithTrack(null,10,new HashMap<>());
    }

    @Test (expected = InvalidArgumentException.class)
    public void FaithTrack_constructor_InvalidArgumentException_NegativeLength() throws InvalidArgumentException {
        ArrayList<VaticanReportSection> listVaticanReportSection= new ArrayList<>();
        listVaticanReportSection.add(new VaticanReportSection(1,2,3));
        faithTrack = new FaithTrack(listVaticanReportSection.iterator(), -10,new HashMap<>());
    }

    @Test (expected = InvalidArgumentException.class)
    public void FaithTrack_constructor_InvalidArgumentException_NullPointerTrackVictoryPoints() throws InvalidArgumentException {
        ArrayList<VaticanReportSection> listVaticanReportSection= new ArrayList<>();
        listVaticanReportSection.add(new VaticanReportSection(1,2,3));
        faithTrack = new FaithTrack(listVaticanReportSection.iterator(), 10,null);
    }

    @Test
    public void isVaticanReport_correctlyReturnsTrue() throws InvalidArgumentException {
        ArrayList<VaticanReportSection> listVaticanReportSection= new ArrayList<>();
        listVaticanReportSection.add(new VaticanReportSection(1,2,3));
        faithTrack = new FaithTrack(listVaticanReportSection.iterator(), 10,new HashMap<>());
        assertTrue(faithTrack.isVaticanReport(3));
    }
    @Test
    public void isVaticanReport_correctlyReturnsFalse() throws InvalidArgumentException {
        ArrayList<VaticanReportSection> listVaticanReportSection= new ArrayList<>();
        listVaticanReportSection.add(new VaticanReportSection(1,2,3));
        faithTrack = new FaithTrack(listVaticanReportSection.iterator(), 10,new HashMap<>());
        assertFalse(faithTrack.isVaticanReport(1));
    }
    @Test
    public void getCurrentSection_returnsCorrectVaticanReportSection() throws InvalidArgumentException {
        ArrayList<VaticanReportSection> listVaticanReportSection= new ArrayList<>();
        listVaticanReportSection.add(new VaticanReportSection(1,2,3));
        listVaticanReportSection.add(new VaticanReportSection(3,4,2));
        faithTrack = new FaithTrack(listVaticanReportSection.iterator(), 10,new HashMap<>());
        assertEquals(listVaticanReportSection.get(0),faithTrack.getCurrentSection());
        assertEquals(listVaticanReportSection.get(1),faithTrack.getCurrentSection());
    }

    @Test
    public void getVictoryPoints_returnsCorrectSumOfVictoryPoints() throws InvalidArgumentException {
        ArrayList<VaticanReportSection> listVaticanReportSection= new ArrayList<>();
        listVaticanReportSection.add(new VaticanReportSection(1,2,3));
        HashMap<Integer,Integer> trackVictoryPoints= new HashMap<>();
        trackVictoryPoints.put(5,2);
        trackVictoryPoints.put(7,1);
        trackVictoryPoints.put(9,10);
        faithTrack = new FaithTrack(listVaticanReportSection.iterator(), 10,trackVictoryPoints);
        assertTrue(faithTrack.getVictoryPoints(7)==3);

    }
}
