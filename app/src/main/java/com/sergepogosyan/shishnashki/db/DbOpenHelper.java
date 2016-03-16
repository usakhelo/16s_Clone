package com.sergepogosyan.shishnashki.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DbOpenHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "sixteen_tiles.db";
  private static final int DATABASE_VERSION = 8;
  private static final String TAG = "shishnashki database";

  static {
    // register our models
    cupboard().register(Player.class);
  }

  public DbOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // this will ensure that all tables are created
    cupboard().withDatabase(db).createTables();
    // add indexes and other database tweaks
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // this will upgrade tables, adding columns and new tables.
    // Note that existing columns will not be converted
    Log.i(TAG, "onUpgrade: called");
    cupboard().withDatabase(db).dropAllTables();
    cupboard().withDatabase(db).createTables();
//        cupboard().withDatabase(db).upgradeTables();
    // do migration work
  }
}
