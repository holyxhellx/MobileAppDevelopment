package tingle.tingle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import tingle.tingle.database.TingleBaseHelper;
import tingle.tingle.database.TingleCursorWrapper;
import tingle.tingle.database.TingleDbSchema;
import tingle.tingle.database.TingleDbSchema.TingleTable;

public class ThingsDB {

    private static ThingsDB sThingsDB;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ThingsDB get(Context context) {
        if (sThingsDB == null) {
            sThingsDB = new ThingsDB(context);
        }
        return sThingsDB;
    }

    //Fill database for testing purposes
    private ThingsDB(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TingleBaseHelper(mContext).getWritableDatabase();

        //This comment out after first run:
        /*
        addThing(new Thing("Android Phone", "Desk"));
        addThing(new Thing("Big Nerd book", "Desk"));
        addThing(new Thing("T-shirt", "Locker"));
        addThing(new Thing("Bag", "Floor"));
        addThing(new Thing("Coffeecup", "Washer"));
        addThing(new Thing("Pensel", "Case"));
        */
    }

    public void addThing(Thing thing) {
        ContentValues values = getContentValues(thing);
        mDatabase.insert(TingleDbSchema.TingleTable.NAME, null, values);
        System.out.println("adding to the database....");
    }

    public List<Thing> getThingsDB() {
        List<Thing> things = new ArrayList<>();
        TingleCursorWrapper cursor = queryThings(null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            things.add(cursor.getThing());
            cursor.moveToNext();
        }
        cursor.close();
        return things;
    }

    public void updateThing(Thing thing) {
        String what  = thing.getWhat().toString();
        ContentValues values = getContentValues(thing);

        mDatabase.update(TingleTable.NAME, values, TingleTable.Cols.WHAT + " = ?" , new String[] {what});
    }

    public static ContentValues getContentValues(Thing thing) {
        ContentValues values = new ContentValues();
        values.put(TingleTable.Cols.WHAT, thing.getWhat());
        values.put(TingleTable.Cols.WHERE, thing.getWhere());
        return values;
    }

    private TingleCursorWrapper queryThings(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TingleTable.NAME,
                null,
                whereClause, whereArgs,
                null,
                null,
                null
        );
        return new TingleCursorWrapper(cursor);
    }

    /*
    public Thing get(int i) {
    //Write a query to select values from the database
        db.execSQL("SELECT ROW_NUMBER("+i+")");
        ContentValues values = getContentValues(thing);
        values.put(TingleDbSchema.TingleTable.Cols.WHAT, thing.getWhat());
        values.put(TingleDbSchema.TingleTable.Cols.WHERE, thing.getWhere());
        //return mThing;
        //return mThingsDB.get(i);
    }
    */

}
