package it.polimi.ingsw.controller;

import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.ChooseLeaderCardsRequest;
import it.polimi.ingsw.messages.toClient.LoadDevelopmentCardsMessage;
import it.polimi.ingsw.messages.toClient.LoadLeaderCardsMessage;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.utility.DevelopmentCardParser;
import it.polimi.ingsw.utility.LeaderCardParser;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class SetUpPhase implements GamePhase {
    Controller controller;

    @Override
    public void executePhase(Controller controller) {
        this.controller = controller;
        setUpDevelopmentCards();
        setUpLeaderCards();
    }



    private void setUpDevelopmentCards() {
        List<String> nicknames = controller.getNicknames();
        List<DevelopmentCard> developmentCards = null;

        try {
            developmentCards = DevelopmentCardParser.parseCards();
        } catch (UnsupportedEncodingException | InvalidArgumentException e) {
            e.printStackTrace();
        }

        Map<Integer, List<String>> lightDevelopmentCards = new HashMap<>();
        for (DevelopmentCard developmentCard : developmentCards){
            List<String> description = new ArrayList<>();
            description.add(developmentCard.toString());
            description.add(developmentCard.getPathImageFront());
            description.add(developmentCard.getPathImageBack());
            lightDevelopmentCards.put(developmentCard.getID(), description);
        }

        for (int i = 0; i < nicknames.size(); i++){
            controller.getConnectionByNickname(nicknames.get(i)).sendMessageToClient(new LoadDevelopmentCardsMessage(lightDevelopmentCards));
        }
    }


    //TODO: manage exception
    private void setUpLeaderCards() {
        List<String> nicknames = controller.getNicknames();
        List<LeaderCard> leaderCards = null;
        try {
            leaderCards = LeaderCardParser.parseCards();
        } catch (Exception | JsonFileNotFoundException e) { }
        Collections.shuffle(leaderCards);
        //Collections.shuffle(nicknames);

        for (int i = 0; i < nicknames.size(); i++){
            List<LeaderCard> cards = new LinkedList<>();
            for(int j=i*4; j<i*4 +4; j++)
                cards.add(leaderCards.get(j));
            try {
                controller.getGame().addPlayer(nicknames.get(i), cards, getInitialFaithPoints(i), hasInkwell(i));
            } catch (InvalidArgumentException | InvalidPlayerAddException e) {
                e.printStackTrace();
            }
            controller.getConnectionByNickname(nicknames.get(i)).sendMessageToClient(new LoadLeaderCardsMessage(leaderCards));
            controller.getConnectionByNickname(nicknames.get(i)).sendMessageToClient(new ChooseLeaderCardsRequest(cards.stream().map(Card::getID).collect(Collectors.toList())));
            controller.getConnectionByNickname(nicknames.get(i)).setClientHandlerPhase(ClientHandlerPhase.WAITING_DISCARDED_LEADER_CARDS);
        }

    }

    private boolean hasInkwell(int index){
        return index == 0;
    }

    private int getInitialFaithPoints(int index){
        return index > 1 ? 1 : 0;
    }























    public int getNumberOfInitialResourcesByNickname(String nickname){
        List<Player> players;
        try {
            players = controller.getGame().getPlayers();
        } catch (InvalidMethodException | ZeroPlayerException e) {
            e.printStackTrace();
            return -1;
        }
        assert (players.size() < 5);
        if (players.indexOf(controller.getPlayerByNickname(nickname)) == 0)
            return 0;
        if (players.indexOf(controller.getPlayerByNickname(nickname)) < 3 )
            return 1;
        return 2;
    }
}
