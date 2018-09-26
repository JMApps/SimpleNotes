package jmapps.simplenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import jmapps.simplenotes.Adapter.MainListAdapter;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Model.MainListModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Создаем объект DatabaseManager и открываем базу данных
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        RecyclerView rvContentsMainList = findViewById(R.id.rv_contents_main_list);

        // Присваиваем MainListModel метод getMainList из DatabaseManager
        List<MainListModel> mainListModels = databaseManager.getMainList();

        // Реализовываем LayoutManager и передаем его в RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvContentsMainList.setLayoutManager(linearLayoutManager);

        // Создаем объект MainListAdapter и передаем ему MainListAdapter и Context
        MainListAdapter mainListAdapter = new MainListAdapter(mainListModels, this);
        // Обновляем адаптер
        mainListAdapter.notifyDataSetChanged();
        // Связываем RecyclerView и MainListAdapter
        rvContentsMainList.setAdapter(mainListAdapter);

        FloatingActionButton fabAddNote = findViewById(R.id.fab_add_note);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Переходим в AddNoteActivity
                Intent addNoteIntent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(addNoteIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
