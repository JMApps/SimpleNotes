package jmapps.simplenotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import jmapps.simplenotes.Adapter.ListPagesAdapter;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Fragment.BookmarkListFragment;
import jmapps.simplenotes.Fragment.MainListFragment;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private DatabaseManager databaseManager;

    private MenuItem nightMode;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainApplication.getInstance().isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

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
        // Получаем доступ к пункту меню и задаем ему состояние полученное по mPreferences
        nightMode = menu.findItem(R.id.action_night_mode);
        boolean nightModeState = mPreferences.getBoolean("night_mode", false);
        nightMode.setChecked(nightModeState);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isChecked;
        item.setChecked(isChecked = !item.isChecked());

        switch (item.getItemId()) {
            case R.id.action_night_mode:

                if (isChecked) {
                    // Включаем ночной режим
                    nightMode.setChecked(true);
                    MainApplication.getInstance().setIsNightModeEnabled(true);
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    // Отключаем ночной режим
                    nightMode.setChecked(false);
                    MainApplication.getInstance().setIsNightModeEnabled(false);
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }

                // Сохраняем последнее выбранное состояние
                mEditor.putBoolean("night_mode", isChecked).apply();

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
