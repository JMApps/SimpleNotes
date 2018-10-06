package jmapps.simplenotes.Database.Tables;

public class MainTable {

    public static final String mainTable = "Main_table";

    public static final String _ID = "_id";
    public static final String itemTitle = "item_title";
    public static final String itemContent = "item_content";
    public static final String timeCreation = "time_creation";
    public static final String timeChanged = "time_changed";
    public static final String timeDeleted = "time_deleted";
    public static final String timeRecovery = "time_recovery";
    public static final String sampleFavorite = "sample_favorite";
    public static final String sampleCategory = "sample_category";
    public static final String sampleRecycleBin = "sample_recycle_bin";

    // Создаем таблицу
    public static final String createMainItemTable = "CREATE TABLE " + mainTable +
            "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + itemTitle + " TEXT, " + itemContent + " TEXT, " + timeCreation + " TEXT, " +
            timeChanged + " TEXT, " + timeDeleted + " TEXT, " + timeRecovery + " TEXT, " +
            sampleFavorite + " NUMERIC, " + sampleCategory + " INTEGER, " + sampleRecycleBin + " TEXT);";

    // Создаем переменную со столбцами
    public static final String[] columnsMainItem = {
            _ID, itemTitle, itemContent, timeCreation, timeChanged,
            timeDeleted, timeRecovery, sampleCategory, sampleRecycleBin};
}