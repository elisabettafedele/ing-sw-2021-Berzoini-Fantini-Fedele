package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.model.player.Player;

public class SinglePlayerPlayPhase implements GamePhase, PlayPhase{
    private Controller controller;
    private TurnController turnController;
    private Player player;
    private int blackCrossPosition;

    public SinglePlayerPlayPhase(Controller controller){
        this.controller = controller;
        try {
           this.player=controller.getGame().getSinglePlayer();
        } catch (InvalidMethodException e) {
            e.printStackTrace();
        } catch (ZeroPlayerException e) {
            e.printStackTrace();
        }
        turnController= new TurnController(controller,player);
    }

    @Override
    public void executePhase(Controller controller) {
        //while(!turnController.)
    }
    @Override
    public void handleResourceDiscard(String nickname) {
        blackCrossPosition++;
    }
}
