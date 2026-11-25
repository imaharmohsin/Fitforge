package com.example.fitforge;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

import java.util.ArrayList;
import java.util.List;

public class Progress_Tracking extends AppCompatActivity {

    // Views
    TextView tabDay, tabWeek, tabMonth;
    TextView tvCaloriesValue, tvWorkoutValue;
    TextView tvCurrentWeight, tvWeightBig;
    TextView tvWeightDelta;
    ImageView btnAddWeight;
    BarChart barChartView;
    LineChart lineChartView;
    FrameLayout progressGoal;

    // Data
    float currentWeight = 78.4f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_fragments);

        initViews();
        setupTabs();
        setupBarChart();
        setupLineChart();
        setupWeightProgress();
        setupAddWeight();
    }

    // ✅ Views initialize
    private void initViews() {
        tabDay          = findViewById(R.id.tabDay);
        tabWeek         = findViewById(R.id.tabWeek);
        tabMonth        = findViewById(R.id.tabMonth);
        tvCaloriesValue = findViewById(R.id.tvCaloriesValue);
        tvWorkoutValue  = findViewById(R.id.tvWorkoutValue);
        tvCurrentWeight = findViewById(R.id.tvCurrentWeight);
        tvWeightBig     = findViewById(R.id.tvWeightBig);
        tvWeightDelta   = findViewById(R.id.tvWeightDelta);
        btnAddWeight    = findViewById(R.id.btnAddWeight);
        barChartView    = findViewById(R.id.barChartView);
        lineChartView   = findViewById(R.id.lineChartView);
//        progressGoal    = findViewById(R.id.progressGoal);
    }

    // ✅ Tab switching
    private void setupTabs() {
        tabDay.setOnClickListener(v -> {
            setActiveTab(tabDay);
            setInactiveTab(tabWeek);
            setInactiveTab(tabMonth);
            updateDataForPeriod("day");
        });

        tabWeek.setOnClickListener(v -> {
            setActiveTab(tabWeek);
            setInactiveTab(tabDay);
            setInactiveTab(tabMonth);
            updateDataForPeriod("week");
        });

        tabMonth.setOnClickListener(v -> {
            setActiveTab(tabMonth);
            setInactiveTab(tabDay);
            setInactiveTab(tabWeek);
            updateDataForPeriod("month");
        });
    }

    private void setActiveTab(TextView tab) {
        tab.setTextColor(Color.WHITE);
        tab.setBackgroundResource(R.drawable.bg_tab_active);
    }

    private void setInactiveTab(TextView tab) {
        tab.setTextColor(Color.parseColor("#888888"));
        tab.setBackgroundColor(Color.TRANSPARENT);
    }

    // ✅ Data update based on tab
    private void updateDataForPeriod(String period) {
        switch (period) {
            case "day":
                tvCaloriesValue.setText("1,840");
                tvWorkoutValue.setText("1.5 hrs");
                updateBarChart(new float[]{30, 45, 60, 40, 55, 35, 50});
                break;
            case "week":
                tvCaloriesValue.setText("12,450");
                tvWorkoutValue.setText("12.5 hrs");
                updateBarChart(new float[]{60, 80, 45, 90, 70, 55, 85});
                break;
            case "month":
                tvCaloriesValue.setText("48,200");
                tvWorkoutValue.setText("52 hrs");
                updateBarChart(new float[]{70, 85, 65, 90, 75, 80, 95});
                break;
        }
    }

    // ✅ Bar Chart setup
    private void setupBarChart() {
        barChartView.setDrawGridBackground(false);
        barChartView.setDrawBorders(false);
        barChartView.getDescription().setEnabled(false);
        barChartView.getLegend().setEnabled(false);
        barChartView.setTouchEnabled(false);
        barChartView.setScaleEnabled(false);
        barChartView.setPinchZoom(false);
        barChartView.setBackgroundColor(Color.TRANSPARENT);
        barChartView.setExtraBottomOffset(8f);

        // X Axis
        XAxis xAxis = barChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.parseColor("#888888"));
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}));
        xAxis.setGranularity(1f);

        // Y Axis
        YAxis leftAxis = barChartView.getAxisLeft();
        leftAxis.setEnabled(false);
        barChartView.getAxisRight().setEnabled(false);

        updateBarChart(new float[]{60, 80, 45, 90, 70, 55, 85});
    }

    private void updateBarChart(float[] values) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            entries.add(new BarEntry(i, values[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#B3D400"));
        dataSet.setDrawValues(false);
//        dataSet.setBarBorderRadius(6f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);
        barChartView.setData(barData);
        barChartView.invalidate();
    }

    // ✅ Line Chart setup
    private void setupLineChart() {
        lineChartView.setDrawGridBackground(false);
        lineChartView.setDrawBorders(false);
        lineChartView.getDescription().setEnabled(false);
        lineChartView.getLegend().setEnabled(false);
        lineChartView.setTouchEnabled(false);
        lineChartView.setScaleEnabled(false);
        lineChartView.setBackgroundColor(Color.TRANSPARENT);


        XAxis xAxis = lineChartView.getXAxis();
        xAxis.setEnabled(false);


        lineChartView.getAxisLeft().setEnabled(false);
        lineChartView.getAxisRight().setEnabled(false);

        // Weight data
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 80.5f));
        entries.add(new Entry(1, 80.1f));
        entries.add(new Entry(2, 79.8f));
        entries.add(new Entry(3, 79.5f));
        entries.add(new Entry(4, 79.0f));
        entries.add(new Entry(5, 78.7f));
        entries.add(new Entry(6, 78.4f));

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#B3D400"));
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#B3D400"));
        dataSet.setFillAlpha(30);

        LineData lineData = new LineData(dataSet);
        lineChartView.setData(lineData);
        lineChartView.invalidate();
    }

    // ✅ Weight progress bar
    private void setupWeightProgress() {
        // 83% progress — goal 75kg, start 85kg
        progressGoal.post(() -> {
            int totalWidth = progressGoal.getWidth();
            int progressWidth = (int) (totalWidth * 0.83f);

            View fill = new View(this);
            fill.setBackgroundResource(R.drawable.bg_progress_fill);

            FrameLayout.LayoutParams params =
                    new FrameLayout.LayoutParams(progressWidth,
                            FrameLayout.LayoutParams.MATCH_PARENT);
            fill.setLayoutParams(params);
            progressGoal.addView(fill);
        });
    }

    // ✅ Add weight button
    private void setupAddWeight() {
        btnAddWeight.setOnClickListener(v -> showAddWeightDialog());
    }

    private void showAddWeightDialog() {
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Weight");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Enter weight in kg");
        input.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER |
                        android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setPadding(40, 20, 40, 20);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String weightStr = input.getText().toString().trim();
            if (!weightStr.isEmpty()) {
                try {
                    float newWeight = Float.parseFloat(weightStr);
                    currentWeight = newWeight;
                    tvWeightBig.setText(String.valueOf(newWeight));
                    tvCurrentWeight.setText(newWeight + " kg current");

                    // Delta update
                    float delta = 78.4f - newWeight;
                    if (delta > 0) {
                        tvWeightDelta.setText(
                                String.format("↓ -%.1f kg", delta));
                        tvWeightDelta.setTextColor(
                                Color.parseColor("#FF6B9D"));
                    } else {
                        tvWeightDelta.setText(
                                String.format("↑ +%.1f kg", Math.abs(delta)));
                        tvWeightDelta.setTextColor(
                                Color.parseColor("#B3D400"));
                    }

                    android.widget.Toast.makeText(this,
                                    "Weight updated!", android.widget.Toast.LENGTH_SHORT)
                            .show();
                } catch (NumberFormatException e) {
                    android.widget.Toast.makeText(this,
                                    "Invalid weight", android.widget.Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        builder.setNegativeButton("Cancel",
                (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}