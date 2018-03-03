package com.example.viteck.viteckchallenge;

/**
 * Created by Greg on 2/3/18.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.db.chart.model.Bar;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Greg on 12/2/17.
 */

public class GraphAdapter extends FragmentStatePagerAdapter {
    int numOftabs;
    CardFragment cardFrag;
    PieChart pieChart;
    BarChart barChart;

    public GraphAdapter (FragmentManager fm, int numOfTabs, PieChart pieChart, BarChart barChart)
    {
        super(fm);
        this.numOftabs = numOfTabs;
        this.barChart = barChart;
        this.pieChart = pieChart;


    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                return pieChart;
            case 1:
                return barChart;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOftabs;
    }
}
