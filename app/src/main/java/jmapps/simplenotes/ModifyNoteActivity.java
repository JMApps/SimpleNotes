package jmapps.simplenotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import jmapps.simplenotes.Database.DatabaseManager;

public class ModifyNoteActivity extends AppCompatActivity {

    private EditText etModifyChapterTitle;
    private EditText etModifyChapterContent;
    private int _id;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_note);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_modify_note) {
            modifyItem();
            return true;
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

    private void returnMainList() {
        Intent toMainList = new Intent(ModifyNoteActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMainList);
    }
}
