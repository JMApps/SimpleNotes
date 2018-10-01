package jmapps.simplenotes.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jmapps.simplenotes.R;

public class BookmarkListViewHolder extends RecyclerView.ViewHolder {

    public final TextView tvBookmarkTitle;
    public final TextView tvBookmarkContent;
    public final TextView tvAddNoteDate;
    public final TextView tvChangeNoteDate;
    public final TextView tvSlash;

    public BookmarkListViewHolder(View itemView) {
        super(itemView);

        tvBookmarkTitle = itemView.findViewById(R.id.tv_bookmark_title);
        tvBookmarkContent = itemView.findViewById(R.id.tv_bookmark_content);
        tvAddNoteDate = itemView.findViewById(R.id.tv_add_note_date);
        tvChangeNoteDate = itemView.findViewById(R.id.tv_change_note_date);
        tvSlash = itemView.findViewById(R.id.tv_slash);
    }
}
