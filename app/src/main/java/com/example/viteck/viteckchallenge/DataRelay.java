package com.example.viteck.viteckchallenge;

import android.app.Application;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.heatmaps.Gradient;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
//import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Greg on 1/28/18.
 */

public class DataRelay extends Application {
    public Location someLocation;
    public HashMap<LatLng, Gradient> theMap = new HashMap<>();
    public ArrayList<LatLng> nearByCodes = new ArrayList<>();
    HashMap<LatLng, AnalysisResults> analysisResults = new HashMap<>();


}
