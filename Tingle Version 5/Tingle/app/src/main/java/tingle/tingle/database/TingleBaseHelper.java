package tingle.tingle.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tingle.tingle.database.TingleDbSchema.TingleTable;

public class TingleBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "TingleBaseHelper";
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "TingleBase.db";

    public TingleBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TingleTable.NAME + "(" +
                        " _id integer primary key autoincrement, " +
                        //TingleTable.Cols.UUID + ", " +
                        //TingleTable.Cols.DATE + ", " +
                        TingleTable.Cols.WHAT + ", " +
                        TingleTable.Cols.WHERE+ ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
