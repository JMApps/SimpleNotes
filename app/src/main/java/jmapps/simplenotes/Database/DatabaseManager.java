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

import jmapps.simplenotes.Database.Tables.CategoryTable;
import jmapps.simplenotes.Database.Tables.DatabaseCreation;
import jmapps.simplenotes.Database.Tables.MainTable;
import jmapps.simplenotes.Model.CategoryContentModel;
import jmapps.simplenotes.Model.CategoryListModel;
import jmapps.simplenotes.Model.FavoriteListModel;
import jmapps.simplenotes.Model.MainItemListModel;

public class DatabaseManager {

    private DatabaseCreation databaseMainList;
    private final Context context;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseManager(Context ctx) {
        context = ctx;
    }

    // Метод открытия базы данных
    public void databaseOpen() throws SQLException {
        databaseMainList = new DatabaseCreation(context);
        sqLiteDatabase = databaseMainList.getWritableDatabase();
    }

    // Метод закрытия базы данных
    public void databaseClose() {
        databaseMainList.close();
    }

    // Создание главного пункта с текущей датой
    public void insertMainItem(String itemTitle, String itemContent) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String timeCreation = simpleDateFormat.format(new Date(System.currentTimeMillis()));

        ContentValues mainItem = new ContentValues();
        mainItem.put(MainTable.itemTitle, itemTitle);
        mainItem.put(MainTable.itemContent, itemContent);
        mainItem.put("time_creation", "Создано: " + timeCreation);
        sqLiteDatabase.insert(MainTable.mainTable, null, mainItem);
    }

    // Создание главного пункта с текущей датой
    public void insertCategoryItem(int sampleId, String categoryItemTitle, String categoryItemContent) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String timeCreation = simpleDateFormat.format(new Date(System.currentTimeMillis()));

        ContentValues categoryItem = new ContentValues();
        categoryItem.put(MainTable.sampleCategory, sampleId);
        categoryItem.put(MainTable.itemTitle, categoryItemTitle);
        categoryItem.put(MainTable.itemContent, categoryItemContent);
        categoryItem.put("time_creation", "Создано: " + timeCreation);
        sqLiteDatabase.insert(MainTable.mainTable, null, categoryItem);
    }

    public void insertCategory(String strCategoryTitle) {
        ContentValues categoryName = new ContentValues();
        categoryName.put(CategoryTable.categoryTitle, strCategoryTitle);
        sqLiteDatabase.insert(CategoryTable.categoryTable, null, categoryName);
    }

    // Изменение главного пункта с текущей датой
    public void updateMainItem(int _id, String itemTitle, String itemContent) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String timeChange = simpleDateFormat.format(new Date(System.currentTimeMillis()));

        ContentValues mainItem = new ContentValues();
        mainItem.put(MainTable._ID, _id);
        mainItem.put(MainTable.itemTitle, itemTitle);
        mainItem.put(MainTable.itemContent, itemContent);
        mainItem.put("time_changed", "Изменено: " + timeChange);
        sqLiteDatabase.update(MainTable.mainTable, mainItem,
                MainTable._ID + " = " + _id, null);
    }

    public void renameCategory(int _id, String strCategoryTitle) {
        ContentValues categoryName = new ContentValues();
        categoryName.put(CategoryTable._ID, _id);
        categoryName.put(CategoryTable.categoryTitle, strCategoryTitle);
        sqLiteDatabase.update(CategoryTable.categoryTable, categoryName,
                CategoryTable._ID + " = " + _id, null);
    }

    public void deleteMainItem(int _id) {
        sqLiteDatabase.delete(MainTable.mainTable,
                MainTable._ID + " = " + _id, null);
    }

    public void deleteCategory(int _id) {
        sqLiteDatabase.delete(CategoryTable.categoryTable,
                CategoryTable._ID + " = " + _id, null);
    }

    // Список категорий
    public List<CategoryListModel> getCategoryList() {

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(CategoryTable.categoryTable,
                CategoryTable.columnsCategoryItem,
                null, null, null, null, null);

        List<CategoryListModel> allCategoryList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                allCategoryList.add(new CategoryListModel(
                        cursor.getString(cursor.getColumnIndex(CategoryTable._ID)),
                        cursor.getString(cursor.getColumnIndex(CategoryTable.categoryTitle))));
                cursor.moveToNext();
            }
        }
        return allCategoryList;
    }

    // Главный список
    public List<MainItemListModel> getMainItemsList() {

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(MainTable.mainTable,
                MainTable.columnsMainItem,
                null,
                null, null, null, null);

        List<MainItemListModel> allMainList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                allMainList.add(new MainItemListModel(
                        cursor.getString(cursor.getColumnIndex(MainTable._ID)),
                        cursor.getString(cursor.getColumnIndex(MainTable.itemTitle)),
                        cursor.getString(cursor.getColumnIndex(MainTable.itemContent)),
                        cursor.getString(cursor.getColumnIndex(MainTable.timeCreation)),
                        cursor.getString(cursor.getColumnIndex(MainTable.timeChanged))));
                cursor.moveToNext();
            }
        }
        return allMainList;
    }

    // Главный список
    public List<FavoriteListModel> getFavoriteItemsList() {

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(MainTable.mainTable,
                MainTable.columnsMainItem,
                "sample_favorite = 1",
                null, null, null, null);

        List<FavoriteListModel> allFavoriteList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                allFavoriteList.add(new FavoriteListModel(
                        cursor.getString(cursor.getColumnIndex(MainTable._ID)),
                        cursor.getString(cursor.getColumnIndex(MainTable.itemTitle)),
                        cursor.getString(cursor.getColumnIndex(MainTable.itemContent)),
                        cursor.getString(cursor.getColumnIndex(MainTable.timeCreation)),
                        cursor.getString(cursor.getColumnIndex(MainTable.timeChanged))));
                cursor.moveToNext();
            }
        }
        return allFavoriteList;
    }

    // Главный список
    public List<CategoryContentModel> getCategoryContentList(int categoryId) {

        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(MainTable.mainTable,
                MainTable.columnsMainItem,
                "sample_category = ?",
                new String[]{String.valueOf(categoryId)},
                null, null, null);

        List<CategoryContentModel> allCategoryContentList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                allCategoryContentList.add(new CategoryContentModel(
                        cursor.getString(cursor.getColumnIndex(MainTable._ID)),
                        cursor.getString(cursor.getColumnIndex(MainTable.itemTitle)),
                        cursor.getString(cursor.getColumnIndex(MainTable.itemContent)),
                        cursor.getString(cursor.getColumnIndex(MainTable.timeCreation)),
                        cursor.getString(cursor.getColumnIndex(MainTable.timeChanged))));
                cursor.moveToNext();
            }
        }
        return allCategoryContentList;
    }

    // Метод добавления пункта в избранное
    public void addRemoveBookmark(boolean isChecked, int _id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("sample_favorite", isChecked);

        if (isChecked) {
            Toast.makeText(context, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Удалено из избраннного", Toast.LENGTH_SHORT).show();
        }

        sqLiteDatabase.update(MainTable.mainTable,
                contentValues,
                "_id = ?",
                new String[]{String.valueOf(_id)});
    }
}