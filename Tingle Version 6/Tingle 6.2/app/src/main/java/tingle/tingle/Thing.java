package tingle.tingle;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class Thing {
        //Data fields
        private String mWhat = null;
        private String mWhere = null;
        private LatLng mLocation = null;

        //Constructor
        public Thing(String what, String where, LatLng location) {
            mWhat = what;
            mWhere = where;
            mLocation = location;
        }

        //Methods
        @Override
        public String toString() {
            return oneLine("Item: ","Location: ");
        }
        public String getWhat() {
            return mWhat;
        }
        public void setWhat(String what) {
            mWhat = what;
        }
        public String getWhere() {
            return mWhere;
        }
        public void setWhere(String where) {
            mWhere = where;
        }
        public String oneLine(String pre, String post) {
            return pre + mWhat + "\n"+ post + mWhere;
        }

        //Location method implemenation:
        //Source: https://developers.google.com/android/reference/com/google/android/gms/maps/model/LatLng#inherited-constant-summary
        public LatLng getLocation() {
            return mLocation;
        }
        public void setLocation(LatLng location) {
            mLocation = location;
        }
}

