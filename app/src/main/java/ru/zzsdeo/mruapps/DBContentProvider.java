package ru.zzsdeo.mruapps;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class DBContentProvider extends ContentProvider {

    private DBHelper database;

    private static final int ALL_ROWS = 10;
    private static final int ROW_ID = 20;

    private static final String AUTHORITY = "ru.zzsdeo.mruapps.contentprovider";

    private static final String STATISTIC_PATH = "statistic";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + STATISTIC_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/list";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/item";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, STATISTIC_PATH, ALL_ROWS);
        sURIMatcher.addURI(AUTHORITY, STATISTIC_PATH + "/#", ROW_ID);
    }

    @Override
    public boolean onCreate() {
        database = new DBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case ALL_ROWS:
                queryBuilder.setTables(StatisticTable.TABLE_NAME);
                break;
            case ROW_ID:
                // Set the table
                queryBuilder.setTables(StatisticTable.TABLE_NAME);
                // adding the ID to the original query
                queryBuilder.appendWhere(StatisticTable.TABLE_NAME + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id;
        String path;
        switch (uriType) {
            case ALL_ROWS:
                id = sqlDB.insert(StatisticTable.TABLE_NAME, null, values);
                path = STATISTIC_PATH;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(path + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted;
        String id;
        switch (uriType) {
            case ALL_ROWS:
                rowsDeleted = sqlDB.delete(StatisticTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case ROW_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(StatisticTable.TABLE_NAME,
                            StatisticTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(StatisticTable.TABLE_NAME,
                            StatisticTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated;
        String id;
        switch (uriType) {
            case ALL_ROWS:
                rowsUpdated = sqlDB.update(StatisticTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ROW_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(StatisticTable.TABLE_NAME,
                            values,
                            StatisticTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(StatisticTable.TABLE_NAME,
                            values,
                            StatisticTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {
                StatisticTable.COLUMN_ID,
                StatisticTable.COLUMN_PACKAGE_NAME,
                StatisticTable.COLUMN_APP_NAME,
                StatisticTable.COLUMN_USAGE,
                StatisticTable.COLUMN_IGNORE
        };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
