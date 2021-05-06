package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;

public class SinglePlayerPlayPhase implements GamePhase, PlayPhase{
    private Controller controller;
    private TurnController turnController;
    private int blackCrossPosition;

    public SinglePlayerPlayPhase(Controller controller){
        this.controller = controller;
        this.turnController = new TurnController(controller);
    }

    @Override
    public void executePhase(Controller controller) {
        //TODO
    }
    @Override
    public void handleResourceDiscard(String nickname) {
        blackCrossPosition++;
    }
}
