package it.polimi.ingsw.model.game;

import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.player.VaticanReportSection;

import java.util.*;

/**
 * The class represents the Game's FaithTrack. It includes all the attributes that FaithTrack has.
 */

public class FaithTrack {
    private Iterator<VaticanReportSection> vaticanReportSectionIterator;
    private final int length;
    private LinkedHashMap< Integer , Integer > trackVictoryPoints;
    private VaticanReportSection currentSection;

    /**
     * Constructs the FaithTrack and automatically assigns to currentSection the first next element of the vaticanReportSectionIterator
     * @throws InvalidArgumentException
     */
    public FaithTrack() {
        length = 24;
        List<VaticanReportSection> tempList= new ArrayList<>();
        try {
            tempList.add(new VaticanReportSection(5, 8, 2));
            tempList.add(new VaticanReportSection(12, 16, 3));
            tempList.add(new VaticanReportSection(19, 24, 4));
        } catch (InvalidArgumentException e){}
        vaticanReportSectionIterator=tempList.iterator();
        currentSection= vaticanReportSectionIterator.next();
        trackVictoryPoints=new LinkedHashMap<>();
        trackVictoryPoints.put(3,1);
        trackVictoryPoints.put(6,2);
        trackVictoryPoints.put(9,4);
        trackVictoryPoints.put(12,6);
        trackVictoryPoints.put(15,9);
        trackVictoryPoints.put(18,12);
        trackVictoryPoints.put(21,16);
        trackVictoryPoints.put(24,20);
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
    public int getVictoryPoints(int playerPosition) {
        int victoryPoints = 0;
        for(Map.Entry<Integer,Integer> entry : trackVictoryPoints.entrySet()){
            if(entry.getKey()<playerPosition){
                victoryPoints= entry.getValue();
            }
            if(entry.getKey()==playerPosition){
                return entry.getValue();
            }
            if(entry.getKey()>playerPosition){
                return victoryPoints;
            }
        }
        return victoryPoints;
    }
}
