package jmapps.simplenotes.Model;

public class BookmarkListModel {

    // Модель главного списка

    private String _id;
    private String bookmarkTitle;
    private String bookmarkContent;

    public BookmarkListModel(String _id, String bookmarkTitle, String bookmarkContent) {
        this._id = _id;
        this.bookmarkTitle = bookmarkTitle;
        this.bookmarkContent = bookmarkContent;
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
}
