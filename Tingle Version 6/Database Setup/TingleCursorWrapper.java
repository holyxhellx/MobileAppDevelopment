package tingle.tingle.database;

import android.database.Cursor;
import android.database.CursorWrapper;

//import java.util.Date;
//import java.util.UUID;

import tingle.tingle.Thing;
import tingle.tingle.database.TingleDbSchema.TingleTable;


public class TingleCursorWrapper extends CursorWrapper {
    public TingleCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Thing getThing() {
        //String uuidString = getString(getColumnIndex(TingleTable.Cols.UUID));
        //long date = getLong(getColumnIndex(TingleTable.Cols.DATE));
        String what = getString(getColumnIndex(TingleTable.Cols.WHAT));
        String where = getString(getColumnIndex(TingleTable.Cols.WHERE));

        Thing thing = new Thing(what, where);
        //thing.setDate(date);
        //UUID.fromString(uuidString);

        return thing;
    }
}
