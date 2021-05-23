package it.polimi.ingsw.jsonParsers;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import it.polimi.ingsw.model.persistency.PersistentGame;

public class JsonAdapter {
    private static final Gson gsonBuilder = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();

    public static Gson getGsonBuilder(){
        return gsonBuilder;
    }

    public static String toJsonClass(Object object) {
        return getGsonBuilder().toJson(object);
    }

}
