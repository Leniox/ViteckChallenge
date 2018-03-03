package com.example.viteck.viteckchallenge;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.Gradient;

import java.util.HashMap;

/**
 * Created by Greg on 3/3/18.
 */

interface  AsyncLoginDelegate {

        void processFinish( HashMap<LatLng, Gradient> output);


}
