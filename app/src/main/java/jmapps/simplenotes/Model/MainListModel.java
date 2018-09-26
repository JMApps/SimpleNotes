package jmapps.simplenotes.Model;

public class MainListModel {

    // Модель главного списка

    private String _id;
    private String chapterTitle;
    private String chapterContent;

    public MainListModel(String _id, String chapterTitle, String chapterContent) {
        this._id = _id;
        this.chapterTitle = chapterTitle;
        this.chapterContent = chapterContent;
    }

    public String get_id() {
        return _id;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public String getChapterContent() {
        return chapterContent;
    }
}
