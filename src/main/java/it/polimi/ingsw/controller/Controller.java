package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.exceptions.InsufficientSpaceException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidDepotException;
import it.polimi.ingsw.exceptions.InvalidResourceTypeException;
import it.polimi.ingsw.messages.toClient.ChooseResourceAndStorageTypeRequest;
import it.polimi.ingsw.messages.toServer.ChooseLeaderCardsResponse;
import it.polimi.ingsw.messages.toServer.ChooseResourceAndStorageTypeResponse;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.player.Player;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {
    private Game game;
    private GamePhase gamePhase;
    private List<Player> players;
    private List<ClientHandler> clientHandlers;
    private ReentrantLock lockPlayers = new ReentrantLock(true);

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
        lockPlayers.lock();
        Player player = null;
        try {
            for (Player p : players) {
                if (p.getNickname().equals(nickname))
                    player = p;
            }
        } finally {
            lockPlayers.unlock();
        }
        return player;
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
        if (message instanceof ChooseLeaderCardsResponse)
            handleChooseLeaderCardResponse((ChooseLeaderCardsResponse) message, nickname);

        if (message instanceof ChooseResourceAndStorageTypeResponse)
            handleChooseResourceAndStorageTypeRequest((ChooseResourceAndStorageTypeResponse) message, nickname);
    }

    public void handleChooseLeaderCardResponse(ChooseLeaderCardsResponse message, String nickname){
        assert (gamePhase instanceof SetUpPhase);
        List <Integer> discardedCards = message.getDiscardedLeaderCards();
        /*
        for (Integer id : discardedCards) {
            getPlayerByNickname(nickname).getPersonalBoard().removeLeaderCard(id);
        }
         */
        if (((SetUpPhase) gamePhase).getNumberOfInitialResourcesByNickname(nickname) == 0){
            //TODO send a message with his personal board view
            getConnectionByNickname(nickname).setClientHandlerPhase(ClientHandlerPhase.WAITING_HIS_TURN);
            return;
        }

        //Available resource types
        List<String> resourceTypes = Stream.of(Resource.values()).map(Enum::name).collect(Collectors.toList());

        //Available depots
        List<String> storageTypes = new ArrayList<>();
        storageTypes.add(ResourceStorageType.WAREHOUSE_FIRST_DEPOT.name());
        storageTypes.add(ResourceStorageType.WAREHOUSE_SECOND_DEPOT.name());
        storageTypes.add(ResourceStorageType.WAREHOUSE_THIRD_DEPOT.name());

        //Number of resources to choose and store
        int quantity = ((SetUpPhase) gamePhase).getNumberOfInitialResourcesByNickname(nickname);

        //I ask to the player to choose one or two type of resource
        getConnectionByNickname(nickname).sendMessageToClient(new ChooseResourceAndStorageTypeRequest(resourceTypes, storageTypes, quantity));
        getConnectionByNickname(nickname).setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_RESOURCE_TYPE);
    }

    private void handleChooseResourceAndStorageTypeRequest(ChooseResourceAndStorageTypeResponse message, String nickname){
        Player player = getPlayerByNickname(nickname);
        Map<String, String> storage = message.getStorage();

        for (String resource : storage.keySet()) {
            try {
                player.getPersonalBoard().addResources(ResourceStorageType.valueOf(storage.get(resource)), Resource.valueOf(resource), 1);
            } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException | InsufficientSpaceException e) {
                e.printStackTrace();
            }
        }
    }


}
