package jmapps.simplenotes.Model;

public class BookmarkListModel {

    // Модель списка закладок

    private final String _id;
    private final String bookmarkTitle;
    private final String bookmarkContent;
    private final String timeCreation;
    private final String timeChange;

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
