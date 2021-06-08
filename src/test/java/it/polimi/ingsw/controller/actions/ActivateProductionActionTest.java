package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.exceptions.InsufficientSpaceException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidDepotException;
import it.polimi.ingsw.exceptions.InvalidResourceTypeException;
import it.polimi.ingsw.jsonParsers.DevelopmentCardParser;
import it.polimi.ingsw.jsonParsers.LeaderCardParser;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ActivateProductionActionTest {


    ActivateProductionAction action;
    Controller controller;

    @Before
    public void setUp() throws Exception {
        controller = new Controller(GameMode.MULTI_PLAYER);
        List<LeaderCard> leaderCards = LeaderCardParser.parseCards();
        List<DevelopmentCard> developmentCards = DevelopmentCardParser.parseCards();
        controller.getGame().addPlayer("Raffa", leaderCards.subList(10, 14), 0, true);
        assert developmentCards.get(0).getID() == 1;
        controller.getPlayerByNickname("Raffa").getPersonalBoard().addDevelopmentCard(developmentCards.get(0), 1);
        TurnController turnController = new TurnController(controller, controller.getPlayerByNickname("Raffa"));
        action = new ActivateProductionAction(turnController);
    }

    @Test
    public void TestIsExecutable_returnsTrue_firstStatement(){
        try {
            controller.getPlayerByNickname("Raffa").getPersonalBoard().addResources(ResourceStorageType.STRONGBOX, Resource.COIN, 3);
        } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException | InsufficientSpaceException e) {
            e.printStackTrace();
        }
        action.reset(controller.getPlayerByNickname("Raffa"));
        assertTrue(action.isExecutable());
    }

    @Test
    public void TestIsExecutable_returnsTrue(){
        try {
            controller.getPlayerByNickname("Raffa").getPersonalBoard().addResources(ResourceStorageType.STRONGBOX, Resource.COIN, 1);
        } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException | InsufficientSpaceException e) {
            e.printStackTrace();
        }
        action.reset(controller.getPlayerByNickname("Raffa"));
        assertTrue(action.isExecutable());
    }
}