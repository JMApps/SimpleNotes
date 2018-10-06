package jmapps.simplenotes.Fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import jmapps.simplenotes.Adapter.FavoriteListAdapter;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Model.FavoriteListModel;
import jmapps.simplenotes.Observer.UpdateLists;
import jmapps.simplenotes.R;

public class FavoriteListFragment extends Fragment implements SearchView.OnQueryTextListener, Observer {

    private DatabaseManager databaseManager;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private TextView tvIsFavoriteListEmpty;
    private RecyclerView rvContentsFavoriteList;
    private FavoriteListAdapter favoriteListAdapter;

    private UpdateLists updateLists;

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootBookmarkList = inflater.inflate(R.layout.fragment_favorite, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPreferences.edit();

        databaseManager = new DatabaseManager(getActivity());
        databaseManager.databaseOpen();

        tvIsFavoriteListEmpty = rootBookmarkList.findViewById(R.id.tv_is_favorite_list_empty);
        rvContentsFavoriteList = rootBookmarkList.findViewById(R.id.rv_contents_favorite_list);

        initFavoriteItemList();

        updateLists = UpdateLists.getInstance();
        updateLists.addObserver(this);
        updateLists.setUpdateAdapterLists(updateLists.isUpdateAdapterLists());

        return rootBookmarkList;
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof UpdateLists) {
            initFavoriteItemList();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_favorite_fragment, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search_by_favorites);
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
            favoriteListAdapter.getFilter().filter("");
        } else {
            favoriteListAdapter.getFilter().filter(newText);
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        updateLists.deleteObservers();
    }

    private void initFavoriteItemList() {
        List<FavoriteListModel> favoriteListModels = databaseManager.getFavoriteItemsList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvContentsFavoriteList.setLayoutManager(linearLayoutManager);

        favoriteListAdapter = new FavoriteListAdapter(favoriteListModels, getActivity());

        if (favoriteListAdapter.getItemCount() == 0) {
            tvIsFavoriteListEmpty.setVisibility(View.VISIBLE);
            rvContentsFavoriteList.setVisibility(View.GONE);
        } else {
            tvIsFavoriteListEmpty.setVisibility(View.GONE);
            rvContentsFavoriteList.setVisibility(View.VISIBLE);
        }

        favoriteListAdapter.notifyDataSetChanged();
        rvContentsFavoriteList.setAdapter(favoriteListAdapter);
    }
}
