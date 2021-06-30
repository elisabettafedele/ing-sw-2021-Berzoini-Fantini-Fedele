package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.messages.toClient.TextMessage;
import it.polimi.ingsw.messages.toClient.matchData.NotifyVictoryPoints;
import it.polimi.ingsw.messages.toClient.matchData.TurnMessage;
import it.polimi.ingsw.messages.toClient.matchData.UpdateMarkerPosition;
import it.polimi.ingsw.jsonParsers.GameHistory;
import it.polimi.ingsw.model.game.FaithTrack;
import it.polimi.ingsw.model.persistency.PersistentControllerPlayPhase;
import it.polimi.ingsw.model.persistency.PersistentGame;
import it.polimi.ingsw.model.player.Player;
import java.util.List;
import java.util.stream.Collectors;

public class MultiplayerPlayPhase extends PlayPhase {

    private int turnIndex;

    /**
     * Standard class constructor
     * @param controller the controller related to the game
     */
    public MultiplayerPlayPhase(Controller controller){
        setController(controller);
        this.turnIndex = 0;
        setPlayer(controller.getPlayers().get(turnIndex));
        setLastTurnGameCopy(new PersistentGame(getController().getGame()));
    }

    /**
     * Class constructor to restart a saved game
     * @param controller the controller of the game
     * @param lastPlayerNickname the nickname of the last player that has finished his turn
     * @param endTrigger true only if the game was end triggered before being interrupted
     */
    public MultiplayerPlayPhase(Controller controller, String lastPlayerNickname, boolean endTrigger){
        setController(controller);
        this.turnIndex = controller.getPlayers().stream().map(Player::getNickname).collect(Collectors.toList()).indexOf(lastPlayerNickname);
        pickNextPlayer();
        setTurnController(new TurnController(controller, controller.getPlayers().get(turnIndex), endTrigger));
    }

    /**
     * Method to restart the game from the right turn, when the game is retrieved the json file
     */
    @Override
    public void restartLastTurn(){
        getController().sendMessageToAll(new TurnMessage(getController().getPlayers().get(turnIndex).getNickname(), true));
        getTurnController().start(getController().getPlayers().get(turnIndex));
    }

    /**
     * Method to start a new turn
     */
    @Override
    public void nextTurn() {
        pickNextPlayer();
        while(!getController().getPlayers().get(turnIndex).isActive() && getTurnController().getController().getClientHandlers().size() > 0)
            pickNextPlayer();
        if (getTurnController().getController().getClientHandlers().size() == 0){
            return;
        }
        if (getTurnController().isEndTriggered() && getTurnController().getController().getPlayers().get(turnIndex).hasInkwell())
            getController().endMatch(); //In theory this is not needed since this work is done by pickNextPlayer. Just an extra check
        else {
            getController().sendMessageToAll(new TurnMessage(getController().getPlayers().get(turnIndex).getNickname(), true));
            getTurnController().start(getController().getPlayers().get(turnIndex));
        }
    }

    /**
     * Method used to set the next player index. It also checks whether the match should end and, in that case, it makes it end.
     */
    public void pickNextPlayer(){
        if (turnIndex == getController().getPlayers().size() - 1 && (getTurnController() != null && getTurnController().isEndTriggered()))
            getController().endMatch();
        else
            turnIndex = turnIndex == getController().getPlayers().size() - 1 ? 0 : turnIndex + 1;
    }

    /**
     * Method to handle the discard of resources by one player. When a resource is discarded all the other players gain one faith point
     * @param nickname is the nickname of the player who has discarded the resource and that will not gain any faith point
     */
    @Override
    public void handleResourceDiscard(String nickname)  {
        List<Player> players = getController().getPlayers();
        for (Player player : players){
            if (!nickname.equals(player.getNickname())) {
                try {
                    player.getPersonalBoard().moveMarker(1);
                    getController().sendMessageToAll(new UpdateMarkerPosition(player.getNickname(), player.getPersonalBoard().getMarkerPosition()));
                    if (FaithTrack.changesVictoryPoints(player.getPersonalBoard().getMarkerPosition()))
                        getController().sendMessageToAll(new NotifyVictoryPoints(player.getNickname(), player.countPoints()));
                } catch (InvalidArgumentException ignored) { } //never thrown, the argument is "1", so it will never be negative
            }
        }
        getTurnController().checkFaithTrack();
    }

    /**
     * Method called when endTrigger is set true by the {@link TurnController}.
     * Since the {@link it.polimi.ingsw.enumerations.GameMode} in this case is multiplayer, I do not have to do anything.
     * I just wait until the round is finished. The end will be checked and handled by pickNextPlayer().
     */
    @Override
    public void handleEndTriggered() {
        //When the endTrigger variable becomes true and the game is multiplayer, I do not have to do anything. I just wait until the round is finished -> it is checked by pickNextPlayer().
    }

    /**
     * Method to execute the play phase.
     * It start the turn of the first player
     * @param controller the controller of the game
     */
    @Override
    public void executePhase(Controller controller) {
        controller.sendMessageToAll(new TextMessage("\nFrom now on, when you are not the turn owner, you can use the command -pb to move to another player's view \n(EG. -pb betti shows you the view of the player named \"betti\")\n"));
        while(!getController().getPlayers().get(turnIndex).isActive())
            pickNextPlayer();
        setPlayer(controller.getPlayers().get(turnIndex));
        setTurnController(new TurnController(controller,getPlayer()));
        controller.sendMessageToAll(new TurnMessage(getPlayer().getNickname(), true));
        getTurnController().start(getPlayer());
    }

    /**
     * Method used to save a multiplayer game as a json file after a player has performed a standard action (his turn becomes valid) and at the end of each turn
     */
    public void saveGame(){
        GameHistory.saveGame(new PersistentControllerPlayPhase(new PersistentGame(getController().getGame()), getTurnController().getCurrentPlayer().getNickname(), getController().getControllerID(), getTurnController().isEndTriggered()));
    }

}
