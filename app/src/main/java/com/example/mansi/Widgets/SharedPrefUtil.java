package com.example.mansi.Widgets;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mansi.Entities.CategoryEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefUtil{

    public static void saveCategory(Context context, ArrayList<CategoryEntity> category) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(AppConstants.Category_PREF, Context.MODE_PRIVATE).edit();
        ArrayList<CategoryEntity> categoryEntities = category;
        Gson gson = new Gson();
        String jsonText = gson.toJson(categoryEntities);
        prefs.putString(AppConstants.Category_PREF_KEY, jsonText);
        prefs.apply();
    }


    public static ArrayList<CategoryEntity> loadCategories(Context context) {


        SharedPreferences prefs = context.getSharedPreferences(AppConstants.Category_PREF, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(AppConstants.Category_PREF_KEY, "");
        Type type = new TypeToken<ArrayList<CategoryEntity>>() {}.getType();
        return gson.fromJson(json, type);


    }
    private static class AppConstants {
        public static final String Category_PREF = "category_preference";
        public static final String Category_PREF_KEY = "category_key";

    }
}