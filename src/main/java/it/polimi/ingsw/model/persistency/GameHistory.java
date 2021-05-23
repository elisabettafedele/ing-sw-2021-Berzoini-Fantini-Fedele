package it.polimi.ingsw.model.persistency;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.jsonParsers.JsonAdapter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class GameHistory {

    public static boolean saveGames;


    public synchronized static JsonObject retrieveGameFromControllerId(int id) {
        JsonObject jsonObjectOfOldMatch = null;
        try (JsonReader jsonReader = new JsonReader(new FileReader("backupOfGames.json"))) {
            JsonArray jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    if (id == currentJsonElement.getAsJsonObject().get("controllerId").getAsInt()) {
                        jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                    }
                }
            }
        } catch (IOException e) { e.printStackTrace();}
        return jsonObjectOfOldMatch;
    }

    public synchronized static void saveGame(PersistentController controller){
        if (!saveGames)
            return;
        JsonArray jsonArray = null;
        try (JsonReader jsonReader = new JsonReader(new FileReader("backupOfGames.json"))) {
            jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            JsonObject jsonObjectOfOldMatch = null;

            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    int oldMatchId = currentJsonElement.getAsJsonObject().get("controllerID").getAsInt();
                    if (oldMatchId == controller.getControllerID()) jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                }
                if (jsonObjectOfOldMatch != null) jsonArray.remove(jsonObjectOfOldMatch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonArray == null) jsonArray = new JsonArray();

        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("controllerID", controller.getControllerID());
            jsonObject.add("game", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getGame())));
            jsonObject.addProperty("gamePhase", controller.getGamePhase());
            jsonObject.addProperty("nextTurnIndex", controller.getLastPlayer());
            jsonArray.add(jsonObject);
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
