package it.polimi.ingsw.model.player;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.JsonFileNotFoundException;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.jsonParsers.LeaderCardParser;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.*;

public class PlayerTest {

    Player player;

    @Before
    public void setUp() throws JsonFileNotFoundException, InvalidArgumentException, FileNotFoundException, UnsupportedEncodingException {
        List<LeaderCard> cards = LeaderCardParser.parseCards();
        List<LeaderCard> myCards = cards.subList(0, 4);
        player = new Player("Betti", myCards);
    }

    @Test
    public void testGetters(){
        assertEquals(player.getNickname(), "Betti");
        assertEquals(player.getVictoryPoints(), 0);
        assertEquals(player.isWinner(), false);
        assertEquals(player.getPersonalBoard().getMarkerPosition(), 0);
    }

    @Test
    public void testSetWinner(){
        player.setWinner(true);
        assertTrue(player.isWinner());
    }

    @Test
    public void testAddVictoryPoints() throws InvalidArgumentException {
        player.addVictoryPoints(3);
        assertEquals(player.getVictoryPoints(), 3);
        player.addVictoryPoints(3);
        assertEquals(player.getVictoryPoints(), 6);
    }

    @Test(expected = InvalidArgumentException.class)
    public void testInvalidAddPoints() throws InvalidArgumentException {
        player.addVictoryPoints(-1);
    }

}