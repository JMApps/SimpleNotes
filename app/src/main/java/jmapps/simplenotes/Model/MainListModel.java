package jmapps.simplenotes.Model;

public class MainListModel {

    // Модель главного списка

    private String _id;
    private String chapterTitle;
    private String chapterContent;
    private String timeCreation;
    private String timeChange;

    public MainListModel(String _id,
                         String chapterTitle,
                         String chapterContent,
                         String timeCreation,
                         String timeChange) {
        this._id = _id;
        this.chapterTitle = chapterTitle;
        this.chapterContent = chapterContent;
        this.timeCreation = timeCreation;
        this.timeChange = timeChange;
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

    public String getTimeCreation() {
        return timeCreation;
    }

    public String getTimeChange() {
        return timeChange;
    }
}
