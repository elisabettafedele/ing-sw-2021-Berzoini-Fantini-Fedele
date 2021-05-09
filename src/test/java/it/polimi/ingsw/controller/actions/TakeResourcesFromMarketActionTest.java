package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidPlayerAddException;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.utility.LeaderCardParser;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class TakeResourcesFromMarketActionTest extends TestCase {
    TakeResourcesFromMarketAction action;
    Controller controller;
    @Before
    public void setUp() throws InvalidArgumentException, UnsupportedEncodingException, InvalidPlayerAddException {
        controller = new Controller(GameMode.MULTI_PLAYER);
        List<LeaderCard> leaderCards = LeaderCardParser.parseCards();
        controller.getGame().addPlayer("Betti", leaderCards.subList(10, 14), 0, true);
        controller.getGame().addPlayer("Pippo", leaderCards.subList(4, 8), 0, false);
        TurnController turnController = new TurnController(controller, controller.getPlayerByNickname("Betti"));
        action = new TakeResourcesFromMarketAction(turnController);
    }

    @Test
    public void testGetAvailableWhiteMarbleConversion(){
        assertTrue(action.getWhiteMarblesConversion().isEmpty());
        controller.getGame().getPlayerByNickname("Betti").getPersonalBoard().getLeaderCards().get(0).activate();
        assertTrue(action.getWhiteMarblesConversion().get(0) == Resource.STONE);
    }

}