package it.polimi.ingsw.model.persistency;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class GameHistory {

    public synchronized static void saveGame(PersistentGame game){
        /*JsonArray jsonArray = null;
        try (JsonReader jsonReader = new JsonReader(new FileReader("backupOfMatches.json"))) {
            jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            JsonObject jsonObjectOfOldMatch = null;

            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    Integer oldMatchId = JsonAdapter.getGsonBuilder().fromJson(currentJsonElement.getAsJsonObject().get("matchId"), new TypeToken<Integer>() {
                    }.getType());
                    if (oldMatchId == match.getMatchID()) jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                }
                if (jsonObjectOfOldMatch != null) jsonArray.remove(jsonObjectOfOldMatch);
            }
        } catch (IOException e) {
        }

        if (jsonArray == null) jsonArray = new JsonArray();

        try (Writer writer = new FileWriter("backupOfMatches.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("matchId", game.getMatchID());
            jsonObject.add("players", JsonParser.parseString(JsonAdapter.toJsonClass(match.getPlayers())));
            jsonObject.add("island", JsonParser.parseString(JsonAdapter.toJsonClass(match.getIsland())));
            jsonObject.add("matchProperties", JsonParser.parseString(JsonAdapter.toJsonClass(match.getMatchProperties())));
            jsonObject.add("currentPlayer", JsonParser.parseString(JsonAdapter.toJsonClass(match.getCurrentPlayer())));
            jsonObject.add("location", JsonParser.parseString(JsonAdapter.toJsonClass(match.getLocation())));
            jsonArray.add(jsonObject);
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

    }
}
