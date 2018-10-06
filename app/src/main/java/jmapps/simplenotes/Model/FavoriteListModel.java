package jmapps.simplenotes.Model;

public class FavoriteListModel {

    // Модель списка закладок

    private final String favoriteId;
    private final String favoriteTitle;
    private final String favoriteContent;
    private final String timeCreation;
    private final String timeChange;

    public FavoriteListModel(String favoriteId,
                             String favoriteTitle,
                             String favoriteContent,
                             String timeCreation,
                             String timeChange) {
        this.favoriteId = favoriteId;
        this.favoriteTitle = favoriteTitle;
        this.favoriteContent = favoriteContent;
        this.timeCreation = timeCreation;
        this.timeChange = timeChange;
    }

    public String getFavoriteId() {
        return favoriteId;
    }

    public String getFavoriteTitle() {
        return favoriteTitle;
    }

    public String getFavoriteContent() {
        return favoriteContent;
    }

    public String getTimeCreation() {
        return timeCreation;
    }

    public String getTimeChange() {
        return timeChange;
    }
}
