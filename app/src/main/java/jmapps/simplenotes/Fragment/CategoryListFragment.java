package jmapps.simplenotes.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import jmapps.simplenotes.Adapter.CategoryListAdapter;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Model.CategoryListModel;
import jmapps.simplenotes.R;

public class CategoryListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private DatabaseManager databaseManager;
    private CategoryListAdapter categoryListAdapter;

    private TextView tvIsCategoryListEmpty;
    private RecyclerView rvContentCategoryList;
    private FloatingActionButton fabAddCategory;

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootMainList = inflater.inflate(R.layout.fragment_category, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor mEditor = mPreferences.edit();

        databaseManager = new DatabaseManager(getActivity());
        databaseManager.databaseOpen();

        tvIsCategoryListEmpty = rootMainList.findViewById(R.id.tv_is_category_list_empty);
        rvContentCategoryList = rootMainList.findViewById(R.id.rv_content_category_list);

        initList();

        fabAddCategory = rootMainList.findViewById(R.id.fab_add_category);
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory();
            }
        });

        rvContentCategoryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) fabAddCategory.hide();
                else fabAddCategory.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        return rootMainList;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_category_fragment, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search_by_categories);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            categoryListAdapter.getFilter().filter("");
        } else {
            categoryListAdapter.getFilter().filter(newText);
        }
        return true;
    }

    private void addCategory() {
        AlertDialog.Builder addCategoryName = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_add_category, null);

        final EditText etAddCategoryName = dialogView.findViewById(R.id.et_add_category);
        openKeyboard(etAddCategoryName);

        addCategoryName.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        addCategoryName.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strCategoryName = etAddCategoryName.getText().toString();
                Toast.makeText(getActivity(), R.string.category_added, Toast.LENGTH_SHORT).show();
                databaseManager.insertCategory(strCategoryName);
                initList();
            }
        });

        addCategoryName.setView(dialogView);
        addCategoryName.create().show();
    }

    private void openKeyboard(final EditText editText) {
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.showSoftInput(editText, 0);
                }
            }
        }, 200);
    }

    private void initList() {
        // Присваиваем MainListModel метод getMainList из DatabaseManager
        List<CategoryListModel> categoryListModels = databaseManager.getCategoryList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvContentCategoryList.setLayoutManager(linearLayoutManager);
        rvContentCategoryList.setHasFixedSize(true);

        // Создаем объект MainListAdapter и передаем ему MainListAdapter и Context
        categoryListAdapter = new CategoryListAdapter(categoryListModels, getActivity());

        // Если главный список пуст, скрываем RecyclerView и делаем видимым tvIsMainListEmpty
        if (categoryListAdapter.getItemCount() == 0) {
            tvIsCategoryListEmpty.setVisibility(View.VISIBLE);
            rvContentCategoryList.setVisibility(View.GONE);
        } else {
            tvIsCategoryListEmpty.setVisibility(View.GONE);
            rvContentCategoryList.setVisibility(View.VISIBLE);
        }

        categoryListAdapter.notifyDataSetChanged();
        // Связываем RecyclerView и MainListAdapter
        rvContentCategoryList.setAdapter(categoryListAdapter);
    }
}
