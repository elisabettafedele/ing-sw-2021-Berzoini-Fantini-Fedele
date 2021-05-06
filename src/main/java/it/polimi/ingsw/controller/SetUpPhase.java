package it.polimi.ingsw.controller;

import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.enumerations.ResourceStorageType;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.*;
import it.polimi.ingsw.messages.toServer.ChooseLeaderCardsResponse;
import it.polimi.ingsw.messages.toServer.ChooseResourceAndStorageTypeResponse;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.utility.DevelopmentCardParser;
import it.polimi.ingsw.utility.LeaderCardParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetUpPhase implements GamePhase {
    Controller controller;
    Map<String, Integer> initialResourceByNickname;

    @Override
    public void executePhase(Controller controller) {
        this.controller = controller;
        sendLightCards();
        setUpLeaderCards();
    }

    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {
        if (message instanceof ChooseLeaderCardsResponse) {
            removeLeaderCards(((ChooseLeaderCardsResponse) message).getDiscardedLeaderCards(), clientHandler);
        }

        if (message instanceof ChooseResourceAndStorageTypeResponse)
            storeResources(((ChooseResourceAndStorageTypeResponse) message).getStorage(), clientHandler);
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
        clientHandler.sendMessageToClient(new ChooseResourceAndStorageTypeRequest(resourceTypes, ResourceStorageType.getWarehouseDepots(), getNumberOfInitialResourcesByNickname(clientHandler.getNickname())));
        clientHandler.setClientHandlerPhase(ClientHandlerPhase.WAITING_CHOOSE_RESOURCE_TYPE);
    }

    private void storeResources(Map<String, String> storage, ClientHandler clientHandler) {
        Player player = controller.getPlayerByNickname(clientHandler.getNickname());
        for (String resource : storage.keySet()) {
            try {
                player.getPersonalBoard().addResources(ResourceStorageType.valueOf(storage.get(resource)), Resource.valueOf(resource), 1);
            } catch (InvalidDepotException | InvalidArgumentException | InvalidResourceTypeException | InsufficientSpaceException e) {
                e.printStackTrace();
            }
        }
        sendSetUpFinishedMessage(clientHandler);
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
