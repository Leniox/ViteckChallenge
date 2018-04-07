package com.example.viteck.viteckchallenge;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.Gradient;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;
import com.ldoublem.loadingviewlib.view.LVBlock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Greg on 3/3/18.
 */

public class AsyncLogin  extends AsyncTask<String, Integer, HashMap<LatLng, Gradient>> {
    DataRelay dataRelay;
    Context ctx;
    AsyncLoginDelegate delegate;


    public AsyncLogin (Context ctx)
    {
        this.ctx = ctx;
      dataRelay =  (DataRelay) ctx.getApplicationContext();
    }

    @Override
    protected void onPreExecute() {





    }



    @Override
    protected HashMap<LatLng, Gradient> doInBackground(String... strings) {
        Location location = getLastKnownLocation();
        dataRelay.someLocation = location;
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String stateName = addresses.get(0).getAdminArea();
        HashMap<LatLng, Gradient> myMap = processMapLatLng(50, stateName);
        return myMap;
    }

    @Override
    protected void onPostExecute(HashMap<LatLng, Gradient> result) {
        delegate.processFinish(result);
    }
    public Location getLastKnownLocation() {

        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager mLocationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);

            mLocationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);

            List<String> providers = mLocationManager.getProviders(true);
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;


    }

    public HashMap<LatLng, Gradient> processMapLatLng(int count, String stateName) {
        //THIS IS BOTTLE NECK. FIX THIS
        HashMap<LatLng, Gradient> locationMap = new HashMap<>();
        ArrayList<LatLng> nearByCodes = new ArrayList<>();

        try {
            //Load File
            //BufferedReader jsonReader = new BufferedReader(new InputStreamReader(this.getResources().openRawResource(R..localjsonfile)));
            //BufferedReader jsonReader = new BufferedReader(new InputStreamReader(this.getResources().getAssets().open("vitechMap.json")));
//            StringBuilder jsonBuilder = new StringBuilder();
//            //consider making another rest endpoint to get this thing because it takes forever.
//            for (String line = null; (line = jsonReader.readLine()) != null; ) {
//                jsonBuilder.append(line).append("\n");
//            }
            JSONObject j = new JSONObject();
            final JSONObject jq = new JSONObject();
            j.put("actual_state", stateName);
            jq.put("state_data", j);

            final String [] okhttpResults = new String[1];
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    OkHttpClient client = new OkHttpClient();

                    RequestBody body = RequestBody.create(JSON, jq.toString());
                    Request request = new Request.Builder()
                            .url("http://52.87.237.215:5000/get_city")
                            .post(body)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        okhttpResults[0] = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            JSONObject someState = (JSONObject) new JSONObject(okhttpResults[0]);
            JSONObject actualStates = (JSONObject) new JSONObject((String) someState.get("cityMap"));

            JSONArray cities = (JSONArray) actualStates.names();



            if (cities.length() < count)
            {
                count = cities.length();
            }
            int i = 0;


            if (Geocoder.isPresent()) {
                try {

                    for (; i < count; i++) {


                        JSONObject cityObject = (JSONObject) actualStates.get((String) cities.get(i));
                        Geocoder gc = new Geocoder(ctx);
                        String testin = cities.getString(i) + stateName;
                        List<Address> address = gc.getFromLocationName(cities.getString(i) + " " +  stateName, 5); //
                        // get the found Address Objects
                        try {
                            int bronze = (int) cityObject.get("Bronze");
                            int silver = (int) cityObject.get("Silver");
                            int gold = (int) cityObject.get("Gold");
                            int platinum = (int) cityObject.get("Platinum");
                            int total = bronze + silver + gold + platinum;

                            for (Address a : address) {
                                if (a.hasLatitude() && a.hasLongitude()) {
                                    LatLng someLatLng = new LatLng(a.getLatitude(), a.getLongitude());
                                    nearByCodes.add(someLatLng);
                                    float[] intensityPoints = {
                                            (float) silver / total, (float) gold / total, (float) bronze / total, (float) platinum / total
                                    };
                                    Arrays.sort(intensityPoints);//I am sorting this which is going to put the order of silver,
                                    //gold,bronze, and platinum out of order i can fix this by also sorting the gradient_colors based
                                    //off of a key value pair where i sort the keys where the keys are the start points and the values
                                    //will then be in order!
                                    Pair<Float, Integer> testing = new Pair<Float, Integer>((float) silver / total, Color.rgb(255, 215, 0)); //silver
                                    Pair<Float, Integer> testing2 = new Pair<Float, Integer>((float) gold / total, Color.rgb(211, 211, 211));//gold
                                    Pair<Float, Integer> testing3 = new Pair<Float, Integer>((float) bronze / total, Color.rgb(212, 175, 55));//bronze,
                                    Pair<Float, Integer> testing4 = new Pair<Float, Integer>((float) platinum / total, Color.rgb(160, 170, 191));
                                    ArrayList<Pair<Float, Integer>> health_gradient = new ArrayList<>();
                                    health_gradient.add(testing);
                                    health_gradient.add(testing2);
                                    health_gradient.add(testing3);
                                    health_gradient.add(testing4);
                                    health_gradient.sort(new Comparator<Pair<Float, Integer>>() {
                                        @Override
                                        public int compare(Pair<Float, Integer> o1, Pair<Float, Integer> o2) {
                                            if (o1.first == o2.first){


                                            }
                                            return Float.compare(o1.first, o2.first);
                                        }
                                    });
                                    int[] final_gradient = new int[4];
                                    for (int k = 0; k < health_gradient.size(); k++) {
                                        final_gradient[k] = health_gradient.get(k).second;
                                    }


                                    Gradient someGradient = new Gradient(final_gradient,
                                            intensityPoints);
                                    locationMap.put(someLatLng, someGradient);


                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        dataRelay.nearByCodes = nearByCodes;
        return locationMap;





    }

}
