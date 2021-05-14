package it.polimi.ingsw;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.client.cli.graphical.GraphicalDevelopmentCardGrid;
import it.polimi.ingsw.client.cli.graphical.GraphicalLogo;
import it.polimi.ingsw.client.cli.graphical.SubscriptNumbers;
import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.Value;
import it.polimi.ingsw.model.game.DevelopmentCardGrid;
import it.polimi.ingsw.utility.DevelopmentCardParser;

import java.io.UnsupportedEncodingException;
import java.util.*;


/**
 * Hello world!
 *
 */
public class App 
{

    public static void main(String[] args ) throws InvalidArgumentException, DifferentEffectTypeException, UnsupportedEncodingException {

        MatchData.getInstance().setAllDevelopmentCards(getLightDevelopmentCards(DevelopmentCardParser.parseCards()));

        GraphicalDevelopmentCardGrid gdc = new GraphicalDevelopmentCardGrid();

        DevelopmentCardGrid devGrid = new DevelopmentCardGrid();

        List<DevelopmentCard> list = devGrid.getAvailableCards();

        List<Integer> IDs = new ArrayList<>();

        for(DevelopmentCard dc : list){
            IDs.add(dc.getID());
        }

        gdc.setCardsToDisplay(IDs);
        gdc.drawDevelopmentCardGrid();



    }

    static List<LightDevelopmentCard> getLightDevelopmentCards(List<DevelopmentCard> cards){
        List<LightDevelopmentCard> lightCards = new ArrayList<>();
        for(DevelopmentCard dc : cards){

            List<String> stringCost = developmentCardResourceCostConversion(dc.getCost());
            List<String> stringProductionCost = developmentCardResourceCostConversion(dc.getProduction().getProductionPower().get(0));
            List<String> stringProductionOutput = developmentCardResourceCostConversion(dc.getProduction().getProductionPower().get(1));

            try {
                int faithPoints = dc.getProduction().getProductionPower().get(1).getFaithValue();
                stringProductionOutput.add(String.valueOf(faithPoints));
                stringProductionOutput.add("FaithPoints");
            } catch (ValueNotPresentException e) {
                //skip
            }


            lightCards.add(new LightDevelopmentCard(stringCost, dc.getVictoryPoints(), dc.getUsed(), dc.getID(),
                    dc.getFlag().getFlagColor().toString(), dc.getFlag().getFlagLevel().toString(), stringProductionCost,
                    stringProductionOutput));
        }
        return lightCards;
    }

    static List<String> developmentCardResourceCostConversion(Value cost){
        Map<Resource, Integer> resourceCost;
        try {
            resourceCost = cost.getResourceValue();
        } catch (ValueNotPresentException e) {
            return new ArrayList<>();
        }
        List<String> stringCost = new ArrayList<>();

        for (Map.Entry<Resource, Integer> entry : resourceCost.entrySet()){
            stringCost.add(entry.getValue().toString());
            stringCost.add(entry.getKey().toString());
        }
        return stringCost;
    }
}

