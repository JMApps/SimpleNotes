package jmapps.simplenotes.Model;

public class MainItemListModel {

    // Модель главного списка

    private final String mainItemId;
    private final String mainItemTitle;
    private final String mainItemContent;
    private final String timeCreation;
    private final String timeChange;

    public MainItemListModel(String mainItemId,
                             String mainItemTitle,
                             String mainItemContent,
                             String timeCreation,
                             String timeChange) {
        this.mainItemId = mainItemId;
        this.mainItemTitle = mainItemTitle;
        this.mainItemContent = mainItemContent;
        this.timeCreation = timeCreation;
        this.timeChange = timeChange;
    }

    public String getMainItemId() {
        return mainItemId;
    }

    public String getMainItemTitle() {
        return mainItemTitle;
    }

    public String getMainItemContent() {
        return mainItemContent;
    }

    public String getTimeCreation() {
        return timeCreation;
    }

    public String getTimeChange() {
        return timeChange;
    }
}
