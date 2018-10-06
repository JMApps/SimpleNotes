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

import jmapps.simplenotes.Adapter.MainItemListAdapter;
import jmapps.simplenotes.AddMainNoteActivity;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Model.MainItemListModel;
import jmapps.simplenotes.Observer.UpdateLists;
import jmapps.simplenotes.R;

public class MainItemListFragment extends Fragment implements SearchView.OnQueryTextListener, Observer {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private UpdateLists updateLists;
    private RecyclerView rvContentsMainItemList;
    private MainItemListAdapter mainItemListAdapter;

    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootMainList = inflater.inflate(R.layout.fragment_main_item, container, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPreferences.edit();

        DatabaseManager databaseManager = new DatabaseManager(getActivity());
        databaseManager.databaseOpen();

        TextView tvIsMainItemListEmpty = rootMainList.findViewById(R.id.tv_is_main_list_empty);
        rvContentsMainItemList = rootMainList.findViewById(R.id.rv_content_main_list);

        List<MainItemListModel> mainItemListModels = databaseManager.getMainItemsList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvContentsMainItemList.setLayoutManager(linearLayoutManager);
        rvContentsMainItemList.setHasFixedSize(true);

        mainItemListAdapter = new MainItemListAdapter(mainItemListModels, getActivity());

        if (mainItemListAdapter.getItemCount() == 0) {
            tvIsMainItemListEmpty.setVisibility(View.VISIBLE);
            rvContentsMainItemList.setVisibility(View.GONE);
        } else {
            tvIsMainItemListEmpty.setVisibility(View.GONE);
            rvContentsMainItemList.setVisibility(View.VISIBLE);
        }

        mainItemListAdapter.notifyDataSetChanged();
        rvContentsMainItemList.setAdapter(mainItemListAdapter);

        final FloatingActionButton fabAddMainNote = rootMainList.findViewById(R.id.fab_add_main_item_note);
        fabAddMainNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNoteIntent = new Intent(getActivity(), AddMainNoteActivity.class);
                startActivity(addNoteIntent);
            }
        });

        updateLists = UpdateLists.getInstance();
        updateLists.addObserver(this);
        updateLists.setUpdateAdapterLists(false);

        rvContentsMainItemList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) fabAddMainNote.hide();
                else fabAddMainNote.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        return rootMainList;
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof UpdateLists) {
            mainItemListAdapter.notifyDataSetChanged();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_item_fragment, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search_by_main_item);
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
            mainItemListAdapter.getFilter().filter("");
        } else {
            mainItemListAdapter.getFilter().filter(newText);
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        updateLists.deleteObservers();
    }
}
