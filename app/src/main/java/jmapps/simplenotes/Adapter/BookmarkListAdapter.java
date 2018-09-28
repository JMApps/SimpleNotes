package jmapps.simplenotes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.List;

import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Model.BookmarkListModel;
import jmapps.simplenotes.ModifyNoteActivity;
import jmapps.simplenotes.R;
import jmapps.simplenotes.ViewHolder.BookmarkListViewHolder;

public class BookmarkListAdapter extends RecyclerView.Adapter<BookmarkListViewHolder> {

    private List<BookmarkListModel> mBookmarkListModel;
    private Context mContext;
    private LayoutInflater inflater;
    private DatabaseManager databaseManager;

    public BookmarkListAdapter(List<BookmarkListModel> bookmarkListModel,
                               Context context) {
        this.mBookmarkListModel = bookmarkListModel;
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        databaseManager = new DatabaseManager(mContext);
    }

    @NonNull
    @Override
    public BookmarkListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootMainList = inflater.inflate(R.layout.item_bookmark_list, parent, false);
        return new BookmarkListViewHolder(rootMainList);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkListViewHolder holder, int position) {

        final String _id = mBookmarkListModel.get(position).get_id();
        final String strChapterTitle = mBookmarkListModel.get(position).getBookmarkTitle();
        final String strChapterContent = mBookmarkListModel.get(position).getBookmarkContent();

        holder.tvBookmarkTitle.setText(strChapterTitle);
        holder.tvBookmarkContent.setText(strChapterContent);

        holder.tbAddBookmark.setOnCheckedChangeListener(null);
        boolean bookmarkState = holder.mPreferences.getBoolean("bookmark_modify " + _id, false);
        holder.tbAddBookmark.setChecked(bookmarkState);

        holder.tbAddBookmark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //databaseManager.addRemoveBookmark(false, Integer.parseInt(_id));
            }
        });

        // Устанавливаем слушателя на пункт RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toModifyNote = new Intent(mContext, ModifyNoteActivity.class);
                toModifyNote.putExtra("_id", _id);
                toModifyNote.putExtra("bookmark_title", strChapterTitle);
                toModifyNote.putExtra("bookmark_content", strChapterContent);
                mContext.startActivity(toModifyNote);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBookmarkListModel.size();
    }
}
