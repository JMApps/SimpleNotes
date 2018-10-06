package jmapps.simplenotes.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jmapps.simplenotes.R;

public class MainItemListViewHolder extends RecyclerView.ViewHolder {

    public final TextView tvMainItemTitle;
    public final TextView tvMainItemContent;
    public final TextView tvAddNoteDate;
    public final TextView tvSlash;
    public final TextView tvChangeNoteDate;

    public MainItemListViewHolder(View itemView) {
        super(itemView);

        tvMainItemTitle = itemView.findViewById(R.id.tv_main_item_title);
        tvMainItemContent = itemView.findViewById(R.id.tv_main_item_content);
        tvAddNoteDate = itemView.findViewById(R.id.tv_add_note_date);
        tvSlash = itemView.findViewById(R.id.tv_slash);
        tvChangeNoteDate = itemView.findViewById(R.id.tv_change_note_date);
    }
}
