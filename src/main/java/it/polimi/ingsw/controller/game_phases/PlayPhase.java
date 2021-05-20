package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.jsonParsers.GameCloneThroughJson;
import it.polimi.ingsw.messages.toClient.matchData.*;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.messages.toServer.game.ChooseActionResponse;
import it.polimi.ingsw.messages.toServer.game.EndTurnRequest;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class PlayPhase {
    private Controller controller;
    private TurnController turnController;
    private Player player;
    private Game lastTurnGameCopy;
    private final String RELOAD = "RELOAD";

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setTurnController(TurnController turnController) {
        this.turnController = turnController;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Controller getController() {
        return controller;
    }

    public TurnController getTurnController() {
        return turnController;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract void nextTurn();

    public abstract void handleResourceDiscard(String nickname);

    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {
        if (message instanceof ChooseActionResponse)
            getTurnController().doAction(((ChooseActionResponse) message).getActionChosen());
        if (message instanceof EndTurnRequest)
            getTurnController().endTurn();
    }

    public abstract void handleEndTriggered();
    public String toString(){
        return "Play Phase";
    }

    public void saveGameCopy(Game game){
        lastTurnGameCopy = GameCloneThroughJson.clone(game);
    }

    public void reloadGameCopy(boolean disconnection){
        assert lastTurnGameCopy!=null;
        //Inform all the clients that a previous game status is being restored
        controller.sendMessageToAll(new ReloadMatchData(true, disconnection));
        if (!disconnection){
            //Resend all the cards
        }
        controller.sendMessageToAll(new LoadDevelopmentCardGrid(lastTurnGameCopy.getDevelopmentCardGrid().getAvailableCards().stream().map(Card::getID).collect(Collectors.toList())));
        controller.sendMessageToAll(new UpdateMarketView(RELOAD, lastTurnGameCopy.getMarket().getMarketTray(), lastTurnGameCopy.getMarket().getSlideMarble()));
        for (Player player : controller.getPlayers()){
            if (player.isActive()) {
                for (Player gamePlayer : controller.getPlayers()) {

                    // 1. I create a map with the leader cards of the gamePlayer I am analyzing
                    Map<Integer, Boolean> leaderCards = new HashMap<>();
                    for (LeaderCard card : gamePlayer.getPersonalBoard().getLeaderCards()){
                        if (card.isActive())
                            leaderCards.put (card.getID(), true);
                        else{
                            if (gamePlayer.getNickname().equals(player.getNickname()))
                                leaderCards.put(card.getID(), false);
                        }
                    }
                    controller.getConnectionByNickname(player.getNickname()).sendMessageToClient(new ReloadLeaderCardsOwned(gamePlayer.getNickname(), leaderCards));

                    //2. Development cards
                    controller.getConnectionByNickname(player.getNickname()).sendMessageToClient(new ReloadDevelopmentCardOwned(gamePlayer.getNickname(), lastTurnGameCopy.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getHiddenDevelopmentCardColours(), lastTurnGameCopy.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getDevelopmentCardIdFirstRow()));

                    //3. Marker position
                    controller.getConnectionByNickname(player.getNickname()).sendMessageToClient(new UpdateMarkerPosition(gamePlayer.getNickname(), lastTurnGameCopy.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getMarkerPosition()));

                    //4. Depots status
                    controller.getConnectionByNickname(player.getNickname()).sendMessageToClient(new UpdateDepotsStatus(gamePlayer.getNickname(), lastTurnGameCopy.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getWarehouse().getWarehouseDepotsStatus(), lastTurnGameCopy.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getStrongboxStatus(), lastTurnGameCopy.getPlayerByNickname(gamePlayer.getNickname()).getPersonalBoard().getLeaderStatus()));

                }
            }
        }
        controller.sendMessageToAll(new ReloadMatchData(false, disconnection));
    }

    public Game getLastTurnGameCopy() {
        return lastTurnGameCopy;
    }

    public void setLastTurnGameCopy(Game lastTurnGameCopy) {
        this.lastTurnGameCopy = lastTurnGameCopy;
    }
}
