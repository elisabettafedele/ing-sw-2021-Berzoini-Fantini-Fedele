package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.actions.SoloActionToken;
import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.Server.ClientHandler;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.InvalidMethodException;
import it.polimi.ingsw.exceptions.ZeroPlayerException;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.utility.SoloActionTokenParser;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class SinglePlayerPlayPhase extends PlayPhase implements GamePhase{
    private Controller controller;
    private TurnController turnController;
    private Player player;
    private int blackCrossPosition;
    private Queue<SoloActionToken> tokens;

    public SinglePlayerPlayPhase(Controller controller){
        this.controller = controller;
        try {
           this.player=controller.getGame().getSinglePlayer();
        } catch (InvalidMethodException e) {
            e.printStackTrace();
        } catch (ZeroPlayerException e) {
            e.printStackTrace();
        }
        turnController= new TurnController(controller,player);
        this.tokens = SoloActionTokenParser.parseTokens();
        shuffleTokens();
    }

    @Override
    public void executePhase(Controller controller) {
        while(!turnController.isEndTriggered()){ //TODO: check if only this is ok, should be
            turnController.start(this.player);
            if(!turnController.isEndTriggered()) {
                useActionToken();//TODO: view communication.
            }
            else{
                player.setWinner(true);
            }
        }
        //nextPhase
    }

    private void useActionToken() {
        SoloActionToken token = tokens.remove(); //removing the first of the queue;
        tokens.add(token); //saving the used token at the end of the queue;
        token.useActionToken(this);
    }

    public void shuffleTokens() {
        Collections.shuffle((List<SoloActionToken>) this.tokens);
    }

    public void discardDevelopmentCards(int numOfCard2Remove, FlagColor flagColor){
        for(int i = 0; i < numOfCard2Remove; i++){
            List<DevelopmentCard> availableCards = controller.getGame().getDevelopmentCardGrid().getAvailableCards();
            List<DevelopmentCard> rightColorCards = availableCards.stream().filter(dc -> dc.getFlag().getFlagColor().equals(flagColor)).collect(Collectors.toList());

            if(rightColorCards.size() > 0){
                DevelopmentCard card2Remove = getLowerCard(rightColorCards);
                try {
                    controller.getGame().getDevelopmentCardGrid().removeCard(card2Remove);
                } catch (InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }else{
                turnController.setEndTriggerToTrue();
                player.setWinner(false); //useless cause is false by default, leave here just to remember
                //TODO: lost the game communication
                break;
            }

        }
    }

    private DevelopmentCard getLowerCard(List<DevelopmentCard> availableCards) {
        DevelopmentCard lowerDevelopmentCard = availableCards.get(0);
        for(DevelopmentCard dc : availableCards){
            if(dc.getFlag().getFlagLevel().getValue() < lowerDevelopmentCard.getFlag().getFlagLevel().getValue()){
                lowerDevelopmentCard = dc;
            }
        }
        return lowerDevelopmentCard;
    }

    public void moveBlackCross(int step){
        blackCrossPosition += step;
        turnController.checkFaithTrack(); //TODO: need to be the checked for the black cross
        //TODO, manage eventual Lorenzo's victory.
    }

    @Override
    public void nextTurn() {
        //TODO
    }

    @Override
    public void handleResourceDiscard(String nickname) {
        moveBlackCross(1);
    }

    //TODO: check this
    @Override
    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {

    }
}
