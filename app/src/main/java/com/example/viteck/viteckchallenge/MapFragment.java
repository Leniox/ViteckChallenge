package com.example.viteck.viteckchallenge;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import eu.amirs.JSON;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, PlaceSelectionListener, GoogleMap.OnMyLocationButtonClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5;
    private GoogleMap map;
    MapView mapView;
    private String stateName;
    View mview;
    private static final int ALT_HEATMAP_RADIUS = 10;
    private static final double ALT_HEATMAP_OPACITY = 0.4;
    private static final int[] ALT_HEATMAP_GRADIENT_COLORS = {
            Color.rgb(255, 215, 0), //silver
            Color.rgb(211, 211, 211),//gold
            Color.rgb(212, 175, 55)//bronze,
            ,Color.rgb(160, 170, 191)//platinum
    };

    public   HashMap<String, String> STATE_MAP = new HashMap<>();



    public static final float[] ALT_HEATMAP_GRADIENT_START_POINTS = {
            0.0f, 0.10f, 0.20f,0.30f
    };
    public static final Gradient ALT_HEATMAP_GRADIENT = new Gradient(ALT_HEATMAP_GRADIENT_COLORS,
            ALT_HEATMAP_GRADIENT_START_POINTS);
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    HashMap<LatLng, Gradient> theMap = new HashMap<>();
    Thread thread;
    Location location = null;
    private int numCities = 50;
    static HashMap<LatLng, Gradient> locationMap = new HashMap<>();
    DataRelay dataRelay;
    public MapFragment() {
        // Required empty public constructor
    }

    //this works with a view pager because when we create a fragment, we return a View that corresponds to its layout file
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview =  inflater.inflate(R.layout.fragment_map, container, false);

