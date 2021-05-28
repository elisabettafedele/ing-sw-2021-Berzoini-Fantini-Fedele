package it.polimi.ingsw.model.player;

import it.polimi.ingsw.client.PopesTileState;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.JsonFileNotFoundException;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.jsonParsers.LeaderCardParser;
import it.polimi.ingsw.model.persistency.PersistentPlayer;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

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

    @Test
    public void testPlayerFromPersistentPlayer(){
        PersistentPlayer persistentPlayer = new PersistentPlayer();
        persistentPlayer.setNickname("Betti");
        persistentPlayer.setOwnedLeaderCards(new HashMap<>());
        Stack<Integer>[] slots = new Stack[3];
        for (int i = 0; i < slots.length; i++)
            slots[i] = new Stack<>();
        persistentPlayer.setDevelopmentCardSlots(slots);
        persistentPlayer.setLeaderDepots(new HashMap<>());
        persistentPlayer.setFaithTrackPosition(16);
        persistentPlayer.setActive(true);
        persistentPlayer.setStrongbox(new int[]{0, 0, 0, 0});
        persistentPlayer.setPopesTileStates(new PopesTileState[]{PopesTileState.TAKEN, PopesTileState.NOT_TAKEN, PopesTileState.NOT_REACHED});
        persistentPlayer.setVictoryPoints(0);
        persistentPlayer.setWarehouse(new List[]{new ArrayList(), new ArrayList(), new ArrayList()});
        Player player = new Player(persistentPlayer);
        assertEquals(player.getNickname(), persistentPlayer.getNickname());
        assertTrue(player.getPersonalBoard().getLeaderCards().isEmpty());
        assertEquals(player.getPersonalBoard().countResourceNumber(), 0);
        assertTrue(player.isActive());
        assertFalse(player.isWinner());
        assertEquals(player.getPersonalBoard().getMarkerPosition(), persistentPlayer.getFaithTrackPosition());

    }

}