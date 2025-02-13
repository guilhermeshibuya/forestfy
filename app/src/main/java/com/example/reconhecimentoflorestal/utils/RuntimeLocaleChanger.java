package com.example.reconhecimentoflorestal.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class RuntimeLocaleChanger {
    private static final String PREFS_NAME = "Settings";
    private static final String KEY_LANGUAGE = "My_Lang";

    private static String getSavedLanguage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_LANGUAGE, "en");
    }

    private static void updateSavedLanguage(Context context, String languageCode) {
        if (languageCode == null) return;
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();
    }

    public static Context wrapContext(Context context) {
        return wrapContext(context, null);
    }

    public static Context wrapContext(Context context, String languageCode) {
        if (languageCode != null) {
            updateSavedLanguage(context, languageCode);
        } else {
            languageCode = getSavedLanguage(context);
        }

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }
}
