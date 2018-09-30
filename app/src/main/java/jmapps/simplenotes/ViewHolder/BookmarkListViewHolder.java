package jmapps.simplenotes.ViewHolder;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import jmapps.simplenotes.R;

public class BookmarkListViewHolder extends RecyclerView.ViewHolder {

    public SharedPreferences mPreferences;

    public final TextView tvBookmarkTitle;
    public final TextView tvBookmarkContent;

    public BookmarkListViewHolder(View itemView) {
        super(itemView);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());

        tvBookmarkTitle = itemView.findViewById(R.id.tv_bookmark_title);
        tvBookmarkContent = itemView.findViewById(R.id.tv_bookmark_content);
    }
}
