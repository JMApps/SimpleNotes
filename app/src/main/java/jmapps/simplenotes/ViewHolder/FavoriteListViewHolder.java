package jmapps.simplenotes.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jmapps.simplenotes.R;

public class FavoriteListViewHolder extends RecyclerView.ViewHolder {

    public final TextView tvFavoriteTitle;
    public final TextView tvFavoriteContent;
    public final TextView tvAddNoteDate;
    public final TextView tvSlash;
    public final TextView tvChangeNoteDate;

    public FavoriteListViewHolder(View itemView) {
        super(itemView);

        tvFavoriteTitle = itemView.findViewById(R.id.tv_favorite_title);
        tvFavoriteContent = itemView.findViewById(R.id.tv_favorite_content);
        tvAddNoteDate = itemView.findViewById(R.id.tv_add_note_date);
        tvSlash = itemView.findViewById(R.id.tv_slash);
        tvChangeNoteDate = itemView.findViewById(R.id.tv_change_note_date);
    }
}
