package com.example.reconhecimentoflorestal.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.reconhecimentoflorestal.R;
import com.example.reconhecimentoflorestal.utils.RuntimeLocaleChanger;
import com.google.android.material.button.MaterialButton;


public class LanguagesFragment extends Fragment {
    private MaterialButton btnPortuguese, btnEnglish, btnSpanish;
    private TextView settingsTitle;

    public LanguagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.languages_fragment, container, false);

        btnPortuguese = view.findViewById(R.id.languages_btn_portuguese);
        btnEnglish = view.findViewById(R.id.languages_btn_english);
        btnSpanish = view.findViewById(R.id.languages_btn_spanish);
        settingsTitle = view.findViewById(R.id.settings_title);

        highlightActiveLanguage();

        btnPortuguese.setOnClickListener(v -> {
            changeLanguage("pt");
        });
        btnEnglish.setOnClickListener(v -> {
            changeLanguage("en");
        });
        btnSpanish.setOnClickListener(v -> {
            changeLanguage("es");
        });

        settingsTitle.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }

    private void changeLanguage(String languageCode) {
        RuntimeLocaleChanger.wrapContext(requireContext(), languageCode);
        requireActivity().recreate();
    }

    private void highlightActiveLanguage() {
        Context context = requireContext();
        SharedPreferences prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String savedLanguage = prefs.getString("My_Lang", "en");

        int defaultBgColor = getResources().getColor(R.color.transparent);
        int defaultTextColor = getResources().getColor(R.color.c10);
        int defaultStrokeColor = getResources().getColor(R.color.c3);
        int activeBgColor = getResources().getColor(R.color.p3);
        int activeTextColor = getResources().getColor(R.color.p8);

        MaterialButton[] buttons = {btnPortuguese, btnEnglish, btnSpanish};
        for (MaterialButton btn : buttons) {
            btn.setBackgroundColor(defaultBgColor);
            btn.setStrokeColor(ColorStateList.valueOf(defaultStrokeColor));
            btn.setTextColor(defaultTextColor);
        }

       switch (savedLanguage) {
           case "pt":
               setButtonActive(btnPortuguese, activeBgColor, activeBgColor, activeTextColor);
               break;
           case "en":
               setButtonActive(btnEnglish, activeBgColor, activeBgColor, activeTextColor);
               break;
           case "es":
               setButtonActive(btnSpanish, activeBgColor, activeBgColor, activeTextColor);
               break;
       }
    }

    private void setButtonActive(MaterialButton button, int bgColor, int strokeColor, int textColor) {
        button.setBackgroundColor(bgColor);
        button.setStrokeColor(ColorStateList.valueOf(strokeColor));
        button.setTextColor(textColor);
    }
}