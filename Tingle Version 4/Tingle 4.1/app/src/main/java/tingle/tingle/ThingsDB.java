package tingle.tingle;

import android.content.Context;

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
        mThingsDB = new ArrayList<Thing>();
        mThingsDB.add(new Thing("Android Phone", "Desk"));
        mThingsDB.add(new Thing("Big Nerd book", "Desk"));
        mThingsDB.add(new Thing("T-shirt", "Locker"));
        mThingsDB.add(new Thing("Bag", "Floor"));
        mThingsDB.add(new Thing("Coffeecup", "Washer"));
        mThingsDB.add(new Thing("Pensel", "Case"));

        //Fill the list (testing)
        //Testing with 1000 items in list does not trigger "Skipped frames" - but 10000 items (bad)
        /*
        for (int i = 0; i < 1000 ; i++) {
            String what = "TingleApp" +i;
            String where = "Aud2" +i;
            mThingsDB.add(new Thing(what, where));
        }
        */
    }
}
