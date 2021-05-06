package it.polimi.ingsw.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.enumerations.FlagColor;
import it.polimi.ingsw.enumerations.Level;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.model.cards.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DevelopmentCardParser {

    private DevelopmentCardParser(){
        throw new IllegalStateException("Utility class");
    }

    /**
     * Static method used to parse all the {@link DevelopmentCard} from a .json file
     * @return a {@link List<DevelopmentCard>} containing all the developments cards of the game
     * @throws UnsupportedEncodingException
     * @throws InvalidArgumentException
     */
    public static List<DevelopmentCard> parseCards() {
        List<DevelopmentCard> cards = new ArrayList<>();
        String path = "json/DevelopmentCards.json";
        InputStream in = LeaderCardParser.class.getClassLoader().getResourceAsStream(path);
        JsonReader reader = null;
        try {
            reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray jsonCards = jsonObject.getAsJsonArray("developmentCards");
            for (JsonElement cardElem : jsonCards) {
                JsonObject card = cardElem.getAsJsonObject();

                String imageFront = card.get("imageFront").getAsString();
                String imageBack = card.get("imageBack").getAsString();

                JsonArray costArray = card.get("cost").getAsJsonArray();
                Value cost = ValueParser.parseValue(costArray);
                Flag flag = new Flag(FlagColor.valueOf(card.get("flagColor").getAsString()), Level.valueOf(card.get("flagLevel").getAsString()));
                Production production = new Production(ValueParser.parseValue(card.get("productionCost").getAsJsonArray()), ValueParser.parseValue(card.get("productionOutput").getAsJsonArray()));
                int victoryPoints = card.get("victoryPoints").getAsInt();

                cards.add(new DevelopmentCard(victoryPoints, cost, flag, production, imageFront, imageBack));
            }
            int id = 1;
            for (DevelopmentCard card : cards) {
                card.setID(id);
                id++;
            }
        } catch (InvalidArgumentException e){
            e.printStackTrace();
        }
        return cards;
    }
}
