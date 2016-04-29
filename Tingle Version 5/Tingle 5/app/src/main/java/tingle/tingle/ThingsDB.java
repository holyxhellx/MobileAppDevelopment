package tingle.tingle;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ThingsDB {
    private static ThingsDB sThingsDB;

    private List<Thing> mThingsDB;

    public static ThingsDB get(Context context) {
        if (sThingsDB == null) {
            sThingsDB = new ThingsDB(context);
        }
        return sThingsDB;
    }

    public List<Thing> getThingsDB() {
        return mThingsDB;
    }
    public void addThing (Thing thing) {
        mThingsDB.add(thing);
    }
    public void deleteThing (Thing thing) {
        mThingsDB.remove(thing);
    }
    public int size() {
        return mThingsDB.size();
    }
    public Thing get(int i) {
        return mThingsDB.get(i);
    }

    //Fill database for testing purposes
    private ThingsDB (Context context) {

        //Setting up the latitude and longitude values:
        //https://developers.google.com/android/reference/com/google/android/gms/maps/model/LatLng#field-summary
        /*
            String[] latLng = "-34.8799174,104.7565664".split(",");
            double latitude = Double.parseDouble(latLng[0]);
            double longitude = Double.parseDouble(latLng[1]);
            LatLng location = new LatLng(latitude, longitude);
        */

        mThingsDB = new ArrayList<Thing>();

        for (int i = 0 ; i < 12 ; i++) {
            String what = "TingleApp" +i;
            String where = "IT: around the world" +i;
            double latitude = -34.8799074 + (i * 10);
            double longitude = 174.7565664 - (i * 10);
            LatLng location = new LatLng(latitude, longitude);
            mThingsDB.add(new Thing(what, where, location));
        }
        //mThingsDB.add(new Thing("Big Nerd book", "Desk", location));
        //mThingsDB.add(new Thing("T-shirt", "Locker", location));
        //mThingsDB.add(new Thing("Bag", "Floor", location));
        //mThingsDB.add(new Thing("Bas", "Floorlever", location));
        //mThingsDB.add(new Thing("Bak", "Lever", location));
        //mThingsDB.add(new Thing("Coffeecup", "Washer", location));
        //mThingsDB.add(new Thing("Pensel", "Case", location));
    }
}
