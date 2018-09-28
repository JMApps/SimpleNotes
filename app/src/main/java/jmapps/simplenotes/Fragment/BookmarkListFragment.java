package jmapps.simplenotes.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jmapps.simplenotes.Adapter.BookmarkListAdapter;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Model.BookmarkListModel;
import jmapps.simplenotes.R;

public class BookmarkListFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootBookmarkList = inflater.inflate(R.layout.fragment_bookmark, container, false);

        // Создаем объект DatabaseManager и открываем базу данных
        DatabaseManager databaseManager = new DatabaseManager(getActivity());
        databaseManager.databaseOpen();

        TextView tvIsBookmarkListEmpty = rootBookmarkList.findViewById(R.id.tv_is_bookmark_list_empty);
        RecyclerView rvContentsBookmarkList = rootBookmarkList.findViewById(R.id.rv_contents_bookmark_list);

        // Присваиваем MainListModel метод bookmarkListModels из DatabaseManager
        List<BookmarkListModel> bookmarkListModels = databaseManager.getBookmarkList();

        // Реализовываем LayoutManager и передаем его в RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvContentsBookmarkList.setLayoutManager(linearLayoutManager);

        // Создаем объект MainListAdapter и передаем ему MainListAdapter и Context
        BookmarkListAdapter bookmarkListAdapter = new BookmarkListAdapter(bookmarkListModels, getActivity());

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

        return rootBookmarkList;
    }
}
