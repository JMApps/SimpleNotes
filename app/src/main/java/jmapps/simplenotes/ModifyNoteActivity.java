package jmapps.simplenotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import jmapps.simplenotes.Database.DatabaseManager;
import jmapps.simplenotes.Observer.UpdateLists;

public class ModifyNoteActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private DatabaseManager databaseManager;
    private MenuItem bookmarkItemModify;

    private EditText etModifyChapterTitle;
    private EditText etModifyChapterContent;

    private int _id;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_note);

        // Добавляем в Toolbar кнопку "Назад"
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        // Создаем объект DatabaseManager и открываем базу данных
        databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        etModifyChapterTitle = findViewById(R.id.et_modify_chapter_title);
        etModifyChapterContent = findViewById(R.id.et_modify_chapter_content);

        // Получаем содержимое переданное через Intent
        Intent intent = getIntent();
        String strId = intent.getStringExtra("_id");
        String strChapterTitle = intent.getStringExtra("chapter_title");
        String strChapterContent = intent.getStringExtra("chapter_content");

        // Присваиваем переменной _id полученную по Intent strId
        _id = Integer.parseInt(strId);

        // Задаем это содержимое нашим EditText
        etModifyChapterTitle.setText(strChapterTitle);
        etModifyChapterContent.setText(strChapterContent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modify_note, menu);
        // Получаем доступ к пункту меню и задаем ему состояние полученное по mPreferences
        bookmarkItemModify = menu.findItem(R.id.action_add_bookmark_modify);
        boolean bookmarkItemState = mPreferences.getBoolean("bookmark_modify " + _id, false);
        bookmarkItemModify.setChecked(bookmarkItemState);

        // В зависимости от полученного состояния показываем разные иконки
        if (bookmarkItemState) {
            bookmarkItemModify.setIcon(R.drawable.ic_bookmark_white);
        } else {
            bookmarkItemModify.setIcon(R.drawable.ic_bookmark_border_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isChecked;
        item.setChecked(isChecked = !item.isChecked());

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_add_bookmark_modify:

                UpdateLists updateAdapterChapters = UpdateLists.getInstance();

                if (isChecked) {
                    // Закладка активна
                    bookmarkItemModify.setChecked(true);
                    bookmarkItemModify.setIcon(R.drawable.ic_bookmark_white);
                    databaseManager.addRemoveBookmark(true, _id);

                    updateAdapterChapters.setUpdateAdapterLists(true);
                    updateAdapterChapters.notifyObservers();
                } else {
                    // Закладка неактивна
                    bookmarkItemModify.setChecked(false);
                    bookmarkItemModify.setIcon(R.drawable.ic_bookmark_border_white);
                    databaseManager.addRemoveBookmark(false, _id);

                    updateAdapterChapters.setUpdateAdapterLists(false);
                    updateAdapterChapters.notifyObservers();
                }

                // Сохраняем состояние
                mEditor.putBoolean("bookmark_modify " + _id, isChecked).apply();

                break;
            case R.id.action_share_note:
                break;
            case R.id.action_modify_note_delete:
                // Удалить пункт
                deleteItem();
                Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void modifyItem() {
        // Получаем текущие введеные данные
        String strChapterTitle = etModifyChapterTitle.getText().toString();
        String strChapterContent = etModifyChapterContent.getText().toString();

        // Если первое или второе поле ввода не пустые, добавляем в БД
        // В противном случае просто удаляем текущий пункт
        if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
            // Вызываем метод databaseUpdateItem и передаем туда ID пункта и новое содержимое
            databaseManager.databaseUpdateItem(_id, strChapterTitle, strChapterContent);
        } else {
            databaseManager.databaseDeleteItem(_id);
        }

        // Вызываем метод с Intent-переходом к главной активити
        returnMainList();
    }

    private void deleteItem() {
        // Удаляем текущий пункт
        databaseManager.databaseDeleteItem(_id);

        // Вызываем метод с Intent-переходом к главной активити
        returnMainList();
    }

    // Метод с Intent-переходом к главной активити
    private void returnMainList() {
        Intent toMainList = new Intent(ModifyNoteActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMainList);
    }
}
