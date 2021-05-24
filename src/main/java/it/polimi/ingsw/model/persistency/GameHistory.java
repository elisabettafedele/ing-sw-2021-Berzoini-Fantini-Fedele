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
    public static final String PLAY_PHASE = "PLAY_PHASE";
    public static final String SETUP_PHASE = "SETUP_PHASE";



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




    public synchronized static void saveGame(PersistentControllerPlayPhase controller){
        if (!saveGames)
            return;
        JsonArray jsonArray = getJsonArray(controller.getControllerID());

        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("controllerID", controller.getControllerID());
            jsonObject.addProperty("gamePhase", PLAY_PHASE);
            jsonObject.add("game", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getGame())));
            jsonObject.addProperty("lastTurnNickname", controller.getLastPlayer());
            jsonArray.add(jsonObject);
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void saveSetupPhase(PersistentControllerSetUpPhase controller){
        if (!saveGames)
            return;
        JsonArray jsonArray = getJsonArray(controller.getControllerID());
        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("controllerID", controller.getControllerID());
            jsonObject.addProperty("gamePhase", SETUP_PHASE);
            jsonObject.add("game", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getGame())));
            jsonObject.add("resourcesToStore", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getResourcesToStore())));
            jsonArray.add(jsonObject);
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static JsonArray getJsonArray(int controllerID){
        JsonArray jsonArray = null;
        try (JsonReader jsonReader = new JsonReader(new FileReader("backupOfGames.json"))) {
            jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            JsonObject jsonObjectOfOldMatch = null;

            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    int oldMatchId = currentJsonElement.getAsJsonObject().get("controllerID").getAsInt();
                    if (oldMatchId == controllerID) jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                }
                if (jsonObjectOfOldMatch != null) jsonArray.remove(jsonObjectOfOldMatch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (jsonArray == null) jsonArray = new JsonArray();
        return jsonArray;
    }
}
