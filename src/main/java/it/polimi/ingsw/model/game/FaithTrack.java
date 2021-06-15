package it.polimi.ingsw.model.game;

import it.polimi.ingsw.exceptions.InvalidArgumentException;

import java.io.Serializable;
import java.util.*;

/**
 * The class represents the Game's FaithTrack. It includes all the attributes that FaithTrack has.
 */

public class FaithTrack {
    private transient Iterator<VaticanReportSection> vaticanReportSectionIterator;
    private final int length;
    private static LinkedHashMap< Integer , Integer > trackVictoryPoints;
    private VaticanReportSection currentSection;
    private List<VaticanReportSection> vaticanReportSectionList;

    /**
     * Constructs the FaithTrack and automatically assigns to currentSection the first next element of the vaticanReportSectionIterator
     * @throws InvalidArgumentException
     */
    public FaithTrack() {
        length = 24;
        vaticanReportSectionList= new ArrayList<>();
        try {
            vaticanReportSectionList.add(new VaticanReportSection(5, 8, 2));
            vaticanReportSectionList.add(new VaticanReportSection(12, 16, 3));
            vaticanReportSectionList.add(new VaticanReportSection(19, 24, 4));
        } catch (InvalidArgumentException e){}
        vaticanReportSectionIterator=vaticanReportSectionList.iterator();
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
    public VaticanReportSection getCurrentSection(boolean goNext) {
        VaticanReportSection returnedSection=currentSection;
        if(goNext && vaticanReportSectionIterator.hasNext()){
            currentSection= vaticanReportSectionIterator.next();
        }
        return returnedSection;
    }

    public int getLength() {
        return length;
    }

    /**
     *
     * @param playerPosition number of  box on the {@link FaithTrack} on which the player is.
     * @return sum of all victory points a player is eligible to achieve if he's in {@link FaithTrack}'box playerPosition
     */

    public static int getVictoryPoints(int playerPosition) {
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

    public int getVaticanReportSectionNumberByStart(int start){
        assert (start == 5 || start == 12 || start == 19);
        if (start == 5)
            return 0;
        if (start == 12)
            return 1;
        return 2;
    }

    public void setVaticanReportSectionIterator(VaticanReportSection currentSection) {
        while (this.currentSection.getStart() != currentSection.getStart()){
            this.currentSection = vaticanReportSectionIterator.next();
        }
    }
}
