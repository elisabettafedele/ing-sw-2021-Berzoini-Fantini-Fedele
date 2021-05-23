package it.polimi.ingsw.jsonParsers;

import it.polimi.ingsw.common.LightDevelopmentCard;
import it.polimi.ingsw.common.LightLeaderCard;
import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.DifferentEffectTypeException;
import it.polimi.ingsw.exceptions.ValueNotPresentException;
import it.polimi.ingsw.model.cards.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LightCardsParser {

    public static List<LightLeaderCard> getLightLeaderCards(List<LeaderCard> cards){
        List<LightLeaderCard> lightCards = new ArrayList<>();
        for(LeaderCard lc : cards){

            String costType;
            try{
                lc.getCost().getResourceValue();
                costType = "RESOURCE";
            } catch (ValueNotPresentException e) {
                //e.printStackTrace();
                costType = "FLAG";
            }
            List<String> stringCost = leaderCardResourceAndFlagCostConversion(lc.getCost());

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

    private static List<String> leaderCardsEffectToStringParse(Effect effect) {
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

    private static List<String> leaderCardResourceAndFlagCostConversion(Value cost) {
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

    public static List<LightDevelopmentCard> getLightDevelopmentCards(List<DevelopmentCard> cards){
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

    private static List<String> developmentCardResourceCostConversion(Value cost){
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
