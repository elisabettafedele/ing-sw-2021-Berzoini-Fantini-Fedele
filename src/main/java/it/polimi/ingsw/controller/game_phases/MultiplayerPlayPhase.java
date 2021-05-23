package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.messages.toClient.matchData.UpdateMarkerPosition;
import it.polimi.ingsw.model.player.Player;

import java.util.List;

public class MultiplayerPlayPhase extends PlayPhase implements GamePhase {

    private int turnIndex;
    private boolean endTrigger;

    public MultiplayerPlayPhase(Controller controller){
        setController(controller);
        this.turnIndex = 0;
        setPlayer(controller.getPlayers().get(turnIndex));
    }

    @Override
    public void nextTurn() {
        pickNextPlayer();
        if (getTurnController().getController().getClientHandlers().size() == 0){
            return;
        }
        while(!getController().getPlayers().get(turnIndex).isActive())
            pickNextPlayer();
        if (endTrigger && getTurnController().getController().getPlayers().get(turnIndex).hasInkwell())
            getController().endMatch();
        else
            getTurnController().start(getController().getPlayers().get(turnIndex));
    }

    public void pickNextPlayer(){
        turnIndex = turnIndex == getController().getPlayers().size() - 1 ? 0 : turnIndex + 1;
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
                    getController().sendMessageToAll(new UpdateMarkerPosition(player.getNickname(), player.getPersonalBoard().getMarkerPosition()));
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        getTurnController().checkFaithTrack();
    }

    @Override
    public void handleEndTriggered() {
        endTrigger = true;
    }


    @Override
    public void executePhase(Controller controller) {
        setTurnController(new TurnController(controller,getPlayer()));
        reloadGameCopy(false);
        getTurnController().start(getPlayer());
    }

}
