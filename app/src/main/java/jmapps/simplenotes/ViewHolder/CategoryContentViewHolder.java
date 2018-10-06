package jmapps.simplenotes.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jmapps.simplenotes.R;

public class CategoryContentViewHolder extends RecyclerView.ViewHolder {

    public final TextView tvCategoryItemTitle;
    public final TextView tvCategoryItemContent;
    public final TextView tvAddNoteDate;
    public final TextView tvSlash;
    public final TextView tvChangeNoteDate;

    public CategoryContentViewHolder(View itemView) {
        super(itemView);

        tvCategoryItemTitle = itemView.findViewById(R.id.tv_category_item_title);
        tvCategoryItemContent = itemView.findViewById(R.id.tv_category_item_content);
        tvAddNoteDate = itemView.findViewById(R.id.tv_add_note_date);
        tvSlash = itemView.findViewById(R.id.tv_slash);
        tvChangeNoteDate = itemView.findViewById(R.id.tv_change_note_date);
    }
}
