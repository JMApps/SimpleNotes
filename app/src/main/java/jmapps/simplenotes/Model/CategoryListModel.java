package jmapps.simplenotes.Model;

public class CategoryListModel {

    // Модель списка категорий

    private String categoryId;
    private String categoryTitle;

    public CategoryListModel(String categoryId, String categoryTitle) {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }
}
