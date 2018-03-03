package com.example.viteck.viteckchallenge;

import com.google.android.gms.maps.model.LatLng;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;

import java.util.HashMap;

/**
 * Created by Greg on 2/3/18.
 */

interface  AsyncTwitterDelegate {
    void processFinish(HashMap<LatLng, AnalysisResults> output);

}
