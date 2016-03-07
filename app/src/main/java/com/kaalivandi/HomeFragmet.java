package com.kaalivandi;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * Created by nandhu on 6/3/16.
 */
public class HomeFragmet extends android.support.v4.app.Fragment {

    private View mView;
    public boolean iSGpsRunnning = false;
    public static String SP_FILE="Kaalivandi1.1";
    private LocationManager locationManager;
    LocationListener mListener;
    Button kaaliButton,busyButton;
    public Network mNetwork;


    /**
     * Default constructor.  <strong>Every</strong> fragment must have an
     * empty constructor, so it can be instantiated when restoring its
     * activity's state.  It is strongly recommended that subclasses do not
     * have other constructors with parameters, since these constructors
     * will not be called when the fragment is re-instantiated; instead,
     * arguments can be supplied by the caller with {@link #setArguments}
     * and later retrieved by the Fragment with {@link #getArguments}.
     * <p/>
     * <p>Applications should generally not implement a constructor.  The
     * first place application code an run where the fragment is ready to
     * be used is in {@link #onAttach(Activity)}, the point where the fragment
     * is actually associated with its activity.  Some applications may also
     * want to implement {@link #onInflate} to retrieve attributes from a
     * layout resource, though should take care here because this happens for
     * the fragment is attached to its activity.
     */
    public HomeFragmet() {

    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p/>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpLocation();
        mNetwork=new Network(getContext());

    }

    private void setUpLocation() {


        try {

            //get A handle to Location Manager
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            mListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {
                    iSGpsRunnning = true;
                }

                @Override
                public void onProviderDisabled(String provider) {
                    iSGpsRunnning = false;
                }
            };


            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            //  Request Updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, mListener);

        }
        catch (Exception e){
            //
        }




    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {@link Activity#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * tied to {@link Activity#onResume() Activity.onResume} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p/>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override





    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_main,container,false);
        pop("On Create View Hoom Fragment");



        kaaliButton=(Button)mView.findViewById(R.id.button2);

        busyButton=(Button)mView.findViewById(R.id.button3);

       String out= getContext().getSharedPreferences(SP_FILE, 0).getString("out", " ");
        if (out.equals("1")){
            kaaliButton.setClickable(false);
        }



        kaaliButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop("Kaalibutton CLick");
                startbuttonClick();
            }
        });
        busyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop("Busy button CLicked");
                stopButtonClick();
            }
        });
        return mView;

    }

    private void stopButtonClick() {
        if (mNetwork!=null){
            if (mNetwork.isOnline()){
                //online
                sendtoserver(false);


                getContext().stopService(new Intent(getContext(), RunService.class));
                Toast.makeText(getContext(), "Service is stoped", Toast.LENGTH_LONG).show();
                kaaliButton.setClickable(true);

                getContext().getSharedPreferences(SP_FILE,0).edit()
                        .putString("out", "0").commit();


            }
            else{
                //not online
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext()).setTitle("No Connection").setMessage("Please Switch on the data/Internet to stop being available").setInverseBackgroundForced(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent in = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                            startActivity(in);
                        }
                        catch (Exception e){

                        }

                    }
                });
                builder.show();

            }
        }

    }

    private void startbuttonClick() {

        pop("GPS VAIRBLE=" + iSGpsRunnning);
        if (!iSGpsRunnning) {
            //GPS is oFf
            showGPSdialog();
        }

        if (mNetwork != null) {

            //Network object exits
            if (mNetwork.isOnline()) {

                //Network is Online
                sendtoserver(true);
                getContext().startService(new Intent(getContext(), RunService.class));
                kaaliButton.setClickable(false);
                // getContext().startService(new Intent(getContext(), RnuGpsNet.class));


                getContext().getSharedPreferences(SP_FILE, 0).edit().putString("out", "1").commit();


            } else {
                //offline
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("No Internet")
                        .setMessage("Please Switch on the data/Internet")
                        .setInverseBackgroundForced(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    getContext().startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                                } catch (Exception e) {
                                    //DO nothing
                                }
                            }


                        });
                builder.show();
            }
        }
    }


    private void showGPSdialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext()).setTitle("Turn On Gps").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // sButton.setVisibility(View.VISIBLE);
                        try {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                        catch (Exception e){

                        }

                    }
                }

        ).

            setInverseBackgroundForced(true);

            builder.show();
        }

    private void sendtoserver(boolean b) {
        try {
            URL url = prepareString(b);
            new Postexecute().execute(url);

        }

        catch(Exception e){

        }
    }

    private URL prepareString(boolean b) {
        try {
            if (b) {
                //available
                String aval = "http://kaalivandi.com/kaalivandi/UpdateDriverAvailablity?id="+readFromSharedPrefs()+"&Availablity=1";
                pop("hitting   "+aval);
                return new URL(aval);

            } else {
                String nvavl = "http://kaalivandi.com/kaalivandi/UpdateDriverAvailablity?id="+readFromSharedPrefs()+"&Availablity=0";
                pop("hitting   "+nvavl);
                return new URL(nvavl);

            }

        } catch (Exception e) {
            Log.d("URL ", "URL EXCEPTION");

        }

        return null;


    }

    private String readFromSharedPrefs() {
        try {
            SharedPreferences sp = getContext().getSharedPreferences(MainActivity.SP_FILE, Context.MODE_PRIVATE);
            String id = sp.getString("id", "Null");
            // mIdTextView.setText("Your ID is  :" + id);
            //mIdTextView.setTextSize(18);
            return id;
        }
        catch (Exception e){
            return "0";
        }

    }



    public void pop(String sm){
        Log.i("Flow", sm);
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
              final Handler ha=new Handler();
               ha.post(new Runnable() {
                   @Override
                   public void run() {
                       pop("Inside Run method Home Fragment");
                   }
               });
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
                int status = urlConnection.getResponseCode();
                InputStream in=urlConnection.getInputStream();
                in.close();
                //Input stream


                if (status == 200) {

                    //  InputStream in=urlConnection.getInputStream();
                    // pasreInput(in);
                    pop("Suceess Fragment");
                    // Snackbar.make(mView, "Sent to network", Snackbar.LENGTH_SHORT).show();
                }
                // if(status==){}
                // if (status==){}
                //if(status==){}
                //if (status==){}
                //if(status==){}
                //if(status==){}

                else {
                }
            } catch (UnknownHostException e) {
                HostError=true;

            }
            catch (NullPointerException e){
            }
            catch (Exception e){
            }
            finally {
                try {
                    urlConnection.disconnect();
                }
                catch (Exception e){
                }
            }
            return null;


        }

    }

}
