package jmapps.simplenotes.ViewHolder;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import jmapps.simplenotes.R;

public class MainListViewHolder extends RecyclerView.ViewHolder {

    public SharedPreferences mPreferences;

    public final TextView tvChapterTitle;
    public final TextView tvChapterContent;

    public MainListViewHolder(View itemView) {
        super(itemView);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());

        tvChapterTitle = itemView.findViewById(R.id.tv_chapter_title);
        tvChapterContent = itemView.findViewById(R.id.tv_chapter_content);
    }
}
