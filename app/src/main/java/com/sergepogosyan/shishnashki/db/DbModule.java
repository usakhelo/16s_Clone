package com.sergepogosyan.shishnashki.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;


public class DbModule {

    // We suggest to keep one instance of StorIO (SQLite or ContentResolver)
    // It's thread safe and so on, so just share it.
    // But if you need you can have multiple instances of StorIO
    // (SQLite or ContentResolver) with different settings such as type mapping, logging and so on.
    // But keep in mind that different instances of StorIOSQLite won't share notifications!
//    StorIOSQLite storIOSQLite = DefaultStorIOSQLite.builder()
//        .sqliteOpenHelper(someSQLiteOpenHelper)
//        .addTypeMapping(Tweet.class, SQLiteTypeMapping.<Tweet>builder()
//            .putResolver(new TweetPutResolver()) // object that knows how to perform Put Operation (insert or update)
//            .getResolver(new TweetGetResolver()) // object that knows how to perform Get Operation
//            .deleteResolver(new TweetDeleteResolver())  // object that knows how to perform Delete Operation
//            .build())
//        .build(); // This instance of StorIOSQLite will know how to work with Tweet objects

    @NonNull
    public SQLiteOpenHelper provideSQLiteOpenHelper(@NonNull Context context) {
        return new DbOpenHelper(context);
    }
}
