package it.polimi.ingsw.model.persistency;

import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.InvalidPlayerAddException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.jsonParsers.LeaderCardParser;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.game.Game;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class PersistentGameTest extends TestCase {

    @Test
    public void testConstructor() throws InvalidArgumentException, UnsupportedEncodingException, InvalidPlayerAddException, InvalidMethodException, ZeroPlayerException {
        Game multiGame = new Game(GameMode.MULTI_PLAYER);
        List<LeaderCard> leaderCards = LeaderCardParser.parseCards();
        multiGame.addPlayer("betti", leaderCards.subList(0, 4), 0, false);
        multiGame.addPlayer("elia", leaderCards.subList(4, 8), 0, false);
        multiGame.addPlayer("raffa", leaderCards.subList(8, 12), 0, false);
        PersistentGame persistentGame = new PersistentGame(multiGame);
        assertEquals(multiGame.getGameMode(), persistentGame.getGameMode());
        assertEquals(multiGame.getPlayers().get(0).getVictoryPoints(), persistentGame.getPlayers().get(0).getVictoryPoints());
        assertEquals(multiGame.getFaithTrack().getCurrentSection(false).getStart(), persistentGame.getCurrentSection().getStart());

    }

}