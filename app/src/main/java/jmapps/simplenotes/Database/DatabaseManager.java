package jmapps.simplenotes.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import jmapps.simplenotes.Model.MainListModel;

public class DatabaseManager {

    private DatabaseHelper databaseHelper;
    private Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseManager(Context ctx) {
        context = ctx;
    }

    public void databaseOpen() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    public void databaseClose() {
        databaseHelper.close();
    }

    public void databaseInsert(String chapterTitle, String chapterContent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.chapterTitle, chapterTitle);
        contentValues.put(DatabaseHelper.chapterContent, chapterContent);
        sqLiteDatabase.insert(DatabaseHelper.tableName, null, contentValues);
    }

    public List<MainListModel> getMainList() {

        databaseOpen();

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.tableName,
                DatabaseHelper.columns,
                null,
                null, null, null, null);

        List<MainListModel> allMainList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                allMainList.add(new MainListModel(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.chapterTitle)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.chapterContent))));
                cursor.moveToNext();
            }
        }
        return allMainList;
    }

    public Cursor fetch() {
        String[] columns = new String[]{
                DatabaseHelper._ID,
                DatabaseHelper.chapterTitle,
                DatabaseHelper.chapterContent
        };
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.tableName, columns,
                null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void databaseUpdate(int _id, String chapterTitle, String chapterContent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper._ID, _id);
        contentValues.put(DatabaseHelper.chapterTitle, chapterTitle);
        contentValues.put(DatabaseHelper.chapterContent, chapterContent);
        sqLiteDatabase.update(DatabaseHelper.tableName, contentValues,
                DatabaseHelper._ID + "=" + _id, null);
    }

    public void databaseDelete(int _id) {
        sqLiteDatabase.delete(DatabaseHelper.tableName, DatabaseHelper._ID + "=" + _id, null);
    }
}
