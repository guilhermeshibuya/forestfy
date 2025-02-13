package com.example.reconhecimentoflorestal.utils;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Locale;

public class PercentageValueFormatter extends ValueFormatter {
    @Override
    public String getBarLabel(BarEntry barEntry) {
        return String.format(Locale.getDefault(), "%.2f%%", barEntry.getY());
    }
}
