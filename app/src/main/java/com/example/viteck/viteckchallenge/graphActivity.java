package com.example.viteck.viteckchallenge;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.*;
import com.google.android.gms.maps.model.LatLng;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class graphActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    HashMap<LatLng, AnalysisResults> analysisRegionHashMap = new HashMap<>();
    DataRelay dataRelay;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        dataRelay = (DataRelay) getApplicationContext();
        analysisRegionHashMap = dataRelay.analysisResults;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        TabLayout tabLayout = (TabLayout) findViewById(R.id.graph_tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Pie Chart"));
        tabLayout.addTab(tabLayout.newTab().setText("Bar Chart"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.graph_pager);
        //ASK WHY IT CRASHES WHEN I REMOVE OR. OR RATHER, HOW DO I FIX IT?
        MapFragment mapFragment = new MapFragment();
        viewPager.setOffscreenPageLimit(2);
        com.example.viteck.viteckchallenge.PieChart pieChart = new com.example.viteck.viteckchallenge.PieChart();
        BarChart barChart = new BarChart();

        final GraphAdapter adapter = new GraphAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), pieChart, barChart);
        viewPager.setAdapter(adapter);
        tabLayout.setOnTabSelectedListener(graphActivity.this);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        ImageButton imageButton = findViewById(R.id.imageButtonBack);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }



    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
