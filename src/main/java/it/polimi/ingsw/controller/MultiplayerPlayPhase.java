package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.model.player.Player;

import java.util.List;

public class MultiplayerPlayPhase implements GamePhase, PlayPhase{

    private Controller controller;
    private TurnController turnController;
    private Player currentPlayer;
    private int turnIndex;

    public MultiplayerPlayPhase(Controller controller){
        this.controller = controller;
        this.turnController = new TurnController(controller);
        this.turnIndex = 0;
        this.currentPlayer = controller.getPlayers().get(turnIndex);
    }
    @Override
    public void handleResourceDiscard(String nickname)  {
        List<Player> players = null;
        try {
            players = controller.getGame().getPlayers();
        } catch (InvalidMethodException | ZeroPlayerException e) {
            e.printStackTrace();
        }
        for (Player player : players){
            if (!nickname.equals(player.getNickname())) {
                try {
                    player.getPersonalBoard().moveMarker(1);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void executePhase(Controller controller) {

    }

    public String toString(){
        return "Play Phase";
    }
}
