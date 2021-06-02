package it.polimi.ingsw.controller.game_phases;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.messages.toServer.MessageToServer;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.server.ClientHandler;

public abstract class EndPhase implements GamePhase {
    /**
     * Abstract class to manage the end phase
     */
    private Controller controller;

    @Override
    public void executePhase(Controller controller) {
        this.controller = controller;
        countVictoryPoints();
        notifyResults();
    }

    public void handleMessage(MessageToServer message, ClientHandler clientHandler){
        //ignored
    }

    /**
     * Method to calculate the victory points of each player
     */
    private void countVictoryPoints(){
        for (Player player : controller.getPlayers()){
            try {
                //Development Cards
                player.addVictoryPoints(player.getPersonalBoard().getDevelopmentCards().stream().map(Card::getVictoryPoints).mapToInt(i -> i).sum());

                //FaithTrack
                player.addVictoryPoints(controller.getGame().getFaithTrack().getVictoryPoints(player.getPersonalBoard().getMarkerPosition()));

                //Leader Cards
                player.addVictoryPoints(player.getPersonalBoard().getLeaderCards().stream().filter(LeaderCard::isActive).map(Card::getVictoryPoints).mapToInt(i -> i).sum());

                //Resources
                player.addVictoryPoints(player.getPersonalBoard().countResourceNumber() / 5);
            } catch (InvalidArgumentException e){
                e.printStackTrace();
            }
        }
    }

    public abstract void notifyResults();

    public Controller getController(){
        return controller;
    }

    public String toString(){
        return "End Phase";
    }
}
