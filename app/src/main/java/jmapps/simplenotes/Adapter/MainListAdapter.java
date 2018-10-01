package jmapps.simplenotes.Adapter;

import android.annotation.SuppressLint;
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

import jmapps.simplenotes.Model.MainListModel;
import jmapps.simplenotes.ModifyNoteActivity;
import jmapps.simplenotes.R;
import jmapps.simplenotes.ViewHolder.MainListViewHolder;

public class MainListAdapter extends RecyclerView.Adapter<MainListViewHolder> implements Filterable {

    private List<MainListModel> firstListState;
    private List<MainListModel> mMainListModel;
    private Context mContext;
    private LayoutInflater inflater;

    public MainListAdapter(List<MainListModel> mainListModel,
                           Context context) {
        this.mMainListModel = mainListModel;
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MainListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootMainList = inflater.inflate(R.layout.item_main_list, parent, false);
        return new MainListViewHolder(rootMainList);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MainListViewHolder holder, final int position) {

        final String _id = mMainListModel.get(position).get_id();
        final String strChapterTitle = mMainListModel.get(position).getChapterTitle();
        final String strChapterContent = mMainListModel.get(position).getChapterContent();
        final String strGetTimeCreation = mMainListModel.get(position).getTimeCreation();
        final String strGetTimeChange = mMainListModel.get(position).getTimeChange();

        holder.tvChapterTitle.setText(strChapterTitle);
        holder.tvChapterContent.setText(strChapterContent);
        holder.tvAddNoteDate.setText(strGetTimeCreation);

        if (strGetTimeChange != null) {
            holder.tvChangeNoteDate.setVisibility(View.VISIBLE);
            holder.tvChangeNoteDate.setText(" / " + strGetTimeChange);
        } else {
            holder.tvChangeNoteDate.setVisibility(View.GONE);
        }

        // Устанавливаем слушателя на пункт RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toModifyNote = new Intent(mContext, ModifyNoteActivity.class);
                toModifyNote.putExtra("_id", _id);
                toModifyNote.putExtra("chapter_title", strChapterTitle);
                toModifyNote.putExtra("chapter_content", strChapterContent);
                mContext.startActivity(toModifyNote);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMainListModel.size();
    }

    // Даже не представляю как это работает
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<MainListModel> results = new ArrayList<>();
                if (firstListState == null)
                    firstListState = mMainListModel;
                if (constraint != null) {
                    if (firstListState != null & (firstListState != null ? firstListState.size() : 0) > 0) {
                        for (final MainListModel g : firstListState) {
                            if (g.getChapterTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || g.getChapterContent().toLowerCase().contains(constraint.toString().toLowerCase()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mMainListModel = (ArrayList<MainListModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
