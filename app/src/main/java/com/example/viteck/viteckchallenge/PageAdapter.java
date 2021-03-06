package com.example.viteck.viteckchallenge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Greg on 12/2/17.
 */

public class PageAdapter extends FragmentStatePagerAdapter {
    int numOftabs;
    CardFragment cardFrag;
    TwitterFeed twitterFrag;
    MapFragment mapFragment;

    public PageAdapter (FragmentManager fm, int numOfTabs, CardFragment cardFrag, MapFragment mapFragment)
    {
        super(fm);
        this.numOftabs = numOfTabs;
        this.cardFrag = cardFrag;
        this.mapFragment = mapFragment;


    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                return mapFragment;
            case 1:
                return cardFrag;
            case 2:
                twitterFrag = new TwitterFeed();
                return twitterFrag;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOftabs;
    }
}
