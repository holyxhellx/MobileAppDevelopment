package tingle.tingle;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.google.android.gms.internal.zzir.runOnUiThread;

public class TingleFragment extends Fragment {

    //Location variables
    //TODO: Gather mLocation from TingleFragment and myListFragment to a class method call in a separate thread
    public Location mLocation;
    public double mlatitudeNetwork, mlatitudeGPS, mlongitudeNetwork, mlongitudeGPS;
    //Adjusting the model to save battery and data exchange
    final int MIN_TIME_BW_UPDATES = 0;
    final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    //Scan Value and API configuation:
    private static final String API_KEY = "2d8ba8811cb2204e305c332027d26ce2";
    public String scanformat;
    public String scanValue;
    public String scanInfo;

    //GUI variables
    private Button addThing, deleteThing; //import android.widget.Button;
    private TextView lastAdded, scanDesc;; //import android.widget.TextView;
    private TextView newWhat, newWhere; //import android.widget.TextView;

    //private ArrayList<Thing> thingsDB
    private static ThingsDB thingsDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thingsDB = ThingsDB.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tingle, parent, false);

        //thingsDB = new ArrayList<Thing>(); //Earlier version setup
        thingsDB = ThingsDB.get(getActivity());

        //Accessing GUI element
        lastAdded = (TextView) v.findViewById(R.id.last_thing);
        scanDesc = (TextView) v.findViewById(R.id.textView);
        updateUI();

        /*
         *  Textfields for describing a thing
         *  layout: hide the keyboard if the user click outsite the edittext area
         */
        newWhat = (EditText) v.findViewById(R.id.what_text);
        newWhat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        // Textfields for describing a thing - should hide the keyboard if the user click outsite the edittext area
        newWhere = (EditText) v.findViewById(R.id.where_text);
        newWhere.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        /*
         *   LocationManager:
         */
        mLocation = getLocation();

        /*
         *  QR / Bar code scanner:
         */
        try {
            Button scanner = (Button) v.findViewById(R.id.scanner);
            scanner.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                    startActivityForResult(intent, 0);
                }
            });

            Button scanner2 = (Button) v.findViewById(R.id.scanner2);
            scanner2.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    startActivityForResult(intent, 0);
                }

            });

        } catch (ActivityNotFoundException anfe) {
            Log.e("onCreate", "Scanner Not Found", anfe);
        }


        addThing = (Button) v.findViewById(R.id.add_button);
        addThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean existIn = false;
                //Ensure the user has made enough inputs to add an element
                if ((newWhat.getText().toString().trim().length() > 0) && (newWhere.getText().toString().trim().length() > 0)) {
                    //Ensure the element does not exist in the thingsDB already
                    for (int i = 0 ; i < thingsDB.size() ; i++ ) {
                        if (newWhat.getText().toString().trim().equals((thingsDB.get(i)).getWhat().toString())) {
                            existIn = true;
                            ExistInThingDB(thingsDB.get(i));
                            break;
                        }
                    }
                    //Add element to ThingsDB:
                    if (existIn == false) {
                        addThingToThingDB();
                    }
                //Invalid input for this operation
                // TODO: Use "Toast" to tell the user which values does not meet the input-requirements
                } else {
                    invalidInput();
                    //Log.i("AddButton", "invalidInput Reach");
                }
            }
        });

        deleteThing = (Button) v.findViewById(R.id.delete_button);
        deleteThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Ensure the user has made enough inputs to add an element
                if ((newWhat.getText().toString().trim().length() > 0) && (newWhere.getText().toString().trim().length() > 0)) {
                    for (int i = 0 ; i < thingsDB.size() ; i++ ) {
                        //Ensure the element does exist in the thingsDB already
                        boolean exist = newWhat.getText().toString().trim().equals((thingsDB.get(i)).getWhat().toString());
                        if (exist) {
                            deleteThingOfThingsDB(thingsDB.get(i));
                            break;
                        }
                    }
                //Invalid input for this operation
                // TODO: Use "Toast" to tell the user which values does not meet the input-requirements
                } else {
                    invalidInput();
                    Log.i("DeleteButton", "invalidInput Reach");
                }
            }
        });
        return v;
    }

    /**
     * @param view
     * This method hides the softKeyboard if the user click outside the area in focus
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * @param thing
     * This method shows a toast with the information "where" if the added item already exist
     */
    private void ExistInThingDB(Thing thing) {
        String message =    "Item exist: " + thing.getWhat().toString() + "\n"+
                            "location: " + thing.getWhere().toString();
        Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();  //Duration and show on screen
    }

    /**
     * @param thing
     * This method deletes a thing from the thingsDB
     */
    private void deleteThingOfThingsDB(Thing thing) {
        thingsDB.deleteThing(thing);
        String message = "Item delete: " + newWhat.getText().toString();
        Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();
        newWhat.setText("");
        newWhere.setText("");
        updateUI();
        updateFragments("list");
    }

    /**
     * This method adds a new thing to the thingDB
     */
    private void addThingToThingDB() {

        //Setting up the latitude and longitude values: (default)
        //String[] latLng = "-34.8799074,174.7565664".split(",");
        //double latitude = Double.parseDouble(latLng[0]);
        //double longitude = Double.parseDouble(latLng[1]);
        //LatLng location = new LatLng(latitude, longitude);

        double latitude = mLocation.getLatitude();
        double longitude = mLocation.getLongitude();
        LatLng location = new LatLng(latitude, longitude);

        thingsDB.addThing(new Thing(newWhat.getText().toString().trim(), newWhere.getText().toString().trim(), location));
        String message = "Item added: " + newWhat.getText().toString() + "\n"+"location: " + newWhere.getText().toString();
        Toast.makeText(getActivity(), message ,Toast.LENGTH_SHORT).show();
        newWhat.setText("");
        newWhere.setText("");
        updateUI();
        updateFragments("list");
    }

    /**
     * This method shows a toast if the input is not valid
     */
    private void invalidInput() {
        Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_SHORT).show();
    }

    /**
     * This method fill the lastAdded Textview with the data lastadded element from thingDB
     */
    private void updateUI() {
        int s = thingsDB.size();
        if(s > 0)  {
            try {
                lastAdded.setText(thingsDB.get(s-1).toString());
            }
            catch (NullPointerException update) {
                Log.e("method: updateUI", "widget.TextView not found");
            }
        }
    }

   /**
    *   This method collect location by network and gps in the named ranked order.
    *   Manifest:
    *   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    *   Sources:
    *   http://developer.android.com/guide/topics/location/strategies.html
    *   http://stackoverflow.com/questions/33065177/how-to-get-cooridates-from-gps-using-android-studio
    */
    public Location getLocation() {
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        try {
            // Acquire a reference to the system Location Manager (In a fragment this = getActivity().getSystemService)
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network or gps provider enabled
                Toast.makeText(getActivity(), "need Network or GPS enabled",  Toast.LENGTH_LONG).show();
            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            mlatitudeNetwork = mLocation.getLatitude();
                            mlongitudeNetwork = mLocation.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (mLocation == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                mlatitudeGPS = mLocation.getLatitude();
                                mlongitudeGPS = mLocation.getLongitude();
                            }
                        }
                    }
                }
            }
            scanDesc.append("\n" + "LatitudeNetwork: " + mlatitudeNetwork);
            scanDesc.append("\n" +"LongitudeNetwork: " +mlongitudeNetwork);
            scanDesc.append("\n" +"LatitudeGPS: " +mlatitudeGPS);
            scanDesc.append("\n" +"LongitudeGPS: " +mlongitudeGPS);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mLocation;
    }

    /*
     * Activity Result Scanner replay method
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                // Handle successful scan (toast)
                Toast toast = Toast.makeText(getActivity(), "Content:" + contents + " Format:" + format , Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();

                // Collect format and value for websearch:
                scanformat = format;
                scanValue = contents;
                // Handle successful scan (desc text - show to user)
                scanDesc.setText("Format:" + format + "\n");
                scanDesc.append("Value:" + contents);

               /*
                *  NetworkManager: bar-Code
                */
                try {
                    networkFetcher();
                } catch (IOException e) {
                    Log.e("NetworkFetcher()", "IOException", e);
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
                Toast toast = Toast.makeText(getActivity(), "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    }

    /*
    *  source: http://www.tutorialspoint.com/android/android_network_connection.htm
    *   Remember these two permission in the manifest:
    *   <uses-permission android:name="android.permission.INTERNET"/>
    *   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    */
    public void networkFetcher() /*extends AsyncTask*/ throws IOException {
        //Check Network Connection:
        ConnectivityManager check = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = check.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) System.out.println("Network is enabled");
        else System.out.println("Network is disabled");

        //API Operation:
            //Open website:     https://www.outpan.com/developers
            //Get product info: https://api.outpan.com/v2/products/[GTIN]?apikey=[YOUR API KEY]
            //Create user:      https://www.outpan.com/register.php
            //Collect user API: https://www.outpan.com/settings?s=api-key     2d8ba8811cb2204e305c332027d26ce2
            //API:              https://api.outpan.com/v2/products/[Insert]?apikey=2d8ba8811cb2204e305c332027d26ce2
        final String link = "https://api.outpan.com/v2/products/"+scanValue+"?apikey="+API_KEY;
        Log.d ("TingleFragment API-link", link);
        URL url = new URL(link); // add: "throws MalformedURLException" to method
        scanInfo = null; //hard reset of scanInfo

        //Open connection:
        final HttpURLConnection con = (HttpURLConnection) url.openConnection(); //add: "throws IOException" to method

        //New thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    InputStream in = con.getInputStream();
                    if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException(con.getResponseMessage() + ": with " + link);
                    }

                    int bytesRead = 0;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = in.read(buffer)) > 0) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.close();
                    String output = new String(out.toByteArray(), "UTF-8"); //Convert ByteArrayOutputStream to String with character encoding
                    String[] sArrayOutput = output.split("\n");
                    boolean dataFound = false;
                    for (int i = 0; i < sArrayOutput.length ; i++) {
                        if (sArrayOutput[i].contains("name")) {
                            dataFound = true;
                            String[] sOutput = sArrayOutput[i].replaceAll("\"", "").split(":");
                            if (sOutput.length > 1) {
                                for(int j = 2; j < sOutput.length ; j++) {
                                    sOutput[1] = sOutput[1].concat(sOutput[j]);
                                }
                                scanInfo = sOutput[1].trim();
                            } else scanInfo = sOutput[1].trim();

                            //Run on main-thread: set text in newWhat
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Write the collected info into the fragment-view
                                    newWhat.setText(scanInfo);
                                }
                            });
                        }
                    }
                    if (dataFound = false) {
                        //Run on main-thread: set text in newWhat
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Write the collected info into the fragment-view
                                //TODO: Insert other API connection if not result on name:
                                newWhat.setText("No name found!");
                            }
                        });
                    }
                } catch (Exception e) {e.printStackTrace();
                } finally {con.disconnect();}
            }
        });
        thread.start();
    }

    /**
     * @param string
     * This method update specific fragment(s), pending on string input value, and is done by
     * "remove" fragment and then again "add" fragment to the activity
     */
    public void updateFragments(String string) {
        //Get the SupportFragmentManager
        FragmentManager manager = getActivity().getSupportFragmentManager();

        //Get the fragment (fragment_list & fragment_tingle)
        Fragment fragment_tingle = manager.findFragmentById(R.id.fragmentContainer_tingle);
        Fragment fragment_list = (myListFragment) manager.findFragmentById(R.id.fragmentContainer_list);

        if (string.equals("tingle")) {
            manager.beginTransaction()
                    .remove(fragment_list)
                    .commit();

            fragment_tingle = new Fragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_tingle, fragment_tingle)
                    .commit();
        }
        else if(string.equals("list")) {
            //Use the manager to begin transaction, and remove the fragment
            manager.beginTransaction()
                    .remove(fragment_list)
                    .commit();

            //Create a new Fragment (myListFragment)
            fragment_list = new myListFragment();
            //Use the manager to begin transaction, and add the new(updated) fragment
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_list, fragment_list)
                    .commit();

        }
        else if(string.equals("all")) {
            manager.beginTransaction()
                    .remove(fragment_list)
                    .remove(fragment_tingle)
                    .commit();

            fragment_tingle = new Fragment();
            fragment_list = new myListFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer_tingle, fragment_tingle)
                    .add(R.id.fragmentContainer_list, fragment_list)
                    .commit();
        }
    }
}