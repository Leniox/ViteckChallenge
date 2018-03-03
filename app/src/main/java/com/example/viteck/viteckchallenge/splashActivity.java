package com.example.viteck.viteckchallenge;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.Gradient;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ldoublem.loadingviewlib.view.LVBlock;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class splashActivity extends AppCompatActivity implements AsyncLoginDelegate {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LVBlock mLVBlock = (LVBlock) findViewById(R.id.lv_block);
        DataRelay dataRelay = (DataRelay) getApplicationContext();

        mLVBlock.setViewColor(Color.rgb(245,209,22));
        mLVBlock.setShadowColor(Color.GRAY);
        mLVBlock.startAnim(1000);
        AsyncLogin asyncLogin = new AsyncLogin(this);
        asyncLogin.delegate = this;
        asyncLogin.execute();



    }


    @Override
    public void processFinish(HashMap<LatLng, Gradient> output) {
        DataRelay dataRelay = (DataRelay) getApplicationContext();
        dataRelay.theMap = output;
        LVBlock mLVBlock = (LVBlock) findViewById(R.id.lv_block);
        mLVBlock.stopAnim();
        Intent intent = new Intent(splashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();


    }
}
