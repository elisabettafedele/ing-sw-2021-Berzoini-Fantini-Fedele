package it.polimi.ingsw.model.player;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.game.FaithTrack;

/**
 * The class represents a Vatican Report Section of the {@link FaithTrack}
 */

public class VaticanReportSection {

    private final int  start;
    private final int  end;
    private final int  popeFavorPoints;
    private boolean vaticanReportAvailable;

    /**
     *
     * @param start the number of the box on the {@link FaithTrack} where the {@link VaticanReportSection} starts;
     * @param end the number of the box on the {@link FaithTrack} where the {@link VaticanReportSection} ends;
     * @param popesFavorPoints the number of victory points to be added to all eligible {@link Player} during {@link VaticanReportSection} activation;
     * @throws InvalidArgumentException
     */
    public VaticanReportSection(int start, int end, int popesFavorPoints) throws InvalidArgumentException {
        if(start<0||end<0||end<start||popesFavorPoints<0){
            throw new InvalidArgumentException();
        }
        this.start = start;
        this.end = end;
        this.popeFavorPoints = popesFavorPoints;
        this.vaticanReportAvailable = true;
    }

    /**
     *
     * @return Returns start
     */
    public int getStart() {
        return start;
    }

    /**
     *
     * @return return end
     */
    public int getEnd() {
        return end;
    }

    /**
     *
     * @return return Pope's favor points
     */
    public int getPopeFavorPoints() {
        return popeFavorPoints;
    }

    /**
     *
     * @return returns true is vatican report is available, false otherwise
     */
    public boolean isVaticanReportAvailable() {
        return vaticanReportAvailable;
    }

    /**
     * Makes vatican report unavailable
     */
    public void setVaticanReportUnavailable() {
        vaticanReportAvailable=false;
    }
}
