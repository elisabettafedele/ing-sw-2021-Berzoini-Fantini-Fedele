package it.polimi.ingsw.controller;

import it.polimi.ingsw.common.ClientHandlerInterface;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.exceptions.InsufficientSpaceException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidDepotException;
import it.polimi.ingsw.exceptions.InvalidResourceTypeException;
import it.polimi.ingsw.messages.toClient.ChooseResourceAndStorageTypeRequest;
import it.polimi.ingsw.messages.toClient.WaitingInTheLobbyMessage;
import it.polimi.ingsw.messages.toServer.ChooseLeaderCardsResponse;
import it.polimi.ingsw.messages.toServer.ChooseResourceAndStorageTypeResponse;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameMessageManager {

    private Game game;
    private Controller controller;

    public void handleMessage(MessageToServer message, ClientHandlerInterface clientHandler){
        String nickname = clientHandler.getNickname();
        if (message instanceof ChooseLeaderCardsResponse)
            handleChooseLeaderCardResponse((ChooseLeaderCardsResponse) message, clientHandler);

        if (message instanceof ChooseResourceAndStorageTypeResponse)
            handleChooseResourceAndStorageTypeRequest((ChooseResourceAndStorageTypeResponse) message, clientHandler);
    }

    public synchronized void  handleChooseLeaderCardResponse(ChooseLeaderCardsResponse message, ClientHandlerInterface clientHandler){
        String nickname = clientHandler.getNickname();
        Player player = game.getPlayerByNickname(nickname);
        List<Integer> discardedCards = message.getDiscardedLeaderCards();

        for (Integer id : discardedCards) {
            player.getPersonalBoard().removeLeaderCard(id);
        }

        if (((SetUpPhase) controller.getGamePhase()).getNumberOfInitialResourcesByNickname(nickname) == 0){
            //TODO send a message with his personal board view
            clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_HIS_TURN);
            clientHandler.sendMessageToClient(new WaitingInTheLobbyMessage());
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
        int quantity = ((SetUpPhase) controller.getGamePhase()).getNumberOfInitialResourcesByNickname(nickname);

        //I ask to the player to choose one or two type of resource
        clientHandler.sendMessageToClient(new ChooseResourceAndStorageTypeRequest(resourceTypes, storageTypes, quantity));
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_RESOURCE_TYPE);
    }

    private synchronized void handleChooseResourceAndStorageTypeRequest(ChooseResourceAndStorageTypeResponse message, ClientHandlerInterface clientHandler){
        Player player = game.getPlayerByNickname(clientHandler.getNickname());
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
