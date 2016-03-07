package com.kaalivandi;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by nandhu on 6/3/16.
 */
public class CustomAdapter extends FragmentPagerAdapter {




    public String Fragments[]={"Home","Instructions","About us"};
    public static int NUM_FRAGMENTS=3;
    public void pop(String sm){
        Log.i("Flow", sm);
    }
    public CustomAdapter(FragmentManager manager, Context context) {
        super(manager);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {

        switch (position){
            case 0:
                return  new HomeFragmet();
            case 1:
                return new ManDrvierFragment();
            case  2:
                return  new FaqFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_FRAGMENTS;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return Fragments[position];
    }



}
