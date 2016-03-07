package com.kaalivandi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by nandhu on 6/3/16.
 */
public class Network {

    private Context mContext;
    public Network(Context c) {
        mContext=c;
    }

    public  boolean isOnline(){

        ConnectivityManager co=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nw=co.getActiveNetworkInfo();
        return (nw!=null && nw.isConnected());



    }
}
