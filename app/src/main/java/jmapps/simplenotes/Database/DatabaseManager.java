package jmapps.simplenotes.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jmapps.simplenotes.Model.BookmarkListModel;
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
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("F MMM yyyy HH:mm");
        String timeCreation = simpleDateFormat.format(new Date(System.currentTimeMillis()));

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.chapterTitle, chapterTitle);
        contentValues.put(DatabaseHelper.chapterContent, chapterContent);
        contentValues.put("time_creation", "Создано: " + timeCreation);
        sqLiteDatabase.insert(DatabaseHelper.tableName, null, contentValues);
    }

    // Метод обновления пункта базы данных
    public void databaseUpdateItem(int _id, String chapterTitle, String chapterContent) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("F MMM yyyy HH:mm");
        String timeChange = simpleDateFormat.format(new Date(System.currentTimeMillis()));

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper._ID, _id);
        contentValues.put(DatabaseHelper.chapterTitle, chapterTitle);
        contentValues.put(DatabaseHelper.chapterContent, chapterContent);
        contentValues.put(DatabaseHelper.chapterContent, chapterContent);
        contentValues.put("time_change", "Изменено: " + timeChange);
        sqLiteDatabase.update(DatabaseHelper.tableName, contentValues,
                DatabaseHelper._ID + "=" + _id, null);
    }

    // Метод удаления пункта базы данных
    public void databaseDeleteItem(int _id) {
        sqLiteDatabase.delete(DatabaseHelper.tableName,
                DatabaseHelper._ID + "=" + _id, null);
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
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.chapterContent)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.timeCreation)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.timeChange))));
                cursor.moveToNext();
            }
        }
        return allMainList;
    }


    // Создание списка избранных пунктов получаемого из базы данных
    public List<BookmarkListModel> getBookmarkList() {

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.tableName,
                DatabaseHelper.columns,
                "Favorite = 1",
                null, null, null, null);

        List<BookmarkListModel> allBookmarkList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                allBookmarkList.add(new BookmarkListModel(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.chapterTitle)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.chapterContent)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.timeCreation)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.timeChange))));
                cursor.moveToNext();
            }
        }
        return allBookmarkList;
    }

    // Метод добавления пункта в избранное
    public void addRemoveBookmark(boolean isChecked, int _id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("Favorite", isChecked);

        if (isChecked) {
            Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Удалено из избраннного", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.update(DatabaseHelper.tableName,
                contentValues,
                "_id = ?",
                new String[]{String.valueOf(_id)});
    }
}
