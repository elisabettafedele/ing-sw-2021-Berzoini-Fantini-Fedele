package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.model.player.Player;

import java.util.List;

public class MultiplayerPlayPhase implements GamePhase, PlayPhase{

    private Controller controller;
    private TurnController turnController;

    public MultiplayerPlayPhase(Controller controller){
        this.controller = controller;
        this.turnController = new TurnController(controller);
    }

    public void handleResourceDiscard() throws InvalidMethodException, ZeroPlayerException, InvalidArgumentException {
        List<Player> players = controller.getGame().getPlayers();
        for (Player player : players){
            if (!turnController.getCurrentPlayer().equals(player))
                player.getPersonalBoard().moveMarker(1);
        }
    }
    @Override
    public void executePhase(Controller controller) {
        //TODO
    }
}
