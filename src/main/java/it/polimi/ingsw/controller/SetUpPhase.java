package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.*;
import it.polimi.ingsw.messages.toServer.ChooseLeaderCardsResponse;
import it.polimi.ingsw.messages.toServer.ChooseResourceTypeResponse;
import it.polimi.ingsw.messages.toServer.ChooseStorageTypeResponse;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.utility.DevelopmentCardParser;
import it.polimi.ingsw.utility.LeaderCardParser;

import java.util.*;
import java.util.stream.Collectors;

public class SetUpPhase implements GamePhase {
    Controller controller;
    Map<String, Integer> initialResourceByNickname;
    Map<String, List<Resource>> resourcesToStoreByNickname;


    @Override
    public void executePhase(Controller controller) {
        this.controller = controller;
        resourcesToStoreByNickname = new HashMap<>();
        sendLightCards();
        setUpLeaderCards();
    }

    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {
        if (message instanceof ChooseLeaderCardsResponse) {
            removeLeaderCards(((ChooseLeaderCardsResponse) message).getDiscardedLeaderCards(), clientHandler);
        }

        if (message instanceof ChooseResourceTypeResponse)
            setInitialResourcesByNickname(((ChooseResourceTypeResponse) message).getResources(), clientHandler);

        if (message instanceof ChooseStorageTypeResponse)
            storeResource(((ChooseStorageTypeResponse) message).getResource(), ((ChooseStorageTypeResponse) message).getStorageType(), clientHandler);
    }

    private void setUpLeaderCards() {
        List<LeaderCard> leaderCards = LeaderCardParser.parseCards();
        Collections.shuffle(leaderCards);

        //Shuffle the order of the nicknames
        List<String> nicknames = controller.getNicknames();
        Collections.shuffle(nicknames);

        //I set the number of resources for each player
        initialResourceByNickname = new HashMap<>();

        for (int i = 0; i < nicknames.size(); i++) {

            // 1. I assign leader cards and add the player to the game
            List<LeaderCard> leaderCardsAssigned = assignLeaderCards(leaderCards, 4 * i, 4 * i + 4);
            addPlayerToTheGame(nicknames.get(i), leaderCardsAssigned, i);

            // 2. I set the number of resources for each player
            initialResourceByNickname.put(nicknames.get(i), getNumberOfInitialResourcesByIndex(i));

            // 3. I send to the client the cards and the leader cards and the card chosen
            ClientHandler connection = controller.getConnectionByNickname(nicknames.get(i));
            connection.setClientHandlerPhase(ClientHandlerPhase.WAITING_DISCARDED_LEADER_CARDS);
            connection.sendMessageToClient(new ChooseLeaderCardsRequest(leaderCardsAssigned.stream().map(Card::getID).collect(Collectors.toList())));
        }

    }


    private void removeLeaderCards(List<Integer> discardedCards, ClientHandler clientHandler) {
        String nickname = clientHandler.getNickname();
        Player player = controller.getPlayerByNickname(nickname);
        for (Integer id : discardedCards)
            player.getPersonalBoard().removeLeaderCard(id);

        if (getNumberOfInitialResourcesByNickname(nickname) == 0) {
            sendSetUpFinishedMessage(clientHandler);
        } else {
            assignResources(clientHandler);
        }
    }

