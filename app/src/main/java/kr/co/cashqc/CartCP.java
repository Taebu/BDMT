
package kr.co.cashqc;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * @author Jung-Hum CHo Created by anp on 14. 12. 26..
 */
public class CartCP extends ContentProvider {

    public static final String uriString = "content://kr.co.cashqc.CartCP";

    public static final Uri CONTENT_URI = Uri.parse(uriString);

    private SQLiteDatabase mDatabase;

    private DataBaseOpenHelper mHelper;

    private String TABLE_NAME = DataBaseOpenHelper.TABLE_NAME;

    @Override
    public boolean onCreate() {

        mHelper = new DataBaseOpenHelper(getContext());

        mDatabase = mHelper.getWritableDatabase();

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {

        SQLiteQueryBuilder sql = new SQLiteQueryBuilder();

        SQLiteDatabase db = mHelper.getReadableDatabase();

        sql.setTables(TABLE_NAME);

        Cursor cursor = sql.query(db, projection, selection, null, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        mDatabase = mHelper.getWritableDatabase();

        long rowId = mDatabase.insert(TABLE_NAME, "", values);

        if (rowId > 0) {

            Uri rowUri = ContentUris.appendId(CONTENT_URI.buildUpon(), rowId).build();

            getContext().getContentResolver().notifyChange(rowUri, null);

            return rowUri;

        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mDatabase.delete(TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return mDatabase.update(TABLE_NAME, values, selection, selectionArgs);
    }
}
