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
    //private Player LorenzoIlMagnifico = controller.getGame().

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

    public void shuffleTokens() {
        Collections.shuffle((List<SoloActionToken>) this.tokens);
    }

    @Override
    public void executePhase(Controller controller) {
        while(!turnController.isEndTriggered()){
            turnController.start(this.player);
            if(!turnController.isEndTriggered())
                useActionToken();
        }
    }

    private void useActionToken() {
        SoloActionToken token = tokens.remove(); //removing the first of the queue;
        tokens.add(token); //saving the used token at the end of the queue;

        token.useActionToken(this);


    }

    public void discardDevelopmentCards(int numOfCard2Remove, FlagColor flagColor){
        List<DevelopmentCard> availableCards = controller.getGame().getDevelopmentCardGrid().getAvailableCards();
        availableCards = availableCards.stream().filter(dc -> dc.getFlag().getFlagColor().equals(flagColor)).collect(Collectors.toList());
        //mi serve solo la carta di livello più basso!
        if(availableCards.size() >= numOfCard2Remove){

        }
    }

    public void moveBlackCross(int step){
        blackCrossPosition += step;
    }

    @Override
    public void handleResourceDiscard(String nickname) {
        moveBlackCross(1);
    }


    @Override
    public void handleMessage(MessageToServer message, ClientHandler clientHandler) {

    }
}
