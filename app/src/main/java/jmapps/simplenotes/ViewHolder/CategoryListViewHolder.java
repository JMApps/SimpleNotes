package jmapps.simplenotes.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.R;

public class CategoryListViewHolder extends RecyclerView.ViewHolder {

    public final DatabaseManager databaseManager;

    public final TextView tvCategoryTitle;
    public final TextView tvCategoryMenuOptions;

    public CategoryListViewHolder(View itemView) {
        super(itemView);

        databaseManager = new DatabaseManager(itemView.getContext());
        databaseManager.databaseOpen();

        tvCategoryTitle = itemView.findViewById(R.id.tv_category_title);
        tvCategoryMenuOptions = itemView.findViewById(R.id.tv_category_menu_options);
    }
}