//        SupportMapFragment mapFragment = (SupportMapFragment)getFragmentManager().findFragmentById(R.id.map1);
//        mapFragment.getMapAsync(this);
        dataRelay = (DataRelay) getActivity().getApplicationContext();
         locationMap = dataRelay.theMap;
         location = dataRelay.someLocation;

            STATE_MAP = new HashMap<String, String>();STATE_MAP.put("AL", "Alabama");STATE_MAP.put("AK", "Alaska");
            STATE_MAP.put("AB", "Alberta");STATE_MAP.put("AZ", "Arizona");STATE_MAP.put("AR", "Arkansas");STATE_MAP.put("BC", "British Columbia");STATE_MAP.put("CA", "California");
            STATE_MAP.put("CO", "Colorado");STATE_MAP.put("CT", "Connecticut");STATE_MAP.put("DE", "Delaware");STATE_MAP.put("DC", "District Of Columbia");
            STATE_MAP.put("FL", "Florida");STATE_MAP.put("GA", "Georgia");
            STATE_MAP.put("GU", "Guam");STATE_MAP.put("HI", "Hawaii");STATE_MAP.put("ID", "Idaho");
            STATE_MAP.put("IL", "Illinois");STATE_MAP.put("IN", "Indiana");STATE_MAP.put("IA", "Iowa");
            STATE_MAP.put("KS", "Kansas");STATE_MAP.put("KY", "Kentucky");
            STATE_MAP.put("LA", "Louisiana");STATE_MAP.put("ME", "Maine");STATE_MAP.put("MB", "Manitoba");STATE_MAP.put("MD", "Maryland");STATE_MAP.put("MA", "Massachusetts");STATE_MAP.put("MI", "Michigan");STATE_MAP.put("MN", "Minnesota");STATE_MAP.put("MS", "Mississippi");STATE_MAP.put("MO", "Missouri");STATE_MAP.put("MT", "Montana");
            STATE_MAP.put("NE", "Nebraska");STATE_MAP.put("NV", "Nevada");
            STATE_MAP.put("NB", "New Brunswick");STATE_MAP.put("NH", "New Hampshire");STATE_MAP.put("NJ", "New Jersey");STATE_MAP.put("NM", "New Mexico");
            STATE_MAP.put("NY", "New York");STATE_MAP.put("NF", "Newfoundland");STATE_MAP.put("NC", "North Carolina");STATE_MAP.put("ND", "North Dakota");STATE_MAP.put("NT", "Northwest Territories");STATE_MAP.put("NS", "Nova Scotia");STATE_MAP.put("NU", "Nunavut");STATE_MAP.put("OH", "Ohio");STATE_MAP.put("OK", "Oklahoma");STATE_MAP.put("ON", "Ontario");STATE_MAP.put("OR", "Oregon");STATE_MAP.put("PA", "Pennsylvania");STATE_MAP.put("PE", "Prince Edward Island");STATE_MAP.put("PR", "Puerto Rico");STATE_MAP.put("QC", "Quebec");STATE_MAP.put("RI", "Rhode Island");STATE_MAP.put("SK", "Saskatchewan");STATE_MAP.put("SC", "South Carolina");STATE_MAP.put("SD", "South Dakota");STATE_MAP.put("TN", "Tennessee");STATE_MAP.put("TX", "Texas");STATE_MAP.put("UT", "Utah");
            STATE_MAP.put("VT", "Vermont");STATE_MAP.put("VI", "Virgin Islands");
            STATE_MAP.put("VA", "Virginia");STATE_MAP.put("WV", "West Virginia");
            STATE_MAP.put("WI", "Wisconsin");STATE_MAP.put("WY", "Wyoming");
            STATE_MAP.put("YT", "Yukon Territory");



        return mview;

    }
    public String getNearestXCities(int x)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://api.geonames.org/findNearbyPlaceNameJSON?lat=" + location.getLatitude() + "" +
                " &lng=" + location.getLongitude() + "&style=short&cities=15000&radius=30&maxRows=" + x + "&username=leniox77";
        Request request = new Request.Builder()
                .url(url)
                .build();
        String results = "";
        try
        {
             Response response = okHttpClient.newCall(request).execute();

             results = response.body().string();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return results;




    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {

        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView) mview.findViewById(R.id.map1);

        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(this);
        PlaceAutocompleteFragment autocompleteFragment  = (PlaceAutocompleteFragment)getActivity().getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        @SuppressLint("ResourceType") View locationButton = ((View) mview.findViewById(1).getParent()).findViewById(2);

        // and next place it, for exemple, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);


        // Register a listener to receive callbacks when a place has been selected or an error has
        // occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer .initialize(getContext());
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            zoomToLocation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        CameraPosition somePosition = CameraPosition.builder().target(new LatLng(41.316324, -72.922343)).zoom(16).bearing(0).tilt(45).build();
//        map.moveCamera(CameraUpdateFactory.newCameraPosition(somePosition));

//        LatLng pp = new LatLng();

    }


    private void zoomToLocation() throws InterruptedException {
        if (ActivityCompat.checkSelfPermission(mview.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mview.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            int count = 0;
            for (LatLng latLng : locationMap.keySet())
            {
                if (count == numCities)
                {
                    break;
                }
                count++;
                List<LatLng> list = new ArrayList<>();
                list.add(latLng);
                mProvider = new HeatmapTileProvider.Builder()
                        .data(list)
                        .radius(50)
                        .gradient(locationMap.get(latLng))
                        .build();
                // Add a tile overlay to the map, using the heat map tile provider.
                mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

            }




            if (location != null) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude())));


            }
            else {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                zoomToLocation();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
            thread.join();

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
                    Geocoder gc = new Geocoder(getContext());
                    List<Address> address = gc.getFromLocationName(cities.getString(i) + " " + stateName, 5); //
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

    public Location getLastKnownLocation() {

        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(mview.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mview.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager mLocationManager = (LocationManager) mview.getContext().getSystemService(LOCATION_SERVICE);

            mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);

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
    private Handler handler;

    int counter = 0;

    GoogleProgressBar googleProgressBar;

    @Override
    public void onPlaceSelected(Place place) {
        CharSequence address = place.getAddress();
        String[] addarr = address.toString().split(" ");
        String stateabrev = addarr[addarr.length-2].replaceAll("[,./']", "");
        String fullStateName = STATE_MAP.get(stateabrev);

        //show dialog
//
         googleProgressBar = mview.findViewById(R.id.google_progress);
//        googleProgressBar.setVisibility(View.VISIBLE);
//        googleProgressBar.bringToFront();
        googleProgressBar.bringToFront();
        googleProgressBar.setVisibility(View.VISIBLE);
        googleProgressBar.setIndeterminate(true);



        locationMap = processMapLatLng(50, fullStateName);
        googleProgressBar.setVisibility(View.INVISIBLE);
        int count = 0;
        for (LatLng latLng : locationMap.keySet()) {
            if (count == numCities) {
                break;
            }
            count++;
            List<LatLng> list = new ArrayList<>();
            list.add(latLng);
            mProvider = new HeatmapTileProvider.Builder()
                    .data(list)
                    .radius(50)
                    .gradient(locationMap.get(latLng))
                    .build();
            // Add a tile overlay to the map, using the heat map tile provider.
            mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));



            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude)));




        }
    }

    @Override
    public void onError(Status status) {
        Log.e("tag", "onError: Status = " + status.toString());

        Toast.makeText(getActivity(), "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }
}
