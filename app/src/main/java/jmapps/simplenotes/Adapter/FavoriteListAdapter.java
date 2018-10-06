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

import jmapps.simplenotes.Model.FavoriteListModel;
import jmapps.simplenotes.ModifyMainNoteActivity;
import jmapps.simplenotes.R;
import jmapps.simplenotes.ViewHolder.FavoriteListViewHolder;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListViewHolder> implements Filterable {

    private List<FavoriteListModel> firstFavoriteState;
    private List<FavoriteListModel> mFavoriteListModel;
    private final Context mContext;
    private final LayoutInflater inflater;

    public FavoriteListAdapter(List<FavoriteListModel> favoriteListModel,
                               Context context) {
        this.mFavoriteListModel = favoriteListModel;
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public FavoriteListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootMainList = inflater.inflate(R.layout.item_favorite_list, parent, false);
        return new FavoriteListViewHolder(rootMainList);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteListViewHolder holder, int position) {

        final String strFavoriteId = mFavoriteListModel.get(position).getFavoriteId();
        final String strFavoriteTitle = mFavoriteListModel.get(position).getFavoriteTitle();
        final String strFavoriteContent = mFavoriteListModel.get(position).getFavoriteContent();
        final String strGetTimeCreation = mFavoriteListModel.get(position).getTimeCreation();
        final String strGetTimeChange = mFavoriteListModel.get(position).getTimeChange();

        holder.tvFavoriteTitle.setText(strFavoriteTitle);
        holder.tvFavoriteContent.setText(strFavoriteContent);
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
                Intent toModifyNote = new Intent(mContext, ModifyMainNoteActivity.class);
                toModifyNote.putExtra("item_id", strFavoriteId);
                toModifyNote.putExtra("item_title", strFavoriteTitle);
                toModifyNote.putExtra("item_content", strFavoriteContent);
                mContext.startActivity(toModifyNote);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFavoriteListModel.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<FavoriteListModel> results = new ArrayList<>();
                if (firstFavoriteState == null)
                    firstFavoriteState = mFavoriteListModel;
                if (constraint != null) {
                    if (firstFavoriteState != null & (firstFavoriteState != null ? firstFavoriteState.size() : 0) > 0) {
                        for (final FavoriteListModel g : firstFavoriteState) {
                            if (g.getFavoriteTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || g.getFavoriteContent().toLowerCase().contains(constraint.toString().toLowerCase()))
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
                mFavoriteListModel = (ArrayList<FavoriteListModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
