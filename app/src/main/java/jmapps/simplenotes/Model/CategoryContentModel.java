package jmapps.simplenotes.Model;

public class CategoryContentModel {

    private final String categoryItemId;
    private final String categoryItemTitle;
    private final String categoryItemContent;
    private final String timeCreation;
    private final String timeChange;

    public CategoryContentModel(String categoryItemId,
                                String categoryItemTitle,
                                String categoryItemContent,
                                String timeCreation,
                                String timeChange) {
        this.categoryItemId = categoryItemId;
        this.categoryItemTitle = categoryItemTitle;
        this.categoryItemContent = categoryItemContent;
        this.timeCreation = timeCreation;
        this.timeChange = timeChange;
    }

    public String getCategoryItemId() {
        return categoryItemId;
    }

    public String getCategoryItemTitle() {
        return categoryItemTitle;
    }

    public String getCategoryItemContent() {
        return categoryItemContent;
    }

    public String getTimeCreation() {
        return timeCreation;
    }

    public String getTimeChange() {
        return timeChange;
    }
}
