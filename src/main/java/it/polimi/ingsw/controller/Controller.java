package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.messages.toServer.ChooseLeaderCardsResponse;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.player.Player;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    private Game game;
    private GamePhase gamePhase;
    private List<Player> players;
    private List<ClientHandler> clientHandlers;

    public Controller(GameMode gameMode) throws InvalidArgumentException, UnsupportedEncodingException {
        this.game = new Game(gameMode);
        this.clientHandlers = new LinkedList<>();
        //TODO this.gamePhase = new GamePhase() link the setup phase

    }

    public Game getGame(){
        return this.game;
    }

    public GamePhase getGamePhase(){
        return this.gamePhase;
    }

    public List<String> getNicknames(){
        return clientHandlers.stream().map(x -> x.getNickname()).collect(Collectors.toList());
    }

    public ClientHandler getConnectionByNickname(String nickname){
        for (ClientHandler clientHandler : clientHandlers){
            if (clientHandler.getNickname().equals(nickname)){
                return clientHandler;
            }
        }
        return null;
    }

    public Player getPlayerByNickname(String nickname){
        for (Player p : players){
            if (p.getNickname().equals(nickname))
                return p;
        }
        return null;
    }

    public void addConnection(ClientHandler connection){
        this.clientHandlers.add(connection);
    }

    public void removeConnection(ClientHandler connection){
        this.clientHandlers.remove(connection);
    }

    public void start(){
        this.gamePhase = new SetUpPhase();
        this.gamePhase.executePhase(this);
    }

    public void handleMessage(MessageToServer message, String nickname){
        if (message instanceof ChooseLeaderCardsResponse){
            handleChooseLeaderCardResponse((ChooseLeaderCardsResponse) message, nickname);
        }
    }

    public void handleChooseLeaderCardResponse(ChooseLeaderCardsResponse message, String nickname){
        for (Integer id : message.getDiscardedLeaderCards())
            getPlayerByNickname(nickname).getPersonalBoard().removeLeaderCard(id);

    }

}
