package jmapps.simplenotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jmapps.simplenotes.Database.DatabaseManager;

public class AddNoteActivity extends AppCompatActivity implements
        TextView.OnEditorActionListener, View.OnTouchListener {

    private DatabaseManager databaseManager;

    private EditText etAddChapterTitle;
    private EditText etAddChapterContent;

    private String strChapterTitle;
    private String strChapterContent;

    private ActionBar actionBar;
    private MenuItem addChangeNote;

    private boolean questionDialogState = false;

    @SuppressLint({"CommitPrefEdits", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Получаем объект DatabaseManager и открываем базу данных
        databaseManager = new DatabaseManager(this);
        databaseManager.databaseOpen();

        actionBar = getSupportActionBar();

        etAddChapterTitle = findViewById(R.id.et_add_chapter_title);
        etAddChapterContent = findViewById(R.id.et_add_chapter_content);

        // Слушатель для получения доступа к кнопку "Enter" у клавиатуры
        etAddChapterTitle.setOnEditorActionListener(this);

        // Слушатель на касание по полям EditText
        etAddChapterTitle.setOnTouchListener(this);
        etAddChapterContent.setOnTouchListener(this);

        openKeyboard();
    }

    @Override
    public void onBackPressed() {
        // Если questionDialogState не равен true, проверяем поля на наличие текста
        // Если нет текста, закрываем активити, если есть текст, предлагаем сохранить или удалить его
        if (!questionDialogState) {
            getTextFromEditTexts();
            if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
                questionDialog();
            } else {
                finish();
            }
            // Если же questionDialogState равен true, проверяем поля на наличие текста
            // Если есть текст, добавляем его в базу данных, если его нет, закрываем активити
        } else {
            super.onBackPressed();
            addNote();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        addChangeNote = menu.findItem(R.id.action_add_change);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isChecked;
        item.setChecked(isChecked = !item.isChecked());

        switch (item.getItemId()) {
            case R.id.action_add_change:
                if (isChecked) {
                    // Добавить заметку
                    saveNote();
                    questionDialogState = true;
                } else {
                    // Изменить заметку
                    modifyItem();
                    questionDialogState = false;
                }
                break;
            case android.R.id.home:
                // Добавить заметку
                addNote();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Если фокус на первом поле и нажать интер, убираем из него фокус
    // и передаем фокус нижнему полю
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean handled = false;
        switch (v.getId()) {
            case R.id.et_add_chapter_title:
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    etAddChapterTitle.clearFocus();
                    etAddChapterContent.requestFocus();
                    handled = true;
                }
        }
        return handled;
    }

    // При клике на любое из двух полей будет вызван метод modifyItem()
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.et_add_chapter_title:
                addChangeNote.setChecked(false);
                modifyItem();
                questionDialogState = false;
                break;
            case R.id.et_add_chapter_content:
                addChangeNote.setChecked(false);
                modifyItem();
                questionDialogState = false;
        }
        return false;
    }

    // Добавить заметку
    private void saveNote() {
        getTextFromEditTexts();
        if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
            clearFocusEditTexts();
            addChangeNote.setTitle(R.string.modify_note);
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
            closeKeyboard();
        } else {
            finish();
        }
    }

    // Изменить заметку
    private void modifyItem() {
        getTextFromEditTexts();
        if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
            addChangeNote.setTitle(R.string.save_note);
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);
            openKeyboard();
        } else {
            addChangeNote.setTitle(R.string.save_note);
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);
            openKeyboard();
        }
    }

    // Добавить конечный вариант в базу данных
    @SuppressLint("SimpleDateFormat")
    private void addNote() {
        getTextFromEditTexts();
        if (!strChapterTitle.isEmpty() || !strChapterContent.isEmpty()) {
            databaseManager.databaseInsertItem(strChapterTitle, strChapterContent);
            Toast.makeText(this, R.string.note_added, Toast.LENGTH_SHORT).show();
            returnMainActivity();
        } else {
            finish();
        }
    }

    // Диалоговое окно с предупреждение о сохранении введенного текста
    private void questionDialog() {
        final AlertDialog.Builder questionDialog = new AlertDialog.Builder(this);

        questionDialog.setIcon(R.drawable.ic_warning);
        questionDialog.setTitle(R.string.warning);
        questionDialog.setMessage(R.string.question_save_note);
        questionDialog.setCancelable(false);


        // Обработчик варианта "Сохранить"
        questionDialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNote();
            }
        });

        // Обработчик варианта "Удалить"
        questionDialog.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Toast.makeText(AddNoteActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
            }
        });

        // Обработчик варианта "Отмена"
        questionDialog.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        questionDialog.create().show();
    }

    // Отображаем клаиатуру с фокусом на etAddChapterContent
    private void openKeyboard() {
        etAddChapterContent.requestFocus();
        etAddChapterContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.showSoftInput(etAddChapterContent, 0);
                }
            }
        }, 200);
    }

    // Скрываем клавиатуру
    private void closeKeyboard() {
        etAddChapterContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (keyboard != null) {
                    keyboard.hideSoftInputFromWindow(etAddChapterContent.
                            getWindowToken(), 0);
                }
            }
        }, 200);
    }

    // Возвращаемся к главной активти
    private void returnMainActivity() {
        Intent toMainList = new Intent(AddNoteActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMainList);
    }

    // Получаем введенные в EditText данные
    private void getTextFromEditTexts() {
        strChapterTitle = etAddChapterTitle.getText().toString();
        strChapterContent = etAddChapterContent.getText().toString();
    }

    // Убираем фокус
    private void clearFocusEditTexts() {
        etAddChapterTitle.clearFocus();
        etAddChapterContent.clearFocus();
    }
}