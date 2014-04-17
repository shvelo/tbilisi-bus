package com.tbilisi.bus.data;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "tbilisiBus.db";
    private static final int DATABASE_VERSION = 2;

    // Database Access Objects
    public Dao<BusStop, Integer> busStopDao;
    public Dao<HistoryItem, Integer> historyItemDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        try {
            busStopDao = getDao(BusStop.class);
            historyItemDao = getDao(HistoryItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, BusStop.class);
            TableUtils.createTable(connectionSource, HistoryItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, BusStop.class, true);
            TableUtils.dropTable(connectionSource, HistoryItem.class, true);
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearTable(Class dataClass) {
        try {
            TableUtils.dropTable(connectionSource, dataClass, true);
            TableUtils.createTable(connectionSource, dataClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}