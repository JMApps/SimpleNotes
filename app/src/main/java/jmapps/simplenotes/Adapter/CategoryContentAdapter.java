package jmapps.simplenotes.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import jmapps.simplenotes.CategoryContentActivity;
import jmapps.simplenotes.Model.CategoryContentModel;
import jmapps.simplenotes.R;
import jmapps.simplenotes.ViewHolder.CategoryContentViewHolder;

public class CategoryContentAdapter extends RecyclerView.Adapter<CategoryContentViewHolder> implements Filterable {

    private CategoryContentActivity mCategoryContentActivity;
    private List<CategoryContentModel> firstCategoryContentModel;
    private List<CategoryContentModel> mCategoryContentModel;
    private LayoutInflater inflater;

    public CategoryContentAdapter(CategoryContentActivity categoryContentActivity,
                                  List<CategoryContentModel> categoryContentModel,
                                  Context context) {
        this.mCategoryContentModel = categoryContentModel;
        inflater = LayoutInflater.from(context);
        this.mCategoryContentActivity = categoryContentActivity;
    }

    @NonNull
    @Override
    public CategoryContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_category_content, parent, false);
        return new CategoryContentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryContentViewHolder holder,
                                 @SuppressLint("RecyclerView") final int position) {
        final String strCategoryItemId = mCategoryContentModel.get(position).getCategoryItemId();
        final String strCategoryItemTitle = mCategoryContentModel.get(position).getCategoryItemTitle();
        final String strCategoryItemContent = mCategoryContentModel.get(position).getCategoryItemContent();
        String strTimeCreation = mCategoryContentModel.get(position).getTimeCreation();
        String strTimeChange = mCategoryContentModel.get(position).getTimeChange();

        holder.tvCategoryItemTitle.setText(strCategoryItemTitle);
        holder.tvCategoryItemContent.setText(strCategoryItemContent);
        holder.tvAddNoteDate.setText(strTimeCreation);

        if (strTimeChange != null) {
            // Если строка strGetTimeChange не пуста
            holder.tvChangeNoteDate.setVisibility(View.VISIBLE);
            holder.tvSlash.setVisibility(View.VISIBLE);
            holder.tvChangeNoteDate.setText(strTimeChange);
        } else {
            // Если строка strGetTimeChange пуста
            holder.tvChangeNoteDate.setVisibility(View.GONE);
            holder.tvSlash.setVisibility(View.GONE);
        }

        // Устанавливаем слушателя на пункт RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCategoryContentActivity.setExtraToModifyCategoryItem(
                        strCategoryItemId, strCategoryItemId, strCategoryItemTitle, strCategoryItemContent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategoryContentModel.size();
    }

    // Даже не представляю как это работает
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<CategoryContentModel> results = new ArrayList<>();
                if (firstCategoryContentModel == null)
                    firstCategoryContentModel = mCategoryContentModel;
                if (constraint != null) {
                    if (firstCategoryContentModel != null & (firstCategoryContentModel != null ? firstCategoryContentModel.size() : 0) > 0) {
                        for (final CategoryContentModel g : firstCategoryContentModel) {
                            if (g.getCategoryItemTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || g.getCategoryItemContent().toLowerCase().contains(constraint.toString().toLowerCase()))
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
                mCategoryContentModel = (ArrayList<CategoryContentModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
