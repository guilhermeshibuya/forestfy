package com.example.reconhecimentoflorestal.ui.results;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.reconhecimentoflorestal.ml.ModelResults;
import com.example.reconhecimentoflorestal.ml.ModelUtilities;
import com.example.reconhecimentoflorestal.R;
import com.example.reconhecimentoflorestal.SharedViewModel;
import com.example.reconhecimentoflorestal.utils.PercentageValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Locale;

public class ResultsFragment extends Fragment {
    private SharedViewModel viewModel;
    private BarChart hBarChart;
    private TextView speciesNamesText;
    private String[] classes = {
            "Acrocarpus fraxinifolius_ACROCARPUS",
            "Apuleia leiocarpa_GARAPEIRA",
            "Araucaria angustifolia_ARAUCARIA",
            "Aspidosperma polyneuron_PEROBA ROSA",
            "Aspidosperma sp_PAU CETIM",
            "Bagassa guianensis_TATAJUBA",
            "Balfourodendron riedelianum_PAU MARFIM",
            "Bertholletia excelsa_CASTANHEIRA",
            "Bowdichia sp_SUCUPIRA",
            "Brosimum paraensis_MUIRAPIRANGA",
            "Carapa guianensis_ANDIROBA",
            "Cariniana estrellensis_JEQUITIBA",
            "Cedrela fissilis_CEDRO",
            "Cedrelinga catenaeformis_CEDRORANA",
            "Clarisia racemosa_GUARIUBA",
            "Cordia Goeldiana_FREIJO",
            "Cordia alliodora_LOURO-AMARELO",
            "Couratari sp_TAUARI",
            "Dipteryx sp_CUMARU",
            "Erisma uncinatum_CEDRINHO",
            "Eucalyptus sp_EUCALIPTO",
            "Euxylophora paraensis_PAU AMARELO",
            "Goupia glabra_CUPIUBA",
            "Grevilea robusta_GREVILEA",
            "Handroanthus sp_IPE",
            "Hymenaea sp_JATOBA",
            "Hymenolobium petraeum_ANGELIM PEDRA",
            "Laurus nobilis_LOURO",
            "Machaerium sp_MACHAERIUM",
            "Manilkara huberi_MASSARANDUBA",
            "Melia azedarach_CINAMOMO",
            "Mezilaurus itauba_ITAUBA",
            "Micropholis venulosa_CURUPIXA",
            "Mimosa scabrella_BRACATINGA",
            "Myroxylon balsamum_CABREUVA VERMELHA",
            "Ocotea porosa_IMBUIA",
            "Peltagyne sp_ROXINHO",
            "Pinus sp_PINUS",
            "Podocarpus lambertii_PODOCARPUS",
            "Pouteria pachycarpa_GOIABAO",
            "Simarouba amara_MARUPA",
            "Swietenia macrophylla_MOGNO",
            "Virola surinamensis_VIROLA",
            "Vochysia sp_QUARUBA CEDRO"
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.results_fragment, container, false);

        viewModel.getImage().observe(getViewLifecycleOwner(), image -> {
            long startTime = System.nanoTime();

            float[][][][] inputArray = ModelUtilities.preprocessImages(image);

            ModelResults results = ModelUtilities.runModel(requireContext(), inputArray);

            StringBuilder resultBuilder = new StringBuilder();
            for (int i = 0; i < results.results.length; i++) {
                float percentage = results.results[i] * 100;
                String name = classes[results.indices[i]];
                resultBuilder.append(String.format("Classe: %s, Confiança: %.6f%%", name, percentage));
                resultBuilder.append("\n");
            }
            System.out.println(resultBuilder.toString());

            long endTime = System.nanoTime();
            long inferenceTime = endTime - startTime;

            double inferenceTimeMs = inferenceTime / 1_000_000.0;
            System.out.println("Tempo de inferência e processamento: " + inferenceTimeMs + " ms");

            hBarChart = view.findViewById(R.id.resultsBarChart);
            speciesNamesText = view.findViewById(R.id.speciesNames);

            String[] xNames = new String[5];

            ArrayList<BarEntry> barEntries = new ArrayList<>();
            for (int i = 0; i < results.results.length; i++) {
                float value = results.results[i] * 100;

                xNames[i] = classes[results.indices[i]];

                BarEntry entry = new BarEntry(i, value);
                barEntries.add(entry);
            }

            BarDataSet barDataSet = new BarDataSet(barEntries, getString(R.string.results_chart));

            barDataSet.setColors(Color.parseColor("#5AC279"));

            BarData barData = new BarData(barDataSet);
            barData.setValueFormatter(new PercentageValueFormatter());
            barData.setValueTextSize(12f);
            barData.setBarWidth(0.8f);

            hBarChart.setData(barData);

            hBarChart.setTouchEnabled(false);

            hBarChart.getDescription().setEnabled(false);

            hBarChart.getXAxis().setDrawGridLines(false);
            hBarChart.getXAxis().setDrawAxisLine(false);

            hBarChart.getAxisLeft().setDrawGridLines(false);
            hBarChart.getAxisLeft().setDrawAxisLine(false);
            hBarChart.getAxisLeft().setDrawLabels(false);

            hBarChart.getAxisRight().setDrawGridLines(false);
            hBarChart.getAxisRight().setDrawAxisLine(false);
            hBarChart.getAxisRight().setDrawLabels(false);

            String[] species = {"a", "b", "c", "d", "e"};
            XAxis xAxis = hBarChart.getXAxis();
            xAxis.setGranularityEnabled(true);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(species));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(12f);

            for (int i = 0; i < xNames.length; i++) {
                speciesNamesText.append(species[i] + ": " + xNames[i] + "\n");
            }
        });

        return  view;
    }
}
