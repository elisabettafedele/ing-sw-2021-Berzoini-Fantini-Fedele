package it.polimi.ingsw.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.enumerations.EffectType;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.enumerations.Resource;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.exceptions.JsonFileNotFoundException;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.ExtraDepot;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.depot.LeaderDepot;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class LeaderCardParser {
    public LeaderCardParser(){
        throw new IllegalStateException("Utility class");
    }

    public static List<LeaderCard> parseCards() throws JsonFileNotFoundException, FileNotFoundException, UnsupportedEncodingException {
        List<LeaderCard> cards = new ArrayList<LeaderCard>();
        String path = "json/LeaderCards/LeaderCards.json";
        InputStream in = LeaderCardParser.class.getClassLoader().getResourceAsStream(path);
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray jsonCards = jsonObject.getAsJsonArray("leaderCards");

        for (JsonElement cardElem : jsonCards){
            JsonObject card = cardElem.getAsJsonObject();
            String effectTypeString = card.get("effectType").getAsString();
            EffectType effectType = EffectType.valueOf(effectTypeString);
            String imageFront = card.get("imageFront").getAsString();
            String imageBack = card.get("imageBack").getAsString();
            JsonArray costArray = card.get("cost").getAsJsonArray();
            int victoryPoints = card.get("victoryPoints").getAsInt();

        }

    return cards;
    }

    private Effect effectParser(EffectType effectType, JsonObject card) throws InvalidArgumentException {
        if (effectType.equals(effectType.DISCOUNT))
            return new Effect(Resource.valueOf(card.get("resource").getAsString()));
        if (effectType.equals(effectType.EXTRA_DEPOT))
            return new Effect(new ExtraDepot(new LeaderDepot(Resource.valueOf(card.get("resource").getAsString()))));
        if (effectType.equals(effectType.WHITE_MARBLE))
            return new Effect(Marble.valueOf(card.get("resource").getAsString()));
        //if (effectType.equals(effectType.PRODUCTION))
            //return new Effect(new Production(valueParser(card.get("productionCost").getAsJsonArray()), valueParser(card.get("productionOutput").getAsJsonArray())));
        throw new InvalidArgumentException ();
    }
/*
    private Value valueParser(JsonArray jsonValues){
        List<Flag> flagValue = new ArrayList<Flag>();
        Map<Resource, Integer> resourceValue = new HashMap<Resource,Integer>();
        int faithValue=0;
        for (JsonElement jsonElement : jsonValues){
            JsonArray value= jsonElement.getAsJsonArray();
            if (ValueType.valueOf(value.get(0).getAsString()).equals(ValueType.RESOURCE)){
                resourceValue.put(Resource.valueOf(value.get(1).getAsString()), value.get(2).getAsInt());
            }
            else if (ValueType.valueOf(value.get(0).getAsString()).equals(ValueType.FAITH)){
                faithValue = value.get(1).getAsInt();
            }
            else if (ValueType.valueOf(value.get(0).getAsString()).equals(ValueType.FLAG)){
                resourceValue.put(Resource.valueOf(value.get(1).getAsString()), value.get(2).getAsInt());
            }

            /*
            BISOGNA RIFARE FLAG VALUE
            else if (ValueType.valueOf(values.get(0).getAsString()).equals(ValueType.FLAG)){
                flagValue.add(new Flag)
            }*/





       // }


    //}
}
