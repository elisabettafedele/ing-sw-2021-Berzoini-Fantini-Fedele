package it.polimi.ingsw.client.cli.graphical;

import it.polimi.ingsw.client.MatchData;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.Flag;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Value;
import it.polimi.ingsw.utility.LeaderCardParser;
import javafx.css.Match;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class GraphicalLeaderCardTest {

    GraphicalLeaderCard glc;

    @Before
    public void setUp() throws Exception {
        MatchData.getInstance().setAllLeaderCards(getLightLeaderCards(LeaderCardParser.parseCards()));
        MatchData.getInstance().setThisClient("Raffa");
        Random rand = new Random();
        int ID_1 =  rand.nextInt(16) + 49;
        int ID_2 = rand.nextInt(16) + 49;
        MatchData.getInstance().addChosenLeaderCard(ID_1, true);
        MatchData.getInstance().addChosenLeaderCard(ID_2, false);
        // MatchData.getInstance().addLightClient("Pluto");




        LightLeaderCard llc = MatchData.getInstance().getLeaderCardByID(ID_2);
        glc = new GraphicalLeaderCard(llc, "Raffa");
    }

    @Test
    public void displayCard() {
        glc.drawCard();
        glc.displayCard();
    }

    static List<LightLeaderCard> getLightLeaderCards(List<LeaderCard> cards){
        List<LightLeaderCard> lightCards = new ArrayList<>();
        for(LeaderCard lc : cards){
            List<String> stringCost = leaderCardResourceAndFlagCostConversion(lc.getCost());

            String costType;
            try{
                lc.getCost().getResourceValue();
                costType = "RESOURCE";
            } catch (ValueNotPresentException e) {
                //e.printStackTrace();
                costType = "FLAG";
            }

            List<String> effectDescription = new ArrayList<>();
            List<String> effectDescription2 = new ArrayList<>();
            String effectType = lc.getEffect().getEffectType().toString();
            if(lc.getEffect().getEffectType() != EffectType.PRODUCTION)
                effectDescription = leaderCardsEffectToStringParse(lc.getEffect());
            else{
                try {
                    effectDescription = developmentCardResourceCostConversion(lc.getEffect().getProductionEffect().getProductionPower().get(0));
                    effectDescription2 = developmentCardResourceCostConversion(lc.getEffect().getProductionEffect().getProductionPower().get(1));
                } catch (DifferentEffectTypeException e) {
                    e.printStackTrace();
                }
                try {
                    int faithPoints = lc.getEffect().getProductionEffect().getProductionPower().get(1).getFaithValue();
                    effectDescription2.add(String.valueOf(faithPoints));
                    effectDescription2.add("FaithPoints");
                } catch (ValueNotPresentException | DifferentEffectTypeException e) {
                    //skip
                }
            }

            lightCards.add(new LightLeaderCard(stringCost, lc.getVictoryPoints(), lc.getUsed(), lc.getID(),
                    effectType, effectDescription, effectDescription2, costType));
        }


        return lightCards;
    }

    static List<String> leaderCardsEffectToStringParse(Effect effect) {
        List<String> description = new ArrayList<>();
        if(effect.getEffectType() == EffectType.WHITE_MARBLE){
            try {
                description.add(effect.getWhiteMarbleEffectResource().toString());
            } catch (DifferentEffectTypeException e) {
                e.printStackTrace();
            }
        }else if(effect.getEffectType() == EffectType.DISCOUNT){
            try {
                description.add(effect.getDiscountEffect().toString());
            } catch (DifferentEffectTypeException e) {
                e.printStackTrace();
            }
        }else if(effect.getEffectType() == EffectType.EXTRA_DEPOT) {
            try {
                description.add(effect.getExtraDepotEffect().getLeaderDepot().getResourceType().toString());
            } catch (DifferentEffectTypeException e) {
                e.printStackTrace();
            }
        }
        return description;
    }

    static List<String> leaderCardResourceAndFlagCostConversion(Value cost) {
        List<String> resourceCost = developmentCardResourceCostConversion(cost);
        if (resourceCost.size()>0)
            return resourceCost;

        Map<Flag, Integer> flagActivationCost;

        try {
            flagActivationCost = cost.getFlagValue();
            List<String> flagCost = new ArrayList<>();
            for (Map.Entry<Flag, Integer> entry : flagActivationCost.entrySet()){
                flagCost.add(entry.getValue().toString());
                flagCost.add(entry.getKey().getFlagColor().toString());
                flagCost.add(entry.getKey().getFlagLevel().toString());
            }
            return flagCost;
        } catch (ValueNotPresentException e) {
            return new ArrayList<>();
        }
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