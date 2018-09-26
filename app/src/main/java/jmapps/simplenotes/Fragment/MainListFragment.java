package jmapps.simplenotes.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import jmapps.simplenotes.Adapter.MainListAdapter;
import jmapps.simplenotes.AddNoteActivity;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Model.MainListModel;
import jmapps.simplenotes.R;

public class MainListFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootMainList = inflater.inflate(R.layout.fragment_main, container, false);

        // Создаем объект DatabaseManager и открываем базу данных
        DatabaseManager databaseManager = new DatabaseManager(getActivity());
        databaseManager.databaseOpen();

        TextView tvIsMainListEmpty = rootMainList.findViewById(R.id.tv_is_main_list_empty);
        RecyclerView rvContentsMainList = rootMainList.findViewById(R.id.rv_contents_main_list);

        // Присваиваем MainListModel метод getMainList из DatabaseManager
        List<MainListModel> mainListModels = databaseManager.getMainList();

        // Реализовываем LayoutManager и передаем его в RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvContentsMainList.setLayoutManager(linearLayoutManager);

        // Создаем объект MainListAdapter и передаем ему MainListAdapter и Context
        MainListAdapter mainListAdapter = new MainListAdapter(mainListModels, getActivity());

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

        return rootMainList;
    }
}
