package jmapps.simplenotes.Fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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

import jmapps.simplenotes.Adapter.BookmarkListAdapter;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Model.BookmarkListModel;
import jmapps.simplenotes.Observer.UpdateLists;
import jmapps.simplenotes.R;

public class BookmarkListFragment extends Fragment implements
        MenuItem.OnMenuItemClickListener, SearchView.OnQueryTextListener, Observer {

    private DatabaseManager databaseManager;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private TextView tvIsBookmarkListEmpty;
    private RecyclerView rvContentsBookmarkList;
    private BookmarkListAdapter bookmarkListAdapter;

    private MenuItem gridMode;
    private UpdateLists updateLists;

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootBookmarkList = inflater.inflate(R.layout.fragment_bookmark, container, false);

        // Сохраняем данные при смене ориентации экрана
        setRetainInstance(true);
        // Отображаем пункты меню
        setHasOptionsMenu(true);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPreferences.edit();

        // Создаем объект DatabaseManager и открываем базу данных
        databaseManager = new DatabaseManager(getActivity());
        databaseManager.databaseOpen();

        tvIsBookmarkListEmpty = rootBookmarkList.findViewById(R.id.tv_is_bookmark_list_empty);
        rvContentsBookmarkList = rootBookmarkList.findViewById(R.id.rv_contents_bookmark_list);

        // Инициализируем весь список
        initBookmarkChapterList();

        // Получаем объект Observer в котором слушатель на изменение в списках
        updateLists = UpdateLists.getInstance();
        updateLists.addObserver(this);
        updateLists.setUpdateAdapterLists(updateLists.isUpdateAdapterLists());

        return rootBookmarkList;
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof UpdateLists) {
            // Если список был изменен инициализируем его повторно
            initBookmarkChapterList();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bookmark_fragment, menu);
        gridMode = menu.findItem(R.id.action_grid_mode_bookmark);

        // Задаем для gridModeState состояние полученное из mPreferences по умолчанию false
        boolean gridModeState = mPreferences.getBoolean("grid_mode_bookmarks", false);
        gridMode.setChecked(gridModeState);

        // По состоянию полученному из mPreferences отображаем сетку/список
        if (gridModeState) {
            gridMode.setTitle(R.string.list_mode);
            rvContentsBookmarkList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            gridMode.setTitle(R.string.grid_mode);
            rvContentsBookmarkList.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        // Реализовываем setOnMenuItemClickListener
        gridMode.setOnMenuItemClickListener(this);

        final MenuItem searchItem = menu.findItem(R.id.action_search_by_bookmarks);
        // Получаем доступ к SearchView
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Реализовываем setOnQueryTextListener
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        boolean isCheckedGriMode;
        item.setChecked(isCheckedGriMode = !item.isChecked());

        if (isCheckedGriMode) {
            // Режим сетки
            gridMode.setTitle(R.string.list_mode);
            rvContentsBookmarkList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            // Режим списка
            gridMode.setTitle(R.string.grid_mode);
            rvContentsBookmarkList.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        // Сохраняем состояние
        mEditor.putBoolean("grid_mode_bookmarks", isCheckedGriMode).apply();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            bookmarkListAdapter.getFilter().filter("");
        } else {
            bookmarkListAdapter.getFilter().filter(newText);
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Удалем Observer
        updateLists.deleteObservers();
    }

    // Инициализация списка
    private void initBookmarkChapterList() {
        // Присваиваем MainListModel метод bookmarkListModels из DatabaseManager
        List<BookmarkListModel> bookmarkListModels = databaseManager.getBookmarkList();

        // Реализовываем LayoutManager и передаем его в RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvContentsBookmarkList.setLayoutManager(linearLayoutManager);

        // Создаем объект MainListAdapter и передаем ему MainListAdapter и Context
        bookmarkListAdapter = new BookmarkListAdapter(bookmarkListModels, getActivity());

        // Если главный список пуст, скрываем RecyclerView и делаем видимым tvIsBookmarkListEmpty
        if (bookmarkListAdapter.getItemCount() == 0) {
            tvIsBookmarkListEmpty.setVisibility(View.VISIBLE);
            rvContentsBookmarkList.setVisibility(View.GONE);
        } else {
            tvIsBookmarkListEmpty.setVisibility(View.GONE);
            rvContentsBookmarkList.setVisibility(View.VISIBLE);
        }

        // Обновляем адаптер
        bookmarkListAdapter.notifyDataSetChanged();
        // Связываем RecyclerView и MainListAdapter
        rvContentsBookmarkList.setAdapter(bookmarkListAdapter);
    }
}
