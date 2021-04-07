package it.polimi.ingsw.model.player;

import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The class represents the Game's FaithTrack. It includes all the attributes that FaithTrack has.
 */

public class FaithTrack {
    private Iterator<VaticanReportSection> vaticanReportSectionIterator;
    private final int length;
    private HashMap< Integer , Integer > trackVictoryPoints;
    private VaticanReportSection currentSection;

    /**
     * Constructs the FaithTrack and automatically assigns to currentSection the first next element of the vaticanReportSectionIterator
     * @param vaticanReportSectionIterator the iterator on an ArrayList of {@link VaticanReportSection}
     * @param length the number of boxes in the {@link FaithTrack}
     * @param trackVictoryPoints a </key,value> HashMap containing pairs of <boxNumber, victoryPoints> Integer values
     * @throws InvalidArgumentException
     */
    public FaithTrack(Iterator<VaticanReportSection> vaticanReportSectionIterator, int length, HashMap<Integer, Integer> trackVictoryPoints) throws InvalidArgumentException{
        if(vaticanReportSectionIterator==null||length<0||trackVictoryPoints==null){
            throw new InvalidArgumentException();
        }
        this.vaticanReportSectionIterator = vaticanReportSectionIterator;
        this.length = length;
        this.trackVictoryPoints = trackVictoryPoints;
        currentSection= vaticanReportSectionIterator.next();
    }

    /**
     * Returns true if the player position on the {@link FaithTrack}, passed as parameter, is higher enough to activate the {@link VaticanReportSection}.
     * This happens only if playerPosition is greater or equals to currentSection's end attribute. In other cases returns false;
     * @param playerPosition player position on the {@link FaithTrack};
     * @return
     */
    public boolean isVaticanReport(int playerPosition){
        return currentSection.getEnd() <= playerPosition;
    }

    /**
     * Returns the {@link VaticanReportSection} stored in currentSection and suddenly changes currentSection to the next {@link VaticanReportSection}
     * returned by vaticanReportSectionIterator.If it is the last VaticanReportSection currentSection remains the same;
     */
    public VaticanReportSection getCurrentSection() {
        VaticanReportSection returnedSection=currentSection;
        if(vaticanReportSectionIterator.hasNext()){
            currentSection= vaticanReportSectionIterator.next();
        }
        return returnedSection;
    }

    /**
     *
     * @param playerPosition number of  box on the {@link FaithTrack} on which the player is.
     * @return sum of all victory points a player is eligible to achieve if he's in {@link FaithTrack}'box playerPosition
     */
    public int getVictoryPoints(int playerPosition){
        int victoryPoints= 0;
        victoryPoints = trackVictoryPoints.entrySet().stream()
                .filter(hashmap -> hashmap.getKey() <= playerPosition)
                .mapToInt(Map.Entry::getValue).sum();
        return victoryPoints;
    }
}
