package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.depot.LeaderDepot;

/**
 * The class represents the extra depot available via some of the {@link LeaderCard}
 */
public class ExtraDepot{

    private LeaderDepot leaderDepot;

    /**
     * The constructor receive a LeaderDepot to associate to the effect
     * @param leaderDepot a {@link LeaderDepot} not null
     * @throws InvalidArgumentException
     */
    public ExtraDepot(LeaderDepot leaderDepot) throws InvalidArgumentException {
        if (leaderDepot == null){
            throw new InvalidArgumentException();
        }
        this.leaderDepot = leaderDepot;
    }

    public LeaderDepot getLeaderDepot() {
        return this.leaderDepot;
    }
}
