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

import jmapps.simplenotes.Model.MainItemListModel;
import jmapps.simplenotes.ModifyMainNoteActivity;
import jmapps.simplenotes.R;
import jmapps.simplenotes.ViewHolder.MainItemListViewHolder;

@SuppressWarnings("unchecked")
public class MainItemListAdapter extends RecyclerView.Adapter<MainItemListViewHolder> implements Filterable {

    private List<MainItemListModel> firstMainItemState;
    private List<MainItemListModel> mMainItemListModel;
    private final Context mContext;
    private final LayoutInflater inflater;

    public MainItemListAdapter(List<MainItemListModel> mainItemListModel,
                               Context context) {
        this.mMainItemListModel = mainItemListModel;
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MainItemListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootMainList = inflater.inflate(R.layout.item_main_item_list, parent, false);
        return new MainItemListViewHolder(rootMainList);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MainItemListViewHolder holder, final int position) {

        final String _id = mMainItemListModel.get(position).getMainItemId();
        final String strMainItemTitle = mMainItemListModel.get(position).getMainItemTitle();
        final String strMainItemContent = mMainItemListModel.get(position).getMainItemContent();
        final String strGetTimeCreation = mMainItemListModel.get(position).getTimeCreation();
        final String strGetTimeChange = mMainItemListModel.get(position).getTimeChange();

        holder.tvMainItemTitle.setText(strMainItemTitle);
        holder.tvMainItemContent.setText(strMainItemContent);
        holder.tvAddNoteDate.setText(strGetTimeCreation);

        if (strGetTimeChange != null) {
            holder.tvChangeNoteDate.setVisibility(View.VISIBLE);
            holder.tvSlash.setVisibility(View.VISIBLE);
            holder.tvChangeNoteDate.setText(strGetTimeChange);
        } else {
            holder.tvChangeNoteDate.setVisibility(View.GONE);
            holder.tvSlash.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toModifyNote = new Intent(mContext, ModifyMainNoteActivity.class);
                toModifyNote.putExtra("item_id", _id);
                toModifyNote.putExtra("item_title", strMainItemTitle);
                toModifyNote.putExtra("item_content", strMainItemContent);
                mContext.startActivity(toModifyNote);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMainItemListModel.size();
    }

    // Даже не представляю как это работает
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<MainItemListModel> results = new ArrayList<>();
                if (firstMainItemState == null)
                    firstMainItemState = mMainItemListModel;
                if (constraint != null) {
                    if (firstMainItemState != null & (firstMainItemState != null ? firstMainItemState.size() : 0) > 0) {
                        for (final MainItemListModel g : firstMainItemState) {
                            if (g.getMainItemTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || g.getMainItemContent().toLowerCase().contains(constraint.toString().toLowerCase()))
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
                mMainItemListModel = (ArrayList<MainItemListModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}