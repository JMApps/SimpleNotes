package jmapps.simplenotes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import jmapps.simplenotes.Model.BookmarkListModel;
import jmapps.simplenotes.ModifyNoteActivity;
import jmapps.simplenotes.R;
import jmapps.simplenotes.ViewHolder.BookmarkListViewHolder;

public class BookmarkListAdapter extends RecyclerView.Adapter<BookmarkListViewHolder> implements Filterable {

    private List<BookmarkListModel> firstBookmarkState;
    private List<BookmarkListModel> mBookmarkListModel;
    private final Context mContext;
    private final LayoutInflater inflater;

    public BookmarkListAdapter(List<BookmarkListModel> bookmarkListModel,
                               Context context) {
        this.mBookmarkListModel = bookmarkListModel;
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
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
        final String strBookmarkTitle = mBookmarkListModel.get(position).getBookmarkTitle();
        final String strBookmarkContent = mBookmarkListModel.get(position).getBookmarkContent();
        final String strGetTimeCreation = mBookmarkListModel.get(position).getTimeCreation();
        final String strGetTimeChange = mBookmarkListModel.get(position).getTimeChange();

        holder.tvBookmarkTitle.setText(strBookmarkTitle);
        holder.tvBookmarkContent.setText(strBookmarkContent);
        holder.tvAddNoteDate.setText(strGetTimeCreation);

        if (strGetTimeChange != null) {
            // Если строка strGetTimeChange не пуста
            holder.tvChangeNoteDate.setVisibility(View.VISIBLE);
            holder.tvSlash.setVisibility(View.VISIBLE);
            holder.tvChangeNoteDate.setText(strGetTimeChange);
        } else {
            // Если строка strGetTimeChange не пуста
            holder.tvChangeNoteDate.setVisibility(View.GONE);
            holder.tvSlash.setVisibility(View.GONE);
        }

        // Устанавливаем слушателя на пункт RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toModifyNote = new Intent(mContext, ModifyNoteActivity.class);
                toModifyNote.putExtra("_id", _id);
                toModifyNote.putExtra("chapter_title", strBookmarkTitle);
                toModifyNote.putExtra("chapter_content", strBookmarkContent);
                mContext.startActivity(toModifyNote);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBookmarkListModel.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<BookmarkListModel> results = new ArrayList<>();
                if (firstBookmarkState == null)
                    firstBookmarkState = mBookmarkListModel;
                if (constraint != null) {
                    if (firstBookmarkState != null & (firstBookmarkState != null ? firstBookmarkState.size() : 0) > 0) {
                        for (final BookmarkListModel g : firstBookmarkState) {
                            if (g.getBookmarkTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || g.getBookmarkContent().toLowerCase().contains(constraint.toString().toLowerCase()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mBookmarkListModel = (ArrayList<BookmarkListModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
