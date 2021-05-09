package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.player.Player;

import java.util.List;

public class MultiplayerPlayPhase extends PlayPhase implements GamePhase{

    private int turnIndex;

    public MultiplayerPlayPhase(Controller controller){
        setController(controller);
        this.turnIndex = 0;
        setPlayer(controller.getPlayers().get(turnIndex));
        setTurnController(new TurnController(controller,getPlayer()));
    }
    @Override
    public void handleResourceDiscard(String nickname)  {
        List<Player> players = null;
        try {
            players = getController().getGame().getPlayers();
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
    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {

    }

    @Override
    public void executePhase(Controller controller) {

    }

    public String toString(){
        return "Play Phase";
    }
}
