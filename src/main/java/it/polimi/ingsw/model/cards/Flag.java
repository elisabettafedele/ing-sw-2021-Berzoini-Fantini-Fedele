package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.exceptions.InvalidArgumentException;

/**
 * Flag represents the {@link DevelopmentCard} flag, composed of a {@link Level} and a {@link FlagColor}.
 * The class is also utilized for {@link LeaderCard} activation cost.
 */
public class Flag {

    private FlagColor flagColor;
    private Level flagLevel;

    /**
     * Constructs a Flag
     * @param flagColor the type (color) of the flag
     * @param flagLevel the level of the flag
     * @throws InvalidArgumentException if flagColor or flagLevel are null.
     */
    public Flag(FlagColor flagColor, Level flagLevel) throws InvalidArgumentException {
        if(flagColor == null || flagLevel == null){
            throw new InvalidArgumentException();
        }
        this.flagColor = flagColor;
        this.flagLevel = flagLevel;
    }

    /**
     * Get the flag color/type
     * @return the flag color/type
     */
    public FlagColor getFlagColor() {
        return flagColor;
    }

    /**
     * Get the flag level
     * @return the flag level
     */
    public Level getFlagLevel() {
        return flagLevel;
    }
}