    private void assignResources(ClientHandler clientHandler) {
        List<Resource> resourceTypes = Resource.realValues();
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_RESOURCE_TYPE);
        clientHandler.sendMessageToClient(new ChooseResourceTypeRequest(resourceTypes, getNumberOfInitialResourcesByNickname(clientHandler.getNickname())));
    }

    private void setInitialResourcesByNickname(List<Resource> resources, ClientHandler clientHandler){
        resourcesToStoreByNickname.put(clientHandler.getNickname(), resources);
        List<String> availableDepots = ResourceStorageType.getWarehouseDepots();
        if (resources.size() == 2 && resources.get(0) == resources.get(1))
            availableDepots.remove(ResourceStorageType.WAREHOUSE_FIRST_DEPOT.name());
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_STORAGE_TYPE);
        clientHandler.sendMessageToClient(new ChooseStorageTypeRequest(resources.get(0), availableDepots, true));
    }

    private void storeResource(Resource resource, String storageType, ClientHandler clientHandler){
        Player player = controller.getPlayerByNickname(clientHandler.getNickname());
        resourcesToStoreByNickname.get(player.getNickname()).remove(resource);
        try {
            player.getPersonalBoard().addResources(ResourceStorageType.valueOf(storageType), resource, 1);
        } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException | InsufficientSpaceException e) {
            e.printStackTrace();
        }
        if (resourcesToStoreByNickname.get(player.getNickname()).isEmpty()) {
            sendSetUpFinishedMessage(clientHandler);
        } else {
            Resource resourceType = resourcesToStoreByNickname.get(player.getNickname()).get(0);
            List<String> availableStorage = player.getPersonalBoard().getWarehouse().getAvailableWarehouseDepotsForResourceType(resourceType).stream().map(x -> x.name()).collect(Collectors.toList());
            clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_STORAGE_TYPE);
            clientHandler.sendMessageToClient(new ChooseStorageTypeRequest(resourceType, availableStorage, true));
        }

    }

    private void sendSetUpFinishedMessage(ClientHandler clientHandler) {
        //TODO send a message with his personal board view
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.SET_UP_FINISHED);
        clientHandler.sendMessageToClient(new TextMessage("The play phase will start in a while..."));
        endPhaseManager();
    }

    private void endPhaseManager() {
        List<String> nicknames = controller.getNicknames();
        for (String nickname : nicknames) {
            if (controller.getConnectionByNickname(nickname).getClientHandlerPhase() != ClientHandlerPhase.SET_UP_FINISHED)
                return;
        }
        controller.setGamePhase(nicknames.size() > 1 ? new MultiplayerPlayPhase(controller) : new SinglePlayerPlayPhase(controller));
    }


    private void sendLightCards() {
        for (String nickname : controller.getNicknames()) {
            ClientHandler connection = controller.getConnectionByNickname(nickname);
            connection.sendMessageToClient(new LoadDevelopmentCardsMessage(getLightCards(DevelopmentCardParser.parseCards())));
            connection.sendMessageToClient(new LoadLeaderCardsMessage(LeaderCardParser.parseCards()));
        }
    }


    private List<LeaderCard> assignLeaderCards(List<LeaderCard> cards, int start, int end) {
        List<LeaderCard> IDs = new LinkedList<>();
        for (int i = start; i < end; i++)
            IDs.add(cards.get(i));
        return IDs;
    }


    private void addPlayerToTheGame(String nickname, List<LeaderCard> leaderCardsAssigned, int index) {
        try {
            controller.getGame().addPlayer(nickname, leaderCardsAssigned, getInitialFaithPoints(index), hasInkwell(index));
        } catch (InvalidArgumentException | InvalidPlayerAddException e) {
            e.printStackTrace();
        }
    }

    private boolean hasInkwell(int index) {
        return index == 0;
    }

    private int getInitialFaithPoints(int index) {
        return index > 1 ? 1 : 0;
    }

    private Map<Integer, List<String>> getLightCards(List<DevelopmentCard> cards) {
        Map<Integer, List<String>> lightCards = new HashMap<>();

        for (DevelopmentCard card : cards) {
            List<String> description = new ArrayList<>();
            description.add(card.toString());
            description.add(card.getPathImageFront());
            description.add(card.getPathImageBack());
            lightCards.put(card.getID(), description);
        }

        return lightCards;
    }

    private int getNumberOfInitialResourcesByIndex(int index) {
        if (index == 0)
            return 0;
        if (index < 3)
            return 1;
        return 2;
    }

    public int getNumberOfInitialResourcesByNickname(String nickname) {
        return initialResourceByNickname.get(nickname);
    }

    public String toString() {
        return "Set Up Phase";
    }
}
