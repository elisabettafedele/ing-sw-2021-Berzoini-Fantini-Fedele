package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.MultiplayerPlayPhase;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.JsonFileNotFoundException;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.game.Market;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.utility.LeaderCardParser;
import junit.framework.TestCase;
import org.junit.Before;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class TakeResourcesFromMarketActionTest extends TestCase {
    //TODO
    @Before
    public void setUp() throws InvalidArgumentException, UnsupportedEncodingException {
        List<LeaderCard> cards = null;

        cards = LeaderCardParser.parseCards();

        cards = cards.subList(11, 15);
        //TakeResourcesFromMarketAction action = new TakeResourcesFromMarketAction(new Player("Betti",cards), null, new Market(), new MultiplayerPlayPhase(new Controller(GameMode.MULTI_PLAYER)), new TurnController(new Controller(G)));
        //action.handleWhiteMarblesConversion();

    }

}