package it.polimi.ingsw.controller;

import it.polimi.ingsw.enumerations.ClientHandlerPhase;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.toClient.ChooseLeaderCardsRequest;
import it.polimi.ingsw.messages.toClient.LoadDevelopmentCardsMessage;
import it.polimi.ingsw.messages.toClient.LoadLeaderCardsMessage;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.utility.DevelopmentCardParser;
import it.polimi.ingsw.utility.LeaderCardParser;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetUpPhase implements GamePhase {
    Controller controller;

    @Override
    public void executePhase(Controller controller) {
        this.controller = controller;
        setUpLeaderCards();
        setUpDevelopmentCards();
    }

    private void setUpDevelopmentCards() {
        List<String> nicknames = controller.getNicknames();
        List<DevelopmentCard> developmentCards = null;

        try {
            developmentCards = DevelopmentCardParser.parseCards();
        } catch (UnsupportedEncodingException | InvalidArgumentException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < nicknames.size(); i++){
            controller.getConnectionByNickname(nicknames.get(i)).sendMessageToClient(new LoadDevelopmentCardsMessage());
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
        Collections.shuffle(nicknames);

        for (int i = 0; i < nicknames.size(); i++){
            List<LeaderCard> cards = leaderCards.subList(i * 4, i * 4 + 4);
            try {
                controller.getGame().addPlayer(nicknames.get(i), cards);
                //TODO set inkwell
            } catch (InvalidArgumentException | InvalidPlayerAddException e) {
                e.printStackTrace();
            }
            controller.getConnectionByNickname(nicknames.get(i)).sendMessageToClient(new LoadLeaderCardsMessage(leaderCards));
            controller.getConnectionByNickname(nicknames.get(i)).sendMessageToClient(new ChooseLeaderCardsRequest(cards.stream().map(x -> x.getID()).collect(Collectors.toList())));
            controller.getConnectionByNickname(nicknames.get(i)).setClientHandlerPhase(ClientHandlerPhase.WAITING_DISCARDED_LEADER_CARDS);
        }

    }
}
