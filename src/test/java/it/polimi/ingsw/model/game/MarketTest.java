package it.polimi.ingsw.model.game;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    public void Constructor_correct_number_of_each_Marble() throws InvalidArgumentException {
        market.printMarketTray();
        System.out.println();
        System.out.println(market.insertMarbleFromTheSlide(0));
        System.out.println();
        market.printMarketTray();
        System.out.println();
        System.out.println(market.insertMarbleFromTheSlide(6));
        System.out.println();
        market.printMarketTray();
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