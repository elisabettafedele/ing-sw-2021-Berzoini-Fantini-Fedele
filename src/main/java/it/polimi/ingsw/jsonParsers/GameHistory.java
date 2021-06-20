package it.polimi.ingsw.jsonParsers;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.jsonParsers.JsonAdapter;
import it.polimi.ingsw.model.persistency.PersistentControllerPlayPhase;
import it.polimi.ingsw.model.persistency.PersistentControllerPlayPhaseSingle;
import it.polimi.ingsw.model.persistency.PersistentControllerSetUpPhase;

import java.io.*;
import java.util.*;

public class GameHistory {
    /**
     * Utility class used to handle the saving of a {@link it.polimi.ingsw.model.game.Game} in the server and to retrieve and old one
     */

    public static final String PLAY_PHASE = "PLAY_PHASE";
    public static final String SETUP_PHASE = "SETUP_PHASE";

    /**
     * Method to retrieve an old game knowing its controller ID (if any matches)
     * @param id the controller id. It is the hash code of the concatenation of the string of players' names ordered alphabetically.
     * @return a JsonObject corresponding to the game wanted if any exists, null if no game with that id is present in the json file
     */
    public synchronized static JsonObject retrieveGameFromControllerId(int id) {
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

    /**
     * Method to retrieve the {@link PersistentControllerSetUpPhase} of a specific {@link it.polimi.ingsw.model.game.Game} identified by the controller ID
     * @param controllerID the ID of the {@link it.polimi.ingsw.controller.Controller} of the {@link it.polimi.ingsw.model.game.Game} to retrieve
     * @return the {@link PersistentControllerSetUpPhase} related to the {@param controllerID}
     */
    public synchronized static PersistentControllerSetUpPhase retrieveSetUpController(int controllerID){
        JsonObject persistentControllerSetUpPhase = retrieveGameFromControllerId(controllerID);
        return new Gson().fromJson(persistentControllerSetUpPhase, PersistentControllerSetUpPhase.class);
    }

    /**
     * Method to retrieve the {@link PersistentControllerPlayPhase} of a specific Multiplayer {@link it.polimi.ingsw.model.game.Game} identified by the controller ID
     * @param controllerID the ID of the {@link it.polimi.ingsw.controller.Controller} of the {@link it.polimi.ingsw.model.game.Game} to retrieve
     * @return the {@link PersistentControllerPlayPhase} related to the {@param controllerID}
     */
    public synchronized static PersistentControllerPlayPhase retrievePlayController(int controllerID){
        JsonObject persistentControllerPlayPhase = retrieveGameFromControllerId(controllerID);
        return new Gson().fromJson(persistentControllerPlayPhase, PersistentControllerPlayPhase.class);

    }

    /**
     * Method to retrieve the {@link PersistentControllerPlayPhaseSingle} of a specific single player {@link it.polimi.ingsw.model.game.Game} identified by the controller ID
     * @param controllerID the ID of the {@link it.polimi.ingsw.controller.Controller} of the {@link it.polimi.ingsw.model.game.Game} to retrieve
     * @return the {@link PersistentControllerPlayPhaseSingle} related to the {@param controllerID}
     */
    public synchronized static PersistentControllerPlayPhaseSingle retrievePlayControllerSingle(int controllerID) {
        JsonObject persistentControllerPlayPhaseSingle = retrieveGameFromControllerId(controllerID);
        return new Gson().fromJson(persistentControllerPlayPhaseSingle, PersistentControllerPlayPhaseSingle.class);
    }

    /**
     * Method to check whether the phase of a specific {@link it.polimi.ingsw.model.game.Game} identified by its {@param controllerID} was executing the set up phase
     * @param controlledID the ID of the {@link it.polimi.ingsw.controller.Controller} of the {@link it.polimi.ingsw.model.game.Game} to retrieve
     * @return true only if the controller was in set up phase
     */
    public static boolean isSetUpPhase(int controlledID){
        return retrieveGameFromControllerId(controlledID).get("gamePhase").getAsString().equals(SETUP_PHASE);
    }

    /**
     * Method to save the set up phase of a specific {@link it.polimi.ingsw.model.game.Game} identified by its {@param controllerID}
     * @param controller the {@link PersistentControllerSetUpPhase} of the game to be saved
     */
    public static synchronized void saveSetupPhase(PersistentControllerSetUpPhase controller){
        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            gson.toJson(createJsonArraySetUp(controller), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to save in the json file a multiplayer {@link it.polimi.ingsw.model.game.Game} executing the play phase
     * @param controller the {@link PersistentControllerPlayPhase} of the game to be saved
     */
    public synchronized static void saveGame(PersistentControllerPlayPhase controller){
        Gson gson = JsonAdapter.getGsonBuilder();

        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            gson.toJson(createJsonArrayMultiplayer(controller), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to save in the json file a single player {@link it.polimi.ingsw.model.game.Game} executing the play phase
     * @param controller the {@link PersistentControllerPlayPhaseSingle} of the game to be saved
     */
    public static synchronized void saveGame(PersistentControllerPlayPhaseSingle controller){
        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            gson.toJson(createJsonArraySinglePlayer(controller), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to retrieve from memory the {@link JsonArray} related to a specific {@link it.polimi.ingsw.model.game.Game} identified by its controllerID
     * @param controllerID the ID of the {@link it.polimi.ingsw.controller.Controller} of the {@link it.polimi.ingsw.model.game.Game} to retrieve
     * @return a {@link JsonArray} with the information related to the {@param controllerID}
     */
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

    /**
     * Method to create a {@link JsonArray} related to a {@link PersistentControllerPlayPhase}
     * @param controller the {@link PersistentControllerPlayPhase} of the game to be saved
     * @return a {@link JsonArray} with the information related to the multiplayer {@link it.polimi.ingsw.model.game.Game}
     */
    public static synchronized JsonArray createJsonArrayMultiplayer(PersistentControllerPlayPhase controller){
        JsonArray jsonArray = getJsonArray(controller.getControllerID());
        JsonObject jsonObject = getBasicJsonObject(controller.getControllerID(), PLAY_PHASE);
        jsonObject.add("game", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getGame())));
        jsonObject.addProperty("lastPlayer", controller.getLastPlayer());
        jsonArray.add(jsonObject);
        return jsonArray;
    }

    /**
     * Method to create a {@link JsonArray} related to a {@link PersistentControllerPlayPhaseSingle}
     * @param controller the {@link PersistentControllerPlayPhaseSingle} of the game to be saved
     * @return a {@link JsonArray} with the information related to the single player {@link it.polimi.ingsw.model.game.Game}
     */
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

    /**
     * Method to create a {@link JsonArray} related to a {@link PersistentControllerSetUpPhase}
     * @param controller the {@link PersistentControllerSetUpPhase} of the game to be saved
     * @return a {@link JsonArray} with the information related to the set up of the {@link it.polimi.ingsw.model.game.Game}
     */
    public static synchronized JsonArray createJsonArraySetUp(PersistentControllerSetUpPhase controller){
        JsonArray jsonArray = getJsonArray(controller.getControllerID());
        JsonObject jsonObject = getBasicJsonObject(controller.getControllerID(), SETUP_PHASE);
        jsonObject.add("game", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getGame())));
        jsonObject.add("resourcesToStore", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getResourcesToStore())));
        jsonArray.add(jsonObject);
        return jsonArray;
    }

    /**
     * Method to create a basic json object, common for setup, single player play phase and multiplayer play phase
     * @param controllerID the ID of the {@link it.polimi.ingsw.controller.Controller} of the {@link it.polimi.ingsw.model.game.Game} to be saved
     * @param gamePhase the game phase of the {@link it.polimi.ingsw.model.game.Game} to be saved
     * @return a {@link JsonObject} containing two properties: controllerID and gamePhase
     */
    private static synchronized JsonObject getBasicJsonObject(int controllerID, String gamePhase){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("controllerID", controllerID);
        jsonObject.addProperty("gamePhase", gamePhase);
        return jsonObject;
    }

    /**
     * Method to remove from the memory the {@link JsonArray} related to a specific controller ID.
     * A game is removed from memory if a game ends correctly or if a player's disconnection cause it to be canceled
     * @param controllerID the ID of the {@link it.polimi.ingsw.controller.Controller} of the game to remove
     */
    public static void removeOldGame(int controllerID){
        JsonArray jsonArray = getJsonArray(controllerID);
        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
