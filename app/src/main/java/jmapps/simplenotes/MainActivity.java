package jmapps.simplenotes;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import jmapps.simplenotes.Adapter.ListPagesAdapter;
import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Fragment.CategoryListFragment;
import jmapps.simplenotes.Fragment.FavoriteListFragment;
import jmapps.simplenotes.Fragment.MainItemListFragment;

public class MainActivity extends AppCompatActivity {

    private DatabaseManager databaseManager;
    private ListPagesAdapter listPagesAdapter;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // По состояние isNightModeEnabled включаем или отключаем ночной режим
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

        vpContainerForPages.setCurrentItem(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem nightMode = menu.findItem(R.id.action_night_mode);

        // Задаем для nightModeState состояние полученное из mPreferences по умолчанию false
        boolean nightModeState = mPreferences.getBoolean("night_mode", false);
        nightMode.setChecked(nightModeState);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isCheckedNightMode;
        item.setChecked(isCheckedNightMode = !item.isChecked());

        switch (item.getItemId()) {
            case R.id.action_night_mode:

                if (isCheckedNightMode) {
                    // Включаем ночной режим
                    recreateActivity(true);
                } else {
                    // Отключаем ночной режим
                    recreateActivity(false);
                }

                // Сохраняем последнее выбранное состояние
                mEditor.putBoolean("night_mode", isCheckedNightMode).apply();

                break;
            case R.id.action_about_us:

                // Диалоговое окно "О нас"
                aboutUsDialog();

                break;
            case R.id.action_share:

                // Метод "Поделиться"
                shareAppLink();

                break;
            case R.id.action_exit:

                // Уничтожаем активити
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

    // Получаем объект ListPagesAdapter и создаем два фрагмента
    private void setupViewPager(ViewPager viewPager) {
        listPagesAdapter = new ListPagesAdapter(getSupportFragmentManager());
        listPagesAdapter.addFragment(new CategoryListFragment(), "КАТЕГОРИИ");
        listPagesAdapter.addFragment(new MainItemListFragment(), "ВСЕ");
        listPagesAdapter.addFragment(new FavoriteListFragment(), "ИЗБРАННЫЕ");
        viewPager.setAdapter(listPagesAdapter);
    }

    // Метод "Поделиться" ссылкой
    private void shareAppLink() {
        Intent shareAppLink = new Intent(Intent.ACTION_SEND);
        shareAppLink.setType("text/plain");
        shareAppLink.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + "\n" +
                "https://play.google.com/store/apps/details?id=jmapps.simplenotes");
        shareAppLink.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareAppLink = Intent.createChooser(shareAppLink, getString(R.string.share_to));
        startActivity(shareAppLink);
    }

    // Метод пересоздания активити
    private void recreateActivity(boolean state) {
        MainApplication.getInstance().setIsNightModeEnabled(state);
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    // Метод диалогового окна "О нас"
    private void aboutUsDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams")
        View dialogAboutUs = inflater.inflate(R.layout.dialog_about_us, null);

        AlertDialog.Builder instructionDialog = new AlertDialog.Builder(this);

        instructionDialog.setView(dialogAboutUs);
        TextView tvAboutUsContent = dialogAboutUs.findViewById(R.id.tv_about_us_content);
        tvAboutUsContent.setMovementMethod(LinkMovementMethod.getInstance());

        instructionDialog.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        instructionDialog.create().show();
    }
}
