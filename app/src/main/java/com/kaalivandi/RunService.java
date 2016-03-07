package com.kaalivandi;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nandhu on 6/3/16.
 */
public class RunService extends Service {
    SharedPreferences settings;
    private static final String TAG = "KVService";
    public static String SP_FILE = "Kaalivandi1.1";

    private boolean isRunning = false;
    private LocationManager locManager;
    private LocationListener locListener;
    private Location mobileLocation;
    private LocationListener passiveListener;
    public boolean isGPSon = false;
    public Location passiveLocation;
    String id;
    String londitude, latitude;
    Timer timer;

    public RunService() {
    }
    public void pop(String sm){
        Log.d(TAG, sm);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int startId, int stopid) {
        // Perform your long running operations here.
        Toast.makeText(this, " Kaalivandi Service Started", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Service onStartCommand");
        try {
            locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        } catch (Exception e) {

        }

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR

        final Handler handler = new Handler();
        isGPSon = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        // send a broadcast to the widget.
                        if (isGPSon) {
                            //gps is on
                            buttonGetLocationClick();


                            Log.i(TAG, "Service running");
                        } else {
                            //GPS is off
                            // Toast.makeText(RunService.this,"Please Switch on GPS for Positioning",Toast.LENGTH_LONG).show();
                            buttonGetLocationClick();
                            final Handler handler1 = new Handler();
                            Timer ti = new Timer();
                            TimerTask task1 = new TimerTask() {
                                @Override
                                public void run() {
                                    handler1.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RunService.this, "Please swtich on GPS Kaalivandi", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            };
                            ti.scheduleAtFixedRate(task1, 50000, 300000);
                            Log.i(TAG, "Service running");

                        }


                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, 10000); // Executes the task every 5 seconds.


        return startId;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, " Kaalivandi Service Destroyed", Toast.LENGTH_LONG).show();
        isRunning = false;
        timer.cancel();


    }


    private void getCurrentLocation() {

        locListener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
                isGPSon = true;
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
                isGPSon = false;
            }

            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
                mobileLocation = location;
            }
        };
        passiveListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                passiveLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locListener);
        locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 5000, 10, passiveListener);

        //Location Listenere for passive

    }

    private void buttonGetLocationClick() {
        getCurrentLocation(); // gets the current location and update mobileLocation variable

        if (mobileLocation != null) {

            sendLocation(mobileLocation);
        } else {
            pop("Mobile Location  null");
            if (passiveLocation != null) {
                sendLocation(passiveLocation);
            } else {
                //do nothing
                pop("Mobile Location  null");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                pop("Sending last Known Location");
                sendLocation(locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
            }
        }



    }



    private String readFromSharedPrefs() {
        try {
            SharedPreferences sp =getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
            String id = sp.getString("id", "NUll");
            return id;

        }
        catch (Exception e){
            return  "0";
        }

    }
    public void sendLocation(Location mLocation) {
        URL murl = null;
        try {
            murl = prepareURL(mLocation);
        } catch (Exception e) {
        }
        new Postexecute().execute(murl);

    }
    private URL prepareURL(Location mLocation) throws MalformedURLException {

        double lat = mLocation.getLatitude();
        double lon = mLocation.getLongitude();
        String id=readFromSharedPrefs();
        String url = "http://Kaalivandi.com/Kaalivandi/UpdateDriverLocation?id="+id+"&latitude="+lat+"&longitude="+lon;
        pop("hitting "+url);

        return new URL(url);

    }
    private class Postexecute extends AsyncTask<URL, Void, Void> {


        private boolean HostError;

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param aVoid The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (HostError){
                // data is not available


            }
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(URL... params)  {



            HttpURLConnection urlConnection = null;
            try {

                urlConnection = (HttpURLConnection) params[0].openConnection();

                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                pop( "Connection made, Kaalivandi Service");
                int status = urlConnection.getResponseCode();
                InputStream in=urlConnection.getInputStream();
                in.close();
                //Input stream
               pop(" Kaalivandi Service :status Returned" + status);


                if (status == 200) {

                    //  InputStream in=urlConnection.getInputStream();
                    // pasreInput(in);
                   pop("easy as pie");
                    // Snackbar.make(mView, "Sent to network", Snackbar.LENGTH_SHORT).show();
                }
                // if(status==){}
                // if (status==){}
                //if(status==){}
                //if (status==){}
                //if(status==){}
                //if(status==){}

                else {
                   pop("SOme shit happened:" + status);
                }
            } catch (UnknownHostException e) {
                Log.d(TAG, "Exception :Unknown Host");
                HostError=true;

            }
            catch (NullPointerException e){
                Log.d(TAG, "Null pointer Exception");
            }
            catch (Exception e){
                Log.d(TAG,"other Exception"+e.getLocalizedMessage());
            }
            finally {
                try {
                    urlConnection.disconnect();
                }
                catch (Exception e){
                    Log.d(TAG,"Exception in finally");
                }
            }
            return null;


        }


    }
}

