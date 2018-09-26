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

    // Метод открытия базы данных
    public void databaseOpen() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    // Метод закрытия базы данных
    public void databaseClose() {
        databaseHelper.close();
    }


    // Метод вставки содержимого в пункт базы данных
    public void databaseInsertItem(String chapterTitle, String chapterContent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.chapterTitle, chapterTitle);
        contentValues.put(DatabaseHelper.chapterContent, chapterContent);
        sqLiteDatabase.insert(DatabaseHelper.tableName, null, contentValues);
    }

    // Создание главного списка пунктов получаемого из базы данных
    public List<MainListModel> getMainList() {

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

    // Метод обновления пункта базы данных
    public void databaseUpdateItem(int _id, String chapterTitle, String chapterContent) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper._ID, _id);
        contentValues.put(DatabaseHelper.chapterTitle, chapterTitle);
        contentValues.put(DatabaseHelper.chapterContent, chapterContent);
        sqLiteDatabase.update(DatabaseHelper.tableName, contentValues,
                DatabaseHelper._ID + "=" + _id, null);
    }

    // Метод удаления пункта базы данных
    public void databaseDeleteItem(int _id) {
        sqLiteDatabase.delete(DatabaseHelper.tableName,
                DatabaseHelper._ID + "=" + _id, null);
    }
}
