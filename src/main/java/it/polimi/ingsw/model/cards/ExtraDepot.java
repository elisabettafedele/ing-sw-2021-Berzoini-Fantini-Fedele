package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.depot.LeaderDepot;

import java.io.Serializable;
import java.util.Objects;

/**
 * The class represents the extra depot available via some of the {@link LeaderCard}
 */
public class ExtraDepot implements Serializable {

    private static final long serialVersionUID = 4421532865781468978L;
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

    /**
     * get the {@link LeaderDepot} belonging to a {@link LeaderCard}
     * @return the {@link LeaderDepot} belonging to a {@link LeaderCard}
     */
    public LeaderDepot getLeaderDepot() {
        return this.leaderDepot;
    }

    @Override
    public String toString() {
        return "ExtraDepot{" +
                "leaderDepot=" + leaderDepot +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraDepot that = (ExtraDepot) o;
        return Objects.equals(that.leaderDepot.getResourceType(), this.leaderDepot.getResourceType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaderDepot);
    }
}
