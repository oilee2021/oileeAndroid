package com.oileemobile.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConverterUtils {

    public static <T> T getObjectFromResponse(Object o, Class<T> convertToClass) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(o), convertToClass);
    }

    public static <T> List<T> getListFromResponse(Object vessel, Class<T> convertToClass) {
        if (vessel != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<T>>() {}.getType();
            List<Object> list = gson.fromJson(gson.toJson(vessel), type);

            List<T> returnList = new ArrayList<>();
            for(Object a : list) {
                returnList.add(getObjectFromResponse(a, convertToClass));
            }
            return returnList;
        }

        return null;
    }
}
