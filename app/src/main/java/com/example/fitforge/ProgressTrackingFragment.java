package com.example.fitforge;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ProgressTrackingFragment extends Fragment {

    // ── Tab Bar ──
    private TextView tabDay, tabWeek, tabMonth;
    private LinearLayout tabContainer;

    // ── Stat Cards ──
    private TextView tvCaloriesValue, tvCaloriesBadge;
    private TextView tvWorkoutValue, tvWorkoutBadge;


    private View progressGoal;
    private CardView btnAddWeight;

    // ── Charts ──
    private BarChart barChartView;
    private LineChart lineChartView;

    // ── Colors ──
    private static final int COLOR_LIME     = Color.parseColor("#CCF200");
    private static final int COLOR_PURPLE   = Color.parseColor("#A78BFA");
    private static final int COLOR_PINK     = Color.parseColor("#FB7185");
    private static final int COLOR_CARD     = Color.parseColor("#141414");
    private static final int COLOR_MUTED    = Color.parseColor("#666666");
    private static final int COLOR_WHITE    = Color.WHITE;
    private static final int COLOR_BG       = Color.parseColor("#0A0A0A");
    private static final int COLOR_DIVIDER  = Color.parseColor("#1E1E1E");

    // ── Active tab state ──
    private String activeTab = "Day";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_fragments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupTabBar();
        loadStatCards("Day");
        setupBarChart("Day");
        setupLineChart();
        setGoalProgress(83);  // 83% toward goal
        setListeners();
    }

    // ── Init Views ───────────────────────────────────────────────────────────
    private void initViews(View view) {
        tabDay   = view.findViewById(R.id.tabDay);
        tabWeek  = view.findViewById(R.id.tabWeek);
        tabMonth = view.findViewById(R.id.tabMonth);

        tvCaloriesValue  = view.findViewById(R.id.tvCaloriesValue);
        tvCaloriesBadge  = view.findViewById(R.id.tvCaloriesBadge);
        tvWorkoutValue   = view.findViewById(R.id.tvWorkoutValue);
        tvWorkoutBadge   = view.findViewById(R.id.tvWorkoutBadge);

        progressGoal     = view.findViewById(R.id.progressGoal);
        btnAddWeight     = view.findViewById(R.id.btnAddWeight);

        barChartView  = view.findViewById(R.id.barChartView);
        lineChartView = view.findViewById(R.id.lineChartView);
    }

    // ── Tab Bar Setup ────────────────────────────────────────────────────────
    private void setupTabBar() {
        setActiveTab("Day");
    }

    private void setActiveTab(String tab) {
        activeTab = tab;

        // Reset all tabs
        resetTab(tabDay);
        resetTab(tabWeek);
        resetTab(tabMonth);

        // Activate selected
        switch (tab) {
            case "Day":
                activateTab(tabDay);
                break;
            case "Week":
                activateTab(tabWeek);
                break;
            case "Month":
                activateTab(tabMonth);
                break;
        }

        loadStatCards(tab);
        setupBarChart(tab);
    }

    private void activateTab(TextView tv) {
        tv.setBackgroundResource(R.drawable.bg_tab_active);
        tv.setTextColor(Color.parseColor("#0A0A0A")); // dark text on lime bg
    }

    private void resetTab(TextView tv) {
        tv.setBackgroundColor(Color.TRANSPARENT);
        tv.setTextColor(COLOR_MUTED);
    }

    // ── Stat Cards Data ──────────────────────────────────────────────────────
    private void loadStatCards(String tab) {
        switch (tab) {
            case "Day":
                tvCaloriesValue.setText("1,840");
                tvCaloriesBadge.setText("↑ +8.2%");
                tvCaloriesBadge.setTextColor(COLOR_LIME);
                tvWorkoutValue.setText("1.5 hrs");
                tvWorkoutBadge.setText("↑ +15%");
                tvWorkoutBadge.setTextColor(COLOR_PURPLE);
                break;
            case "Week":
                tvCaloriesValue.setText("12,450");
                tvCaloriesBadge.setText("↑ +5.1%");
                tvCaloriesBadge.setTextColor(COLOR_LIME);
                tvWorkoutValue.setText("6.5 hrs");
                tvWorkoutBadge.setText("↑ +10%");
                tvWorkoutBadge.setTextColor(COLOR_PURPLE);
                break;
            case "Month":
                tvCaloriesValue.setText("48,200");
                tvCaloriesBadge.setText("↑ +12%");
                tvCaloriesBadge.setTextColor(COLOR_LIME);
                tvWorkoutValue.setText("24 hrs");
                tvWorkoutBadge.setText("↑ +20%");
                tvWorkoutBadge.setTextColor(COLOR_PURPLE);
                break;
        }
    }

    // ── Bar Chart ────────────────────────────────────────────────────────────
    private void setupBarChart(String tab) {
        float[] dayData   = {45f, 60f, 30f, 75f, 50f, 90f, 40f};
        float[] weekData  = {180f, 240f, 150f, 300f, 200f, 360f, 160f};
        float[] monthData = {600f, 850f, 720f, 940f, 680f};

        String[] dayLabels   = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[] weekLabels  = {"Wk1", "Wk2", "Wk3", "Wk4", "Wk5", "Wk6", "Wk7"};
        String[] monthLabels = {"Oct", "Nov", "Dec", "Jan", "Feb"};

        float[] values;
        String[] labels;

        switch (tab) {
            case "Week":  values = weekData;  labels = weekLabels;  break;
            case "Month": values = monthData; labels = monthLabels; break;
            default:      values = dayData;   labels = dayLabels;   break;
        }

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            entries.add(new BarEntry(i, values[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(COLOR_LIME);
        dataSet.setValueTextColor(Color.TRANSPARENT);   // hide values on bars
        dataSet.setValueTextSize(0f);
        dataSet.setHighlightEnabled(true);
        dataSet.setHighLightColor(Color.parseColor("#88CCF200"));
//        dataSet.setBarBorderRadius(6f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.55f);

        barChartView.setData(barData);
        barChartView.setFitBars(true);
        barChartView.getDescription().setEnabled(false);
        barChartView.getLegend().setEnabled(false);
        barChartView.setDrawGridBackground(false);
        barChartView.setDrawBorders(false);
        barChartView.setBackgroundColor(COLOR_CARD);
        barChartView.setExtraBottomOffset(8f);
        barChartView.setScaleEnabled(false);
        barChartView.setPinchZoom(false);

        // X Axis
        XAxis xAxis = barChartView.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(COLOR_MUTED);
        xAxis.setTextSize(10f);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.length);

        // Left Y Axis
        YAxis leftAxis = barChartView.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#1A1A1A"));
        leftAxis.setTextColor(COLOR_MUTED);
        leftAxis.setTextSize(9f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setLabelCount(4, true);

        // Right Y Axis — hide
        barChartView.getAxisRight().setEnabled(false);

        barChartView.animateY(600);
        barChartView.invalidate();
    }

    // ── Line Chart ───────────────────────────────────────────────────────────
    private void setupLineChart() {
        // Weight data: declining trend (kg over time)
        float[] weights = {80.5f, 80.1f, 79.8f, 79.4f, 79.0f, 78.7f, 78.4f};

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < weights.length; i++) {
            entries.add(new Entry(i, weights[i]));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(COLOR_LIME);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(COLOR_LIME);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleHoleColor(COLOR_CARD);
        dataSet.setCircleHoleRadius(2f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(COLOR_LIME);
        dataSet.setFillAlpha(18);
        dataSet.setHighlightEnabled(true);
        dataSet.setHighLightColor(Color.parseColor("#88CCF200"));
        dataSet.setDrawHighlightIndicators(true);

        LineData lineData = new LineData(dataSet);

        lineChartView.setData(lineData);
        lineChartView.getDescription().setEnabled(false);
        lineChartView.getLegend().setEnabled(false);
        lineChartView.setDrawGridBackground(false);
        lineChartView.setDrawBorders(false);
        lineChartView.setBackgroundColor(COLOR_CARD);
        lineChartView.setTouchEnabled(true);
        lineChartView.setScaleEnabled(false);
        lineChartView.setPinchZoom(false);

        // X Axis — hide (we have custom labels below)
        XAxis xAxis = lineChartView.getXAxis();
        xAxis.setEnabled(false);

        // Left Y Axis
        YAxis leftAxis = lineChartView.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#1A1A1A"));
        leftAxis.setTextColor(COLOR_MUTED);
        leftAxis.setTextSize(9f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setLabelCount(3, true);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + " kg";
            }
        });


        lineChartView.getAxisRight().setEnabled(false);

        lineChartView.animateX(800);
        lineChartView.invalidate();
    }


    private void setGoalProgress(final int percent) {
        if (progressGoal == null) return;

        progressGoal.post(() -> {
            View parent = (View) progressGoal.getParent();
            int totalWidth = parent.getWidth();
            int fillWidth  = (int) (totalWidth * (percent / 100f));

            ViewGroup.LayoutParams params = progressGoal.getLayoutParams();
            params.width = fillWidth;
            progressGoal.setLayoutParams(params);
        });
    }

    // ── Listeners ────────────────────────────────────────────────────────────
    private void setListeners() {

        // Tab clicks
        tabDay.setOnClickListener(v   -> setActiveTab("Day"));
        tabWeek.setOnClickListener(v  -> setActiveTab("Week"));
        tabMonth.setOnClickListener(v -> setActiveTab("Month"));

        // Add weight button
        if (btnAddWeight != null) {
            btnAddWeight.setOnClickListener(v -> showAddWeightDialog());
        }
    }

    // ── Add Weight Dialog ────────────────────────────────────────────────────
    private void showAddWeightDialog() {
        android.app.Dialog dialog = new android.app.Dialog(requireContext());
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_add_weight);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (requireActivity().getResources().getDisplayMetrics().widthPixels * 0.85f),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setDimAmount(0.6f);
        }
    }
}