package jmapps.simplenotes.Model;

public class BookmarkListModel {

    // Модель главного списка

    private String _id;
    private String bookmarkTitle;
    private String bookmarkContent;
    private String timeCreation;
    private String timeChange;

    public BookmarkListModel(String _id,
                             String bookmarkTitle,
                             String bookmarkContent,
                             String timeCreation,
                             String timeChange) {
        this._id = _id;
        this.bookmarkTitle = bookmarkTitle;
        this.bookmarkContent = bookmarkContent;
        this.timeCreation = timeCreation;
        this.timeChange = timeChange;
    }

    public String get_id() {
        return _id;
    }

    public String getBookmarkTitle() {
        return bookmarkTitle;
    }

    public String getBookmarkContent() {
        return bookmarkContent;
    }

    public String getTimeCreation() {
        return timeCreation;
    }

    public String getTimeChange() {
        return timeChange;
    }
}
