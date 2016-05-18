
package com.anp.bdmt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Jung-Hum CHo Created by anp on 14. 12. 26..
 */
public class DataBaseOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 99;

    public static final String DATABASE_FILE = "cart.db";

    public static final String TABLE_NAME = "cart";

    public DataBaseOpenHelper(Context context) {
        super(context, DATABASE_FILE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(TABLE_NAME);
        sql.append(" (_id INTEGER PRIMARY KEY AUTOINCREMENT, ");

        sql.append("shop_code VARCHAR(50), ");
        sql.append("shop_name VARCHAR(50), ");
        sql.append("shop_phone VARCHAR(20), ");
        sql.append("shop_vphone VARCHAR(20), ");

        sql.append("menu_code VARCHAR(50), ");
        sql.append("menu_name VARCHAR(255), ");

        sql.append("price INTEGER, ");
        sql.append("ea INTEGER);");

        db.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
