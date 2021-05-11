package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.player.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class EndPhase implements GamePhase{

    private Controller controller;
    private Map<Player, Integer> victoryPoints;

    public EndPhase(){
        victoryPoints = new HashMap<>();
    }

    @Override
    public void executePhase(Controller controller) {
        this.controller = controller;
        countVictoryPoints();


    }

    private void countVictoryPoints(){
        for (Player player : controller.getPlayers()){
            try {
                //Development Cards
                player.addVictoryPoints(player.getPersonalBoard().getDevelopmentCards().stream().map(x -> x.getVictoryPoints()).mapToInt(i -> i).sum());
                //FaithTrack
                player.addVictoryPoints(controller.getGame().getFaithTrack().getVictoryPoints(player.getPersonalBoard().getMarkerPosition()));
                //Previous favor tiles are already counted

                //Leader Cards
                player.addVictoryPoints(player.getPersonalBoard().getLeaderCards().stream().filter(x -> x.isActive()).map(x -> x.getVictoryPoints()).mapToInt(i -> i).sum());
                //Resources
                player.addVictoryPoints(player.getPersonalBoard().countResourceNumber() / 5);
            } catch (InvalidArgumentException e){
                e.printStackTrace();
            }
        }
    }

    public abstract void notifyResults();

}
