package ru.zzsdeo.mruapps;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StatisticTable {

    public static final String TABLE_NAME = "statistic";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PACKAGE_NAME = "package_name";
    public static final String COLUMN_USAGE = "usage";
    public static final String COLUMN_IGNORE = "ignore";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PACKAGE_NAME + " text not null, "
            + COLUMN_USAGE + " integer not null, "
            + COLUMN_IGNORE + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(StatisticTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}