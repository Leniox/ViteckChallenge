package com.example.viteck.viteckchallenge;

import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.maps.android.heatmaps.Gradient;
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
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;

    private View mLoginBtn;
    private TextView mSignupBtn;
    private TextView forgotPass;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public boolean isFirstStart;

    private ProgressDialog progressDialog;
    Location location;
    private DatabaseReference mDatabase;

    private int backButtonCount = 0;
    private ProgressDialog mProgress;
    private  DataRelay dataRelay;

    LinearLayout li;

    private LVBlock mLVBlock;
    private static int ANIMATION_DELAY = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Checking for first time launch - before calling setContentView()
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(LoginActivity.this, MainIntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();

        mAuth = FirebaseAuth.getInstance();

        li = (LinearLayout) findViewById(R.id.lay123);

        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);
        mLoginBtn = (Button) findViewById(R.id.loginBtn);
        mSignupBtn = (TextView) findViewById(R.id.signupBtn);
        forgotPass = findViewById(R.id.forgotPass);
        dataRelay = (DataRelay) getApplicationContext();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(LoginActivity.this, splashActivity.class);
                    startActivity(intent);
                    finish();
//                    //User has logged-in already
//                    //Move user directly to the Account Activity.
//                    setContentView(R.layout.activity_splash);
//                    mLVBlock = (LVBlock) findViewById(R.id.lv_block);
//
//                    mLVBlock.setViewColor(Color.rgb(245,209,22));
//                    mLVBlock.setShadowColor(Color.GRAY);
//                    mLVBlock.startAnim(1000);
//                    View splashView =  View.inflate(getApplicationContext(),R.layout.fragment_twitter_feed, );
//
//
//
//
//                    final HashMap<LatLng, Gradient> [] someArray = new HashMap[1];
//                    Thread pleaseWork = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                             location = getLastKnownLocation();
//                            //uncomment this and change the onMapReady function when showtime
//                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//                            List<Address> addresses = null;
//                            try {
//                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            String stateName = addresses.get(0).getAdminArea();
//                            HashMap<LatLng, Gradient> myMap = processMapLatLng(50, stateName);
//                            someArray[0] = myMap;
//
//
//
//                        }
//                    });
//                    pleaseWork.start();
//                    try {
//                        pleaseWork.join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    dataRelay.theMap = someArray[0];
//                    dataRelay.someLocation = location;
//                    mLVBlock.stopAnim();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();



//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            // This method will be executed once the timer is over
//                            // Start your app main activity
//                            mLVBlock.stopAnim();
//                            // close this activity
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//
//                            //mProgress.setMessage("Give Us A Moment...");
//                            //mProgress.show();
//                        }
//                    }, ANIMATION_DELAY);
                }
            }
        };



        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSignIn();

            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PasswordResetActivity.class));
            }
        });

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, SignUp.class));
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

            }
        });

        progressDialog = new ProgressDialog(LoginActivity.this);
    }

    public Location getLastKnownLocation() {

        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            mLocationManager = (LocationManager) this.getApplicationContext().getSystemService(LOCATION_SERVICE);

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
                            .url("http://54.174.183.245:5000/get_city")
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
                        Geocoder gc = new Geocoder(this);
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



    public void doWork(){
            setContentView(R.layout.fragment_map);
            MapFragment fragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_frag);

            Location location = fragment.getLastKnownLocation();
        //uncomment this and change the onMapReady function when showtime
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String [] jsonStringarr = new String[1];
        String stateName = addresses.get(0).getAdminArea();
        HashMap<LatLng, Gradient> myMap = fragment.processMapLatLng(50, stateName);
//        Thread thread1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mapFragment.processMapLatLng(25);
//            }
//        });
//        thread1.start();
//        Thread thread2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mapFragment.processMapLatLng(50);
//
//            }
//        });
//        thread2.start();
//        try
//        {
//            thread1.join();
//            thread2.join();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
        System.out.println(myMap);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void startSignIn() {

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            //User did not enter any email or password.
            Snackbar snackbar = Snackbar
                    .make(li, R.string.empty_field, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Snackbar snackbar = Snackbar
                    .make(li, R.string.incorrect_email, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else {

            progressDialog.setMessage("Logging in...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        //if incorrect email/password entered.
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(li, R.string.auth_failed, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(li, R.string.auth_success, Snackbar.LENGTH_LONG);
                        snackbar.show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
            });
        }

    }

}
