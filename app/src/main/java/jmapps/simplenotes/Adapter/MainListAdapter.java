package jmapps.simplenotes.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jmapps.simplenotes.Model.MainListModel;
import jmapps.simplenotes.R;
import jmapps.simplenotes.ViewHolder.MainListViewHolder;

public class MainListAdapter extends RecyclerView.Adapter<MainListViewHolder> {

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

    @Override
    public void onBindViewHolder(@NonNull MainListViewHolder holder, int position) {

        String _id = mMainListModel.get(position).get_id();
        String strChapterTitle = mMainListModel.get(position).getChapterTitle();
        String strChapterContent = mMainListModel.get(position).getChapterContent();

        holder.tvChapterTitle.setText(strChapterTitle);
        holder.tvChapterContent.setText(strChapterContent);
    }

    @Override
    public int getItemCount() {
        return mMainListModel.size();
    }
}
