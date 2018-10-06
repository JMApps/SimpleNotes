package jmapps.simplenotes.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jmapps.simplenotes.CategoryContentActivity;
import jmapps.simplenotes.Model.CategoryListModel;
import jmapps.simplenotes.R;
import jmapps.simplenotes.ViewHolder.CategoryListViewHolder;

@SuppressWarnings("unchecked")
public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListViewHolder> implements Filterable {

    private List<CategoryListModel> firstCategoryListState;
    private List<CategoryListModel> mCategoryListModel;
    private final Context mContext;
    private final LayoutInflater inflater;

    public CategoryListAdapter(List<CategoryListModel> categoryListModel,
                               Context context) {
        this.mCategoryListModel = categoryListModel;
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootMainList = inflater.inflate(R.layout.item_category_list, parent, false);
        return new CategoryListViewHolder(rootMainList);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final CategoryListViewHolder holder,
                                 @SuppressLint("RecyclerView") final int position) {
        final String strCategoryId = mCategoryListModel.get(position).getCategoryId();
        final String strCategoryTitle = mCategoryListModel.get(position).getCategoryTitle();

        holder.tvCategoryTitle.setText(strCategoryTitle);

        // Устанавливаем слушателя на пункт RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCategoryContent = new Intent(mContext, CategoryContentActivity.class);
                toCategoryContent.putExtra("category_position", position + 1);
                toCategoryContent.putExtra("category_title", strCategoryTitle);
                mContext.startActivity(toCategoryContent);
            }
        });

        holder.tvCategoryMenuOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(mContext, holder.tvCategoryMenuOptions);
                popup.inflate(R.menu.category_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.rename_category:
                                renameCategory(holder, Integer.parseInt(strCategoryId));
                                break;
                            case R.id.delete_category:
                                deleteCategory(holder, Integer.parseInt(strCategoryId));
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategoryListModel.size();
    }

    // Даже не представляю как это работает
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<CategoryListModel> results = new ArrayList<>();
                if (firstCategoryListState == null)
                    firstCategoryListState = mCategoryListModel;
                if (constraint != null) {
                    if (firstCategoryListState != null & (firstCategoryListState != null ? firstCategoryListState.size() : 0) > 0) {
                        for (final CategoryListModel g : firstCategoryListState) {
                            if (g.getCategoryTitle().toLowerCase().contains(constraint.toString().toLowerCase()))
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
                mCategoryListModel = (ArrayList<CategoryListModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    private void renameCategory(final CategoryListViewHolder holder, final int _id) {
        final AlertDialog.Builder renameCategory = new AlertDialog.Builder(mContext);

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_rename_category, null);

        final EditText etNewCategoryName = dialogView.findViewById(R.id.et_rename_category);
        openKeyboard(etNewCategoryName);
        renameCategory.setCancelable(false);

        renameCategory.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        renameCategory.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strNewCategoryName = etNewCategoryName.getText().toString();
                holder.databaseManager.renameCategory(_id, strNewCategoryName);
                Toast.makeText(mContext, R.string.category_renamed, Toast.LENGTH_SHORT).show();
                notifyItemChanged(_id);
            }
        });

        renameCategory.setView(dialogView);
        renameCategory.create().show();
    }

    private void deleteCategory(final CategoryListViewHolder holder, final int _id) {
        AlertDialog.Builder deleteCategory = new AlertDialog.Builder(mContext);
        @SuppressLint("InflateParams")

        View dialogView = inflater.inflate(R.layout.dialog_delete_category, null);

        deleteCategory.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        deleteCategory.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                holder.databaseManager.deleteCategory(_id);
                Toast.makeText(mContext, R.string.category_deleted, Toast.LENGTH_SHORT).show();
                notifyItemInserted(mCategoryListModel.size() - _id);
            }
        });

        deleteCategory.setView(dialogView);
        deleteCategory.create().show();
    }

    // Отображаем клаиатуру с фокусом на etAddCategoryName
    private void openKeyboard(final EditText editText) {
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        Objects.requireNonNull(mContext).getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.showSoftInput(editText, 0);
                }
            }
        }, 200);
    }
}