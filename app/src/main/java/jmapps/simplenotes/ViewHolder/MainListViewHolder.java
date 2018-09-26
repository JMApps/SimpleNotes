package jmapps.simplenotes.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import jmapps.simplenotes.R;

public class MainListViewHolder extends RecyclerView.ViewHolder {

    public final TextView tvChapterTitle;
    public final TextView tvChapterContent;

    public MainListViewHolder(View itemView) {
        super(itemView);

        tvChapterTitle = itemView.findViewById(R.id.tv_chapter_title);
        tvChapterContent = itemView.findViewById(R.id.tv_chapter_content);
    }
}
