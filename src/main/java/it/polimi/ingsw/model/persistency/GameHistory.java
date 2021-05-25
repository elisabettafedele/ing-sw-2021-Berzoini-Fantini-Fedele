package it.polimi.ingsw.model.persistency;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.controller.game_phases.PlayPhase;
import it.polimi.ingsw.controller.game_phases.SetUpPhase;
import it.polimi.ingsw.enumerations.GameMode;
import it.polimi.ingsw.enumerations.Marble;
import it.polimi.ingsw.exceptions.InvalidArgumentException;
import it.polimi.ingsw.jsonParsers.JsonAdapter;
import it.polimi.ingsw.model.game.VaticanReportSection;

import java.io.*;
import java.util.*;

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
                    if (id == currentJsonElement.getAsJsonObject().get("controllerID").getAsInt()) {
                        jsonObjectOfOldMatch = currentJsonElement.getAsJsonObject();
                    }
                }
            }
        } catch (IOException e) { return null;}
        return jsonObjectOfOldMatch;
    }

    public synchronized static PersistentGame retrieveGame(int controllerID){
        JsonObject controller = retrieveGameFromControllerId(controllerID);
        return new Gson().fromJson(controller.get("game").getAsJsonObject(), PersistentGame.class);
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
            jsonObject.addProperty("lastPlayer", controller.getLastPlayer());
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

    public static synchronized void saveGame(PersistentControllerPlayPhaseSingle controller){
        if (!saveGames)
            return;
        JsonArray jsonArray = getJsonArray(controller.getControllerID());
        try (Writer writer = new FileWriter("backupOfGames.json", false)) {
            Gson gson = JsonAdapter.getGsonBuilder();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("controllerID", controller.getControllerID());
            jsonObject.addProperty("gamePhase", PLAY_PHASE);
            jsonObject.add("game", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getGame())));
            jsonObject.addProperty("lastPlayer", controller.getLastPlayer());
            jsonObject.add("tokens", JsonParser.parseString(JsonAdapter.toJsonClass(controller.getTokens())));
            jsonObject.addProperty("blackCrossPosition", controller.getBlackCrossPosition());
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
            jsonArray = null;
        }

        if (jsonArray == null) jsonArray = new JsonArray();
        return jsonArray;
    }

    private static PersistentGame getPersistentGame(JsonObject gameObject){
        JsonArray gameArray = gameObject.getAsJsonArray();
        PersistentGame game = new PersistentGame();
        for (JsonElement elementOfGameArray : gameArray){
            JsonObject objectOfGameArray = elementOfGameArray.getAsJsonObject();
            game.setGameMode(GameMode.valueOf(objectOfGameArray.get("gameMode").getAsString()));
            game.setCurrentSection(getVaticanReportSection(objectOfGameArray.get("currentSection").getAsJsonArray()));
            game.setMarketTray(getMarketTray(objectOfGameArray.get("marketTray").getAsJsonArray()));
            game.setSlideMarble(Marble.valueOf(objectOfGameArray.get("slideMarble").getAsString()));
            game.setDevelopmentCardGrid(getDevelopmentCardGrid(objectOfGameArray.get("developmentCardGrid").getAsJsonArray()));
            //game.setPlayers();

        }
        return game;
    }

    private static VaticanReportSection getVaticanReportSection(JsonArray vaticanReportSectionArray){
        int start = vaticanReportSectionArray.get(0).getAsInt();
        int end = vaticanReportSectionArray.get(1).getAsInt();
        int popesFavorPoints = vaticanReportSectionArray.get(2).getAsInt();
        VaticanReportSection vaticanReportSection = null;
        try {
            vaticanReportSection = new VaticanReportSection(start, end, popesFavorPoints);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
        }
        return vaticanReportSection;
    }

    private static Marble[][] getMarketTray(JsonArray marketTrayArray){
        Marble[][] marketTray = new Marble[3][4];
        for (int i = 0; i < marketTray.length; i++){
            JsonArray marketRowArray = marketTrayArray.get(i).getAsJsonArray();
            for (int j = 0; j < marketTray[i].length; j++){
                marketTray[i][j] = Marble.valueOf(marketRowArray.get(j).getAsString());
            }
        }
        return marketTray;
    }

    private static Stack<Integer>[][] getDevelopmentCardGrid(JsonArray developmentCardGridArray){
        Stack<Integer>[][] developmentCardGrid = new Stack[3][4];
        for (int i = 0; i < developmentCardGrid.length; i++){
            JsonArray row = developmentCardGridArray.get(i).getAsJsonArray();
            for (int j = 0; j < developmentCardGrid[i].length; j++){
                JsonArray cell = row.get(j).getAsJsonArray();
                developmentCardGrid[i][j] = new Stack<>();
                for (JsonElement element : cell)
                    developmentCardGrid[i][j].push(element.getAsInt());
            }
        }
        return developmentCardGrid;
    }

}
