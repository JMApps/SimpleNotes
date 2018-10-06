package jmapps.simplenotes.Database.Tables;

public class CategoryTable {

    public static final String categoryTable = "Category_table";

    public static final String _ID = "_id";
    public static final String categoryTitle = "category_title";

    public static final String createCategoryTable = "CREATE TABLE " + categoryTable + "(" + _ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + categoryTitle + " TEXT);";

    public static final String[] columnsCategoryItem = {_ID, categoryTitle};
}