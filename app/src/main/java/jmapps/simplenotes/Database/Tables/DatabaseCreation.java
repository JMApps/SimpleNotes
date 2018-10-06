package jmapps.simplenotes.Database.Tables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import jmapps.simplenotes.R;

public class DatabaseCreation extends SQLiteOpenHelper {

    private static final String DBName = "simpleNotes.DB";
    private static final int DBVersion = 2;

    public DatabaseCreation(Context context) {
        super(context, DBName, null, DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(MainTable.createMainItemTable);
        database.execSQL(CategoryTable.createCategoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(R.string.drop_table_if_exists + MainTable.mainTable);
        database.execSQL(R.string.drop_table_if_exists + CategoryTable.categoryTable);
        onCreate(database);
    }
}