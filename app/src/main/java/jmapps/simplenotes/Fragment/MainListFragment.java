package jmapps.simplenotes.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.TextView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import jmapps.simplenotes.Adapter.MainListAdapter;
import jmapps.simplenotes.AddNoteActivity;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Model.MainListModel;
import jmapps.simplenotes.Observer.UpdateLists;
import jmapps.simplenotes.R;

public class MainListFragment extends Fragment implements
        MenuItem.OnMenuItemClickListener, SearchView.OnQueryTextListener, Observer {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private RecyclerView rvContentsMainList;
    private MainListAdapter mainListAdapter;
    private UpdateLists updateLists;

    private MenuItem gridMode;

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootMainList = inflater.inflate(R.layout.fragment_main, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPreferences.edit();

        // Создаем объект DatabaseManager и открываем базу данных
        DatabaseManager databaseManager = new DatabaseManager(getActivity());
        databaseManager.databaseOpen();

        TextView tvIsMainListEmpty = rootMainList.findViewById(R.id.tv_is_main_list_empty);
        rvContentsMainList = rootMainList.findViewById(R.id.rv_contents_main_list);

        // Присваиваем MainListModel метод getMainList из DatabaseManager
        List<MainListModel> mainListModels = databaseManager.getMainList();

        // Создаем объект MainListAdapter и передаем ему MainListAdapter и Context
        mainListAdapter = new MainListAdapter(mainListModels, getActivity());

        // Если главный список пуст, скрываем RecyclerView и делаем видимым tvIsMainListEmpty
        if (mainListAdapter.getItemCount() == 0) {
            tvIsMainListEmpty.setVisibility(View.VISIBLE);
            rvContentsMainList.setVisibility(View.GONE);
        } else {
            tvIsMainListEmpty.setVisibility(View.GONE);
            rvContentsMainList.setVisibility(View.VISIBLE);
        }

        // Обновляем адаптер
        mainListAdapter.notifyDataSetChanged();
        // Связываем RecyclerView и MainListAdapter
        rvContentsMainList.setAdapter(mainListAdapter);

        FloatingActionButton fabAddNote = rootMainList.findViewById(R.id.fab_add_note);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Переходим в AddNoteActivity
                Intent addNoteIntent = new Intent(getActivity(), AddNoteActivity.class);
                startActivity(addNoteIntent);
            }
        });

        updateLists = UpdateLists.getInstance();
        updateLists.addObserver(this);
        updateLists.setUpdateAdapterLists(false);

        return rootMainList;
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof UpdateLists) {
            mainListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_fragment, menu);
        // Получаем доступ к пункту меню и задаем ему состояние полученное по mPreferences
        gridMode = menu.findItem(R.id.action_grid_list_modes);
        boolean gridModeState = mPreferences.getBoolean("grid_mode", false);
        gridMode.setChecked(gridModeState);
        // В зависимости от полученного состояния устанавливаем режимы отображения
        if (gridModeState) {
            gridMode.setTitle(R.string.list_mode);
            rvContentsMainList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            gridMode.setTitle(R.string.grid_mode);
            rvContentsMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        // Реализовываем setOnMenuItemClickListener
        gridMode.setOnMenuItemClickListener(this);

        // Получаем доступ к SearchView
        final MenuItem searchItem = menu.findItem(R.id.action_search_by_chapters);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Реализовываем setOnQueryTextListener
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        boolean isChecked;
        item.setChecked(isChecked = !item.isChecked());

        if (isChecked) {
            // Режим сетки активен
            gridMode.setTitle(R.string.list_mode);
            rvContentsMainList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            // Режим сетки неактивен
            gridMode.setTitle(R.string.grid_mode);
            rvContentsMainList.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        // Сохраняем состояние
        mEditor.putBoolean("grid_mode", isChecked).apply();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mainListAdapter.getFilter().filter("");
        } else {
            mainListAdapter.getFilter().filter(newText);
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        updateLists.deleteObservers();
    }
}
