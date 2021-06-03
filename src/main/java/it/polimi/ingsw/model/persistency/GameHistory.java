package it.polimi.ingsw.model.persistency;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.jsonParsers.JsonAdapter;

import java.io.*;
import java.util.*;

public class GameHistory {
    /**
     * Utility class used to handle the saving of a {@link it.polimi.ingsw.model.game.Game} in the server and to retrieve and old one
     */

    public static boolean saveGames;
    public static final String PLAY_PHASE = "PLAY_PHASE";
    public static final String SETUP_PHASE = "SETUP_PHASE";

    /**
     * Method to retrieve an old game knowing its controller ID (if any matches)
     * @param id the controller id. It is the hash code of the concatenation of the string of players' names ordered alphabetically.
     * @return a JsonObject corresponding to the game wanted if any exists, null if no game with that id is present in the json file
     */
    public synchronized static JsonObject retrieveGameFromControllerId(int id) {
        if (!saveGames)
            return null;
        JsonObject jsonObjectOfOldMatch = null;
        try (JsonReader jsonReader = new JsonReader(new FileReader("backupOfGames.json"))) {
            JsonArray jsonArray = new Gson().fromJson(jsonReader, JsonArray.class);
            if (jsonArray != null) {
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext() && jsonObjectOfOldMatch == null) {
                    JsonElement currentJsonElement = iterator.next();
                    if (id == currentJsonElement.getAsJsonObject().get("controllerID").getAsInt()) {
                        jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                    }
                }
            }
        } catch (IOException e) { return jsonObjectOfOldMatch;}
        return jsonObjectOfOldMatch;
    }

    public synchronized static PersistentControllerSetUpPhase retrieveSetUpController(int controllerID){
        JsonObject persistentControllerSetUpPhase = retrieveGameFromControllerId(controllerID);
        return new Gson().fromJson(persistentControllerSetUpPhase, PersistentControllerSetUpPhase.class);
    }


    public synchronized static PersistentControllerPlayPhase retrievePlayController(int controllerID){
        JsonObject persistentControllerPlayPhase = retrieveGameFromControllerId(controllerID);
        return new Gson().fromJson(persistentControllerPlayPhase, PersistentControllerPlayPhase.class);

    }

    public synchronized static PersistentControllerPlayPhaseSingle retrievePlayControllerSingle(int controllerID) {
        JsonObject persistentControllerPlayPhaseSingle = retrieveGameFromControllerId(controllerID);
        return new Gson().fromJson(persistentControllerPlayPhaseSingle, PersistentControllerPlayPhaseSingle.class);
    }

    public static boolean isSetUpPhase(int controlledID){
        return retrieveGameFromControllerId(controlledID).get("gamePhase").getAsString().equals(SETUP_PHASE);
    }

    public static synchronized void saveSetupPhase(PersistentControllerSetUpPhase controller){
        if (!saveGames)
            return;
        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            gson.toJson(createJsonArraySetUp(controller), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void saveGame(PersistentControllerPlayPhase controller){
        if (!saveGames)
            return;
        Gson gson = JsonAdapter.getGsonBuilder();

        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            gson.toJson(createJsonArrayMultiplayer(controller), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void saveGame(PersistentControllerPlayPhaseSingle controller){
        if (!saveGames)
            return;
        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            gson.toJson(createJsonArraySinglePlayer(controller), writer);
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
            jsonArray = null;
        }

        if (jsonArray == null) jsonArray = new JsonArray();
        return jsonArray;
    }

    public static synchronized JsonArray createJsonArrayMultiplayer(PersistentControllerPlayPhase controller){
        JsonArray jsonArray = getJsonArray(controller.getControllerID());
        JsonObject jsonObject = getBasicJsonObject(controller.getControllerID(), PLAY_PHASE);
        jsonObject.add("game", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getGame())));
        jsonObject.addProperty("lastPlayer", controller.getLastPlayer());
        jsonArray.add(jsonObject);
        return jsonArray;
    }

    public static synchronized JsonArray createJsonArraySinglePlayer(PersistentControllerPlayPhaseSingle controller){
        JsonArray jsonArray = getJsonArray(controller.getControllerID());
        JsonObject jsonObject = getBasicJsonObject(controller.getControllerID(), PLAY_PHASE);
        jsonObject.add("game", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getGame())));
        jsonObject.addProperty("lastPlayer", controller.getLastPlayer());
        jsonObject.add("tokens", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getTokens())));
        jsonObject.addProperty("blackCrossPosition", controller.getBlackCrossPosition());
        jsonArray.add(jsonObject);
        return jsonArray;
    }

    public static synchronized JsonArray createJsonArraySetUp(PersistentControllerSetUpPhase controller){
        JsonArray jsonArray = getJsonArray(controller.getControllerID());
        JsonObject jsonObject = getBasicJsonObject(controller.getControllerID(), SETUP_PHASE);
        jsonObject.add("game", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getGame())));
        jsonObject.add("resourcesToStore", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getResourcesToStore())));
        jsonArray.add(jsonObject);
        return jsonArray;
    }

    private static synchronized JsonObject getBasicJsonObject(int controllerID, String gamePhase){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("controllerID", controllerID);
        jsonObject.addProperty("gamePhase", gamePhase);
        return jsonObject;
    }


    public static void removeOldGame(int controllerID){
        if (!saveGames)
            return;
        JsonArray jsonArray = getJsonArray(controllerID);
        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
