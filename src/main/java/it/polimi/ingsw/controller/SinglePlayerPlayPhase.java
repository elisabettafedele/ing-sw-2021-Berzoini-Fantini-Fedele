package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.player.Player;

public class SinglePlayerPlayPhase extends PlayPhase implements GamePhase{

    private int blackCrossPosition;

    public SinglePlayerPlayPhase(Controller controller){
        setController(controller);
        try {
           setPlayer(getController().getGame().getSinglePlayer());
        } catch (InvalidMethodException e) {
            e.printStackTrace();
        } catch (ZeroPlayerException e) {
            e.printStackTrace();
        }
        setTurnController(new TurnController(getController(),getPlayer()));
    }

    @Override
    public void executePhase(Controller controller) {
        //while(!turnController.)
    }
    @Override
    public void handleResourceDiscard(String nickname) {
        blackCrossPosition++;
    }

    @Override
    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {

    }
}
