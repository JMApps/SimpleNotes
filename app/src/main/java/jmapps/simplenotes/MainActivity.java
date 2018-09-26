package jmapps.simplenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import jmapps.simplenotes.Adapter.ListPagesAdapter;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Fragment.BookmarkListFragment;
import jmapps.simplenotes.Fragment.MainListFragment;

public class MainActivity extends AppCompatActivity {

    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        ViewPager vpContainerForPages = findViewById(R.id.vp_container_for_pages);
        setupViewPager(vpContainerForPages);

        TabLayout tlPages = findViewById(R.id.tl_pages);
        tlPages.setupWithViewPager(vpContainerForPages);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sorting_by:
                break;
            case R.id.action_grid_list_modes:
                break;
            case R.id.action_about_us:
                break;
            case R.id.action_share:
                break;
            case R.id.action_exit:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Закрываем базу данных
        databaseManager.databaseClose();
    }

    // Получаем объект на ListPagesAdapter и добавляем два фрагмента
    private void setupViewPager(ViewPager viewPager) {
        ListPagesAdapter listPagesAdapter = new ListPagesAdapter(getSupportFragmentManager());
        listPagesAdapter.addFragment(new MainListFragment(), "ВСЕ");
        listPagesAdapter.addFragment(new BookmarkListFragment(), "ИЗБРАННОЕ");
        viewPager.setAdapter(listPagesAdapter);
    }
}
