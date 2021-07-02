package it.polimi.ingsw.model.game;

import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MarketTest {

    Market market;
    @Before
    public void setUp() throws Exception {
        market = new Market();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void Constructor_all_Marbles_present() throws InvalidArgumentException {
        int[] numOfMarbles = new int[6];
        List<Marble> marbles;
        for(int i = 0; i < 3; i++){
            marbles = market.insertMarbleFromTheSlide(i);
            for (Marble m : marbles){
                numOfMarbles[m.getValue()]++;
            }
        }
        marbles = market.insertMarbleFromTheSlide(3);
        numOfMarbles[marbles.get(0).getValue()]++;
        assertEquals(2, numOfMarbles[0]);
        assertEquals(2, numOfMarbles[1]);
        assertEquals(2, numOfMarbles[2]);
        assertEquals(2, numOfMarbles[3]);
        assertEquals(1, numOfMarbles[4]);
        assertEquals(4, numOfMarbles[5]);

    }

    @Test (expected = InvalidArgumentException.class)
    public void insertMarbleFromTheSlide_InvalidArgumentException_insertionPositionLessThanZero() throws InvalidArgumentException {
        market.insertMarbleFromTheSlide(-1);
    }

    @Test (expected = InvalidArgumentException.class)
    public void insertMarbleFromTheSlide_InvalidArgumentException_insertionPositionGreaterThanSix() throws InvalidArgumentException {
        market.insertMarbleFromTheSlide(7);
    }

}
