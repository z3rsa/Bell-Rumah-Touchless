package com.example.bel_touchless.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {Data.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DataDao dataDao();

    private static AppDatabase INSTANCE;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE data " + " ADD COLUMN Waktu TEXT DEFAULT ''");
        }
    };

    public static AppDatabase getDbInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "data")
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return INSTANCE;
    }
}
