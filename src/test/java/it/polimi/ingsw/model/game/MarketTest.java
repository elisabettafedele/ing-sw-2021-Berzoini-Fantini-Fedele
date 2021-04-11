package it.polimi.ingsw.model.game;

import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        marbles = market.insertMarbleFromTheSlide(0);
        numOfMarbles[marbles.get(marbles.size()-1).getValue()]++;
        assertTrue(numOfMarbles[0] == 2);
        assertTrue(numOfMarbles[1] == 2);
        assertTrue(numOfMarbles[2] == 1);
        assertTrue(numOfMarbles[3] == 2);
        assertTrue(numOfMarbles[4] == 2);
        assertTrue(numOfMarbles[5] == 4);

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
