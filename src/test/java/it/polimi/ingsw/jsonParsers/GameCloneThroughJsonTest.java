package it.polimi.ingsw.jsonParsers;

import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.game.Game;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class GameCloneThroughJsonTest extends TestCase {
    @Test
    public void testCopy() {
        Game game = new Game (GameMode.MULTI_PLAYER);
        Game gameClone = GameCloneThroughJson.clone(game);
        assertEquals(gameClone.getDevelopmentCardGrid().getAvailableCards().get(0), game.getDevelopmentCardGrid().getAvailableCards().get(0));
    }

}