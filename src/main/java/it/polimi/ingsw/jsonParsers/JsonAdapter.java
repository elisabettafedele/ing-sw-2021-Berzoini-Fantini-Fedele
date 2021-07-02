package it.polimi.ingsw.jsonParsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//TODO: JavaDoc
public class JsonAdapter {
    private static final Gson gsonBuilder = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();

    public static Gson getGsonBuilder(){
        return gsonBuilder;
    }

    public static String toJsonClass(Object object) {
        return getGsonBuilder().toJson(object);
    }

}
