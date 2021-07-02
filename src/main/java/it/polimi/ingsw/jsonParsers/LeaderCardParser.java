package it.polimi.ingsw.jsonParsers;

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
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.depot.LeaderDepot;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to parse leader cards from a json file
 */
public class LeaderCardParser {

    private LeaderCardParser(){
        throw new IllegalStateException("Utility class");
    }

    /**
     * Static method used to parse all the {@link LeaderCard} from a .json file
     * @return a {@link List<LeaderCard>} containing all the leader cards of the game
     * @throws JsonFileNotFoundException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws InvalidArgumentException
     */
    public static List<LeaderCard> parseCards()  {
        List<LeaderCard> cards = new ArrayList<LeaderCard>();
        String path = "json/LeaderCards.json";
        InputStream in = LeaderCardParser.class.getClassLoader().getResourceAsStream(path);
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray jsonCards = jsonObject.getAsJsonArray("leaderCards");
        for (JsonElement cardElem : jsonCards){
            JsonObject card = cardElem.getAsJsonObject();

            String effectTypeString = card.get("effectType").getAsString();
            EffectType effectType = EffectType.valueOf(effectTypeString);
            Effect effect = null;
            try {
                effect = LeaderCardParser.effectParser(effectType, card);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }

            String imageFront = card.get("imageFront").getAsString();
            String imageBack = card.get("imageBack").getAsString();

            JsonArray costArray = card.get("cost").getAsJsonArray();
            Value cost = null;
            try {
                cost = ValueParser.parseValue(costArray);
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }

            int victoryPoints = card.get("victoryPoints").getAsInt();

            try {
                cards.add(new LeaderCard(victoryPoints, cost, effect, imageFront, imageBack));
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }

        }
        int id = 49;
        for (LeaderCard card : cards){
            card.setID(id++);
        }



    return cards;
    }

    /**
     * Static method used to parse an effect from a JsonObject representing a specific card
     * @param effectType {@link EffectType} of the card
     * @param card {@link JsonObject} related to the card
     * @return the {@link Effect} of the {@link LeaderCard}
     * @throws InvalidArgumentException
     */
    private static Effect effectParser(EffectType effectType, JsonObject card) throws InvalidArgumentException {
        if (effectType.equals(EffectType.DISCOUNT))
            return new Effect(Resource.valueOf(card.get("resource").getAsString()));
        if (effectType.equals(EffectType.EXTRA_DEPOT))
            return new Effect(new ExtraDepot(new LeaderDepot(Resource.valueOf(card.get("resource").getAsString()))));
        if (effectType.equals(EffectType.WHITE_MARBLE))
            return new Effect(Marble.valueOf(card.get("marble").getAsString()));
        if (effectType.equals(EffectType.PRODUCTION))
            return new Effect(new Production(ValueParser.parseValue(card.get("productionCost").getAsJsonArray()), ValueParser.parseValue(card.get("productionOutput").getAsJsonArray())));
        throw new InvalidArgumentException ();
    }

}
