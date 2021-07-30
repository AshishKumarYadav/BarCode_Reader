package com.ashish.idflow.database;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {ScanTable.class}, version = 1, exportSchema = true)
public abstract class DatabaseReference extends RoomDatabase {

    private static DatabaseReference INSTANCE;

    public abstract ScanDao dockDao();

    public static DatabaseReference getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatabaseReference.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatabaseReference.class, "scan_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigrationFrom(1)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
