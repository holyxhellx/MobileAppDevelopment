package tingle.tingle;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class myListFragment extends ListFragment {

    //Location variables
    //TODO: Gather mLocation from TingleFragment and myListFragment to a class method call in a separate thread
    public Location mLocation;
    public double mlatitudeNetwork, mlatitudeGPS, mlongitudeNetwork, mlongitudeGPS;
    //Adjusting the model to save battery and data exchange
    final int MIN_TIME_BW_UPDATES = 0;
    final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    //Defining button
    private Button searchButton;
    private TextView newWhat;

    //Declaring Array Adapter
    ArrayAdapter<String>  mWhatItem;

    //Defining ListView (Search-mode)
    private ListView mListView;

    //Element that will be displayed in ListView
    String[] mWhat = new String[0];

    //Collect Singleton Class
    private static ThingsDB thingsDB;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Declaring Array adapter
        ArrayAdapter<String>  mWhatItem = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mWhat);

        //Setting the android ListView´s adaptor to the newly created adaptor
        setListAdapter(mWhatItem);

        //ListView OnItemClicListener and action
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
            for (int j = 0; j < thingsDB.size(); j++) {
                Thing thing = thingsDB.get(j);
                boolean isTrue = mWhat[position].toString().equals(thing.getWhat().toString());
                if (isTrue) {
                    //distance calculation:
                    // Source: http://www.movable-type.co.uk/scripts/latlong.html
                    // TODO: Futher work on distance calculations: conveter LatLng to km
                    double dist = 0.0;
                    double mlatitude = mLocation.getLatitude(); //myLocation
                    double mlongitude = mLocation.getLongitude(); //myLocation
                    LatLng thingLocation = thingsDB.get(j).getLocation();
                    double tlatitude = thingLocation.latitude; //itemLocation
                    double tlongitude = thingLocation.longitude; //itemLocation

                    dist = distLocation(mlatitude, mlongitude, tlatitude, tlongitude);

                    if (dist < 1) {
                        dist = dist*1000;
                        int distRound = (int) dist;
                        Toast.makeText(getActivity(), "Item located: " + thing.getWhere().toString() + "\n" +
                                "Coordinates: " + thing.getLocation()+ "\n" +"Distance: " +distRound+" m", Toast.LENGTH_SHORT).show();
                    } else {
                        int distRound = (int) dist;
                        Toast.makeText(getActivity(), "Item located: " + thing.getWhere().toString() + "\n" +
                                "Coordinates: " + thing.getLocation() + "\n" + "Distance: " + distRound + " km", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, parent, false);

        //Accessing GUI elements
        mListView = (ListView) v.findViewById(android.R.id.list);
        newWhat = (EditText) v.findViewById(R.id.what_text);

        // Textfields for describing a thing - should hide the keyboard if the user click outsite the edittext area
        newWhat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        //Accessing Singleton class
        thingsDB = ThingsDB.get(getActivity());

        /*
         *   LocationManager:
         */
        mLocation = getLocation();

        //Argument pass and set textfield
        String txt = "";
        try {
            txt = getArguments().getString("SEARCH_TEXT").toString();
            Log.d("myListFragment input: ", txt);
            newWhat.setText(txt);
        } catch (NullPointerException e) { e.printStackTrace(); }


        //Collecting all element from the ThingsDB
        //TODO: if (txt != "") else etx...
        //Reset the list of items
        mWhat = new String[0];
        int count = 0;

        //Collect the searched items (100% match)
        for (int i = 0; i < thingsDB.size(); i++) {
            Thing thing = thingsDB.get(i);
            boolean exist = newWhat.getText().toString().trim().toLowerCase().equals(thing.getWhat().toString().toLowerCase());
            if (exist) {
                String What = thing.getWhat();
                mWhat = appendToList(mWhat, What);
                count++;
            }
        }

        //Collect the searched items (dist. 1-3 match)
        if (count == 0) count = collectSimilarWords(1, count);

        if (count == 0) count = collectSimilarWords(2,count);

        if (count == 0) count = collectSimilarWords(3,count);

        if (count == 0) mWhat = appendToList(mWhat, "Non words found:");

        //Sort the searched results after location
        if (mWhat.length > 1) mWhat = sort(mWhat);

        //Declaring Array adapter
        mWhatItem = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mWhat);
        //Setting the android ListView´s adaptor to the newly created adaptor
        mListView.setAdapter(mWhatItem);

        //This is the Search action
        searchButton = (Button) v.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set search txt to new value:
                // TODO: Does not work with activity call - txt
                //((ListActivity)getActivity()).txt = newWhat.getText().toString().trim();

                //Reset the list of items
                mWhat = new String[0];
                int count = 0;

                if (newWhat.getText().toString().trim().length() == 0) {
                    //Collecting all element from the ThingsDB
                    // TODO: Since the ThingsDB in this version only accepts unique things from the add method, this is a bit overkill!
                    for (int i = 0; i < thingsDB.size(); i++) {
                        Thing thing = thingsDB.get(i);
                        String What = thing.getWhat();
                        mWhat = appendToList(mWhat, What);
                        count++;
                    }
                }

                //Collect the searched items
                for (int i = 0; i < thingsDB.size(); i++) {
                    Thing thing = thingsDB.get(i);
                    boolean exist = newWhat.getText().toString().trim().toLowerCase().equals(thing.getWhat().toString().toLowerCase());
                    if (exist) {
                        String What = thing.getWhat();
                        mWhat = appendToList(mWhat, What);
                        count++;
                    }
                }

                if (count == 0) count = collectSimilarWords(1, count);

                if (count == 0) count = collectSimilarWords(2,count);

                if (count == 0) count = collectSimilarWords(3, count);

                if (count == 0) mWhat = appendToList(mWhat, "Non words found:");

                if (count != 0 && mWhat.length > 1) mWhat = sort(mWhat);

                //Declaring Array adapter
                mWhatItem = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mWhat);
                //Setting the android ListView´s adaptor to the newly created adaptor
                mListView.setAdapter(mWhatItem);
            }
        });
        return v;
    }
    /**
     * @param list
     * This method sorts the input list according to dist from mLocation
     */
    private String[] sort(String[] list) {
        //TODO: Solve 1 : 1 relation in map (things with the same distance in radius will give errors)
        Map<Double, String> map = new HashMap<Double, String>();
        String[] listSorted = new String[0];
        for (int i = 0 ; i < list.length ; i++) {
            double dist = 0.0;

            double mlatitude = mLocation.getLatitude(); //myLocation
            double mlongitude = mLocation.getLongitude(); //myLocation

            for (int j = 0; j < thingsDB.size(); j++) {
                Thing thing = thingsDB.get(j);
                boolean isTrue = list[i].equals(thing.getWhat().toString());
                if (isTrue) {
                    LatLng thingLocation = thingsDB.get(j).getLocation();
                    double tlatitude = thingLocation.latitude; //itemLocation
                    double tlongitude = thingLocation.longitude; //itemLocation

                    dist = distLocation(mlatitude, mlongitude, tlatitude, tlongitude);
                    map.put(dist, list[i]);
                }
            }
        }
        for (int i = 0 ; i < list.length ; i++) {
            double min = Collections.min(map.keySet());
            System.out.println("min: "+min);
            String listItem = map.get(min);
            listSorted = appendToList(listSorted, listItem);
            System.out.println("sizeBefore: "+map.size());
            map.remove(min);
            System.out.println("sizeAfter: " + map.size());
        }
        return listSorted;
    }

    /**
     * @param mlat
     * @param mlng
     * @param tlat
     * @param tlng
     * This method calculate the distance between two locations (+/- 5% distance variation)
     * TODO: Ref. a lib instead or use googleMaps to collect the distance (return method)
     * http://andrew.hedges.name/experiments/haversine/
     */
    private double distLocation(double mlat, double mlng, double tlat, double tlng) {
        /*
        *  Calculations using LatLng:
        *  double distLatLng =   Math.abs(Math.sqrt(Math.pow((tlat -mlat), 2) +Math.pow((tlng - mlng), 2)));
        */
        double R = 6371; //radius of the earth (km)
        double dlon = Math.abs(mlng - tlng);
        double dlat = Math.abs(mlat - tlat);
        double a = Math.pow(Math.sin(dlat/2), 2) + Math.cos(mlat) * Math.cos(tlat) * Math.pow(Math.sin(dlon/2), 2);
        double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) );
        double dist = R * c;
        return dist;
    }
    /**
     *
     * @param dist
     * @param count
     * @return
     * This method collects similar words and increase the count if any match are find with the distance equal to dist.
     */
    private int collectSimilarWords(int dist, int count) {
        //Collect the newWhat from user input
        String a = newWhat.getText().toString().trim();
        //Collect all newWhat from DB
        for (int i = 0; i < thingsDB.size(); i++) {
            Thing thing = thingsDB.get(i);
            String b = thing.getWhat().toString();
            if (distance(a, b) == dist) {
                mWhat = appendToList(mWhat, b);
                count++;
            }
        }
        return count;
    }

    /**
     * @param a
     * @param b
     * This method uses the Levenshtein method to find the distance between two words
     * - This algorithm is borrowed from [http://rosettacode.org/wiki/Levenshtein_distance#Java]
     * - which is based on [http://www.codeproject.com/Articles/13525/Fast-memory-efficient-Levenshtein-algorithm]
     */
    private static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();

        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }

    /**
     *
     * @param set
     * @param element
     * @return
     * This method revives the total collection "set" and the element for which to append,
     * creates a new stringSet containing the element, and send the two sets to the method: merge()
     */
    private String[] appendToList(String[] set, String element) {
        //create an array containing the element
        String[] appendElement = new String[]{element};

        //add the String element and current
        String[] Set = merge(set, appendElement);
        return Set;
    }

    /**
     *
     * @param set
     * @param appendElement
     * @return
     * This method revives two stringSets and merges both together.
     */
    private String[] merge(String[] set, String[] appendElement) {
        int lenSet              = set.length;           //Collect the length of the array (set)
        int lenAppendElement    = appendElement.length; //Collect the length of the array (appendElement)

        //Create an array with the total length of both StringSet
        String[] merged = new String[lenSet + lenAppendElement];
        // Copy the first array (arr1) to merge from index 0 to len1.2
        System.arraycopy(set, 0, merged, 0, lenSet);
        // Copy the second array (arr2) to merge from index len1 to len 2.
        System.arraycopy(appendElement, 0, merged, lenSet, lenAppendElement);

        return merged;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mLocation;
    }

}